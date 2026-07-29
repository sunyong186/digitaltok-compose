package com.yourcompany.digitaltok.ui.upload

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.nfc.NfcAdapter
import android.nfc.NfcManager
import android.nfc.Tag
import android.nfc.tech.IsoDep
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.yourcompany.digitaltok.R
import com.yourcompany.digitaltok.databinding.FragmentImagePreviewBinding
import com.yourcompany.digitaltok.ui.MainViewModel
import com.yourcompany.digitaltok.ui.device.NfcDisabledFragment
import com.yourcompany.digitaltok.ui.faq.HelpFragment
import java.io.IOException
import kotlin.math.ceil

class ImagePreviewFragment : Fragment(), NfcAdapter.ReaderCallback {

    private var _binding: FragmentImagePreviewBinding? = null
    private val binding get() = _binding!!

    private val imageViewModel: ImageViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()

    private var loadingDialog: AlertDialog? = null
    private var nfcTransferDialog: AlertDialog? = null
    private var resultDialog: AlertDialog? = null

    private var binaryDataToWrite: ByteArray? = null

    @Volatile
    private var isTransferring = false

    @Volatile
    private var transferCompleted = false

    //  성공 후 2초 뒤 popBackStack 시점에 state saved면 크래시 → onResume에서 처리
    @Volatile
    private var pendingPopBack = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentImagePreviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val imageId = arguments?.getInt(ARG_IMAGE_ID)
        val previewUrl = arguments?.getString(ARG_PREVIEW_URL)

        setupToolbar()
        setupClickListeners(imageId)
        observeViewModel()

        if (previewUrl != null) {
            Glide.with(this)
                .load(previewUrl)
                .placeholder(R.drawable.ic_launcher_background)
                .into(binding.ivPreview)
        } else {
            // 실패 시에도 동일한 실패 팝업(XML) 사용
            showFailDialog("이미지 정보를 불러오는데 실패했습니다.")
        }
    }

    override fun onResume() {
        super.onResume()
        requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        if (pendingPopBack) {
            pendingPopBack = false
            safePopBackStackOrPend()
        }
    }

    override fun onPause() {
        // 전송 중에는 readerMode/dismiss 하지 않음 (끊기면 실패)
        if (!isTransferring) {
            disableNfcReaderMode()
            nfcTransferDialog?.dismiss()
        }
        resultDialog?.dismiss()
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        super.onPause()
    }

    private fun setupToolbar() {
        binding.appBar.titleTextView.text = "사진 업로드"
        binding.appBar.backButton.setOnClickListener { safePopBackStackOrPend() }
    }

    private fun setupClickListeners(imageId: Int?) {
        binding.btnSendToDiring.setOnClickListener {
            //  NFC 꺼짐이면: 머터리얼 알럿 대신 내가 만든 XML(DialogFragment) 띄움
            if (!ensureNfcEnabledOrShowDialog()) return@setOnClickListener

            if (imageId != null) {
                showLoadingDialog("바이너리 데이터 준비 중...")
                imageViewModel.fetchAndDownloadBinary(imageId)
            } else {
                showFailDialog("전송할 이미지 정보가 없습니다.")
            }
        }
    }

    private fun observeViewModel() {
        imageViewModel.binaryData.observe(viewLifecycleOwner) { result ->
            hideLoadingDialog()
            result.onSuccess { data ->
                binaryDataToWrite = data
                transferCompleted = false
                showNfcTransferDialog()
            }.onFailure { e ->
                showFailDialog("오류: ${e.message}")
            }
        }
    }

    // ======================================================
    // NFC / ReaderMode helpers
    // ======================================================
    private fun getNfcAdapter(): NfcAdapter? {
        val nfcManager = context?.getSystemService(Context.NFC_SERVICE) as? NfcManager
        return nfcManager?.defaultAdapter
    }

    /**
     *  "NFC 미지원" 안내는 배제
     * - adapter == null 이어도 '꺼짐'처럼 처리해서 NfcDisabledFragment 띄움
     */
    private fun ensureNfcEnabledOrShowDialog(): Boolean {
        val adapter = getNfcAdapter()
        if (adapter?.isEnabled != true) {
            if (!parentFragmentManager.isStateSaved) {
                NfcDisabledFragment().show(parentFragmentManager, "NfcDisabledDialog")
            }
            return false
        }
        return true
    }

    private fun enableNfcReaderMode() {
        val activity = requireActivity()
        val flags = NfcAdapter.FLAG_READER_NFC_A or
                NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK or
                NfcAdapter.FLAG_READER_NO_PLATFORM_SOUNDS

        getNfcAdapter()?.enableReaderMode(activity, this, flags, null)
    }

    private fun disableNfcReaderMode() {
        try {
            getNfcAdapter()?.disableReaderMode(requireActivity())
        } catch (e: Exception) {
            Log.w(TAG, "Error disabling reader mode", e)
        }
    }

    // ======================================================
    // NFC ReaderCallback (IsoDep 전용)
    // ======================================================
    override fun onTagDiscovered(tag: Tag?) {
        if (tag == null) return

        if (transferCompleted) return

        if (isTransferring) {
            Log.d(TAG, "Already transferring, ignore")
            return
        }
        isTransferring = true

        val data = binaryDataToWrite
        if (data == null) {
            showFailDialog("전송할 데이터가 준비되지 않았습니다.")
            isTransferring = false
            return
        }

        val isoDep = IsoDep.get(tag)
        if (isoDep == null) {
            showFailDialog("IsoDep 태그가 아닙니다.")
            isTransferring = false
            return
        }

        try {
            isoDep.timeout = 120_000
            if (!isoDep.isConnected) isoDep.connect()

            // 0) SELECT
            val selectResp = transceiveHex(isoDep, "00A4040007D2760000850101")
            Log.d(TAG, "SELECT <- ${bytesToHex(selectResp)}")
            if (!endsWith9000(selectResp)) {
                showFailDialog("기기 선택 실패: ${bytesToHex(selectResp)}")
                return
            }

            // 2) 전송 (F0D2)
            val screenIndex = 0
            val ok = writeBinaryByChunks(isoDep, data, screenIndex)
            if (!ok) return

            // 3) Refresh + Poll (F0D4 + F0DE)
            val done = refreshScreenAndWait(isoDep, screenIndex)
            if (!done) {
                showFailDialog("전송은 되었지만 갱신이 완료되지 않았습니다. 다시 시도해주세요.")
                return
            }

            // 4) 성공
            transferCompleted = true
            showSuccessDialogAndExit()
        } catch (e: IOException) {
            Log.e(TAG, "IsoDep IO 실패(태그 떨어짐 가능)", e)
            showFailDialog("태그가 떨어졌어요. 다시 대고 시도해 주세요.")
        } catch (e: Exception) {
            Log.e(TAG, "IsoDep 전송 실패", e)
            showFailDialog("전송 실패: ${e.message}")
        } finally {
            isTransferring = false
            try {
                isoDep.close()
            } catch (e: Exception) {
                Log.e(TAG, "IsoDep close 실패", e)
            }
        }
    }

    /**
     * 데이터 전송: APDU = F0 D2 [screenIndex] [rowIndex] FA [250 bytes]
     */
    private fun writeBinaryByChunks(isoDep: IsoDep, data: ByteArray, screenIndex: Int): Boolean {
        val chunkSize = 0xFA // 250
        val rowCount = ceil(data.size / chunkSize.toDouble()).toInt()
        Log.d(TAG, "payloadBytes=${data.size}, rowCount=$rowCount")

        val padded = if (data.size % chunkSize == 0) {
            data
        } else {
            ByteArray(rowCount * chunkSize).also { out ->
                System.arraycopy(data, 0, out, 0, data.size)
            }
        }

        val apdu = ByteArray(chunkSize + 5)
        apdu[0] = 0xF0.toByte()
        apdu[1] = 0xD2.toByte()
        apdu[2] = screenIndex.toByte()
        apdu[4] = chunkSize.toByte()

        for (rowIndex in 0 until rowCount) {
            apdu[3] = rowIndex.toByte()

            val start = rowIndex * chunkSize
            for (j in 0 until chunkSize) {
                apdu[5 + j] = padded[start + j]
            }

            val resp = isoDep.transceive(apdu)
            val respHex = bytesToHex(resp)
            Log.d(TAG, "D2 row=$rowIndex <- $respHex")

            if (!endsWith9000(resp)) {
                // 실패 시에도 동일한 실패 팝업(XML) 사용
                showFailDialog("전송 오류(row=$rowIndex): $respHex")
                return false
            }
        }
        return true
    }

    private fun refreshScreenAndWait(isoDep: IsoDep, screenIndex: Int): Boolean {
        val refresh = byteArrayOf(
            0xF0.toByte(),
            0xD4.toByte(),
            0x05.toByte(),
            (screenIndex or 0x80).toByte(),
            0x00.toByte()
        )

        val first = isoDep.transceive(refresh)
        Log.d(TAG, "REFRESH <- ${bytesToHex(first)}")

        return pollRefreshResult(isoDep)
    }

    private fun pollRefreshResult(isoDep: IsoDep): Boolean {
        val poll = byteArrayOf(
            0xF0.toByte(),
            0xDE.toByte(),
            0x00.toByte(),
            0x00.toByte(),
            0x01.toByte()
        )

        repeat(1000) { i ->
            val resp = isoDep.transceive(poll)
            val hex = bytesToHex(resp)
            Log.d(TAG, "POLL[$i] <- $hex")

            return@repeat when (hex) {
                "009000", "9000" -> return true
                "019000" -> {
                    Thread.sleep(100)
                    Unit
                }
                "698A", "6986", "68C6" -> return false
                else -> return false
            }
        }
        return false
    }

    // ======================================================
    // Dialogs (원래 방식 유지: MaterialAlertDialogBuilder + setView)
    // - 전송중: dialog_nfc_transfer.xml
    // - 성공: dialog_transfer_success.xml
    // - 실패: dialog_transfer_fail.xml
    // ======================================================
    private fun showNfcTransferDialog() {
        if (nfcTransferDialog == null) {
            val dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_nfc_transfer, null)

            nfcTransferDialog = MaterialAlertDialogBuilder(requireContext())
                .setView(dialogView)
                .setCancelable(true)
                .setOnDismissListener {
                    if (!isTransferring) disableNfcReaderMode()
                }
                .create()
                .apply { window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) }
        }

        nfcTransferDialog?.show()
        enableNfcReaderMode()
    }

    private fun showSuccessDialogAndExit() {
        activity?.runOnUiThread {
            if (!isAdded || activity == null) return@runOnUiThread

            mainViewModel.setLastTransferredImageUrl(arguments?.getString(ARG_PREVIEW_URL))

            nfcTransferDialog?.dismiss()
            if (resultDialog?.isShowing == true) return@runOnUiThread

            val dialogView = layoutInflater.inflate(R.layout.dialog_transfer_success, null)

            resultDialog = MaterialAlertDialogBuilder(requireContext())
                .setView(dialogView)
                .setCancelable(false)
                .create()
                .apply { window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) }

            resultDialog?.show()

            binding.root.postDelayed({
                if (!isAdded) return@postDelayed

                resultDialog?.dismiss()
                disableNfcReaderMode()

                // onSaveInstanceState 이후 popBackStack 크래시 방지
                if (parentFragmentManager.isStateSaved) {
                    pendingPopBack = true
                    Log.w(TAG, "State is saved. Will pop back onResume.")
                    return@postDelayed
                }
                parentFragmentManager.popBackStack()
            }, 2000)
        }
    }

    private fun showFailDialog(message: String?) {
        activity?.runOnUiThread {
            if (!isAdded || activity == null) return@runOnUiThread

            nfcTransferDialog?.dismiss()
            if (resultDialog?.isShowing == true) return@runOnUiThread

            Log.e(TAG, "Transfer failed: $message")

            val dialogView = layoutInflater.inflate(R.layout.dialog_transfer_fail, null)

            // XML이 AppCompatButton이므로 MaterialButton 캐스팅 금지
            val btnRetry = dialogView.findViewById<Button>(R.id.btnRetry)
            val btnSupport = dialogView.findViewById<Button>(R.id.btnSupport)

            resultDialog = MaterialAlertDialogBuilder(requireContext())
                .setView(dialogView)
                .setCancelable(true)
                .create()
                .apply { window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) }

            btnRetry.setOnClickListener {
                resultDialog?.dismiss()
                transferCompleted = false
                showNfcTransferDialog()
            }

            btnSupport.setOnClickListener {
                resultDialog?.dismiss()
                if (!isAdded) return@setOnClickListener

                //  state saved면 트랜잭션 크래시 방지
                if (parentFragmentManager.isStateSaved) {
                    Log.w(TAG, "State is saved. Skip navigation to HelpFragment.")
                    return@setOnClickListener
                }

                val rootView = view ?: return@setOnClickListener
                val parent = rootView.parent as? ViewGroup ?: return@setOnClickListener
                val containerId = parent.id
                if (containerId != View.NO_ID) {
                    parentFragmentManager.beginTransaction()
                        .replace(containerId, HelpFragment())
                        .addToBackStack(null)
                        .commit()
                }
            }

            resultDialog?.show()
        }
    }

    // ======================================================
    // Loading dialog
    // ======================================================
    private fun showLoadingDialog(message: String) {
        if (loadingDialog == null) {
            val progressBar = ProgressBar(requireContext()).apply {
                val padding = 100
                setPadding(padding, padding, padding, padding)
            }
            loadingDialog = MaterialAlertDialogBuilder(requireContext())
                .setTitle(message)
                .setView(progressBar)
                .setCancelable(false)
                .create()
        } else {
            loadingDialog?.setTitle(message)
        }
        loadingDialog?.show()
    }

    private fun hideLoadingDialog() {
        loadingDialog?.dismiss()
        loadingDialog = null
    }

    private fun safePopBackStackOrPend() {
        if (!isAdded) return
        if (parentFragmentManager.isStateSaved) {
            pendingPopBack = true
            return
        }
        parentFragmentManager.popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        nfcTransferDialog = null
        loadingDialog = null
        resultDialog = null
    }

    // ======================================================
    // Utils
    // ======================================================
    private fun transceiveHex(isoDep: IsoDep, hex: String): ByteArray {
        val req = hexToBytes(hex)
        Log.d(TAG, "-> $hex")
        return isoDep.transceive(req)
    }

    private fun endsWith9000(resp: ByteArray): Boolean {
        if (resp.size < 2) return false
        val sw1 = resp[resp.size - 2].toInt() and 0xFF
        val sw2 = resp[resp.size - 1].toInt() and 0xFF
        return sw1 == 0x90 && sw2 == 0x00
    }

    private fun hexToBytes(hex: String): ByteArray {
        val clean = hex.replace(" ", "").trim()
        require(clean.length % 2 == 0) { "Invalid hex length" }
        val out = ByteArray(clean.length / 2)
        for (i in out.indices) {
            val idx = i * 2
            out[i] = clean.substring(idx, idx + 2).toInt(16).toByte()
        }
        return out
    }

    private fun bytesToHex(bytes: ByteArray): String {
        val sb = StringBuilder(bytes.size * 2)
        for (b in bytes) sb.append(String.format("%02X", b))
        return sb.toString()
    }

    companion object {
        private const val TAG = "ImagePreviewFragment"
        private const val ARG_IMAGE_ID = "IMAGE_ID"
        private const val ARG_PREVIEW_URL = "PREVIEW_URL"

        fun newInstance(imageId: Int, previewUrl: String) = ImagePreviewFragment().apply {
            arguments = Bundle().apply {
                putInt(ARG_IMAGE_ID, imageId)
                putString(ARG_PREVIEW_URL, previewUrl)
            }
        }
    }
}
