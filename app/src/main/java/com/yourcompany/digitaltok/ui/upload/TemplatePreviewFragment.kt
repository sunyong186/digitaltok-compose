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
import com.yourcompany.digitaltok.databinding.FragmentTemplatePreviewBinding
import com.yourcompany.digitaltok.ui.MainViewModel
import com.yourcompany.digitaltok.ui.device.NfcDisabledFragment
import com.yourcompany.digitaltok.ui.faq.HelpFragment
import java.io.IOException
import kotlin.math.ceil

class TemplatePreviewFragment : Fragment(), NfcAdapter.ReaderCallback {

    private var _binding: FragmentTemplatePreviewBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TemplatePreviewViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()

    private var loadingDialog: AlertDialog? = null
    private var nfcTransferDialog: AlertDialog? = null
    private var resultDialog: AlertDialog? = null

    private var binaryDataToWrite: ByteArray? = null

    @Volatile
    private var isTransferring = false

    @Volatile
    private var transferCompleted = false

    @Volatile
    private var pendingPopBack = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTemplatePreviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val templateImageUrl = arguments?.getString(ARG_TEMPLATE_IMAGE_URL)
        val templateDataUrl = arguments?.getString(ARG_TEMPLATE_DATA_URL)

        setupToolbar()
        setupClickListeners(templateDataUrl)
        observeViewModel()

        if (templateImageUrl != null) {
            Glide.with(this)
                .load(templateImageUrl)
                .placeholder(R.drawable.blank_img)
                .error(R.drawable.blank_img)
                .into(binding.ivTemplatePreview)
        } else {
            showFailDialog("템플릿 정보를 불러오는데 실패했습니다.")
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
        if (!isTransferring) {
            disableNfcReaderMode()
            nfcTransferDialog?.dismiss()
        }
        resultDialog?.dismiss()
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        super.onPause()
    }

    private fun setupToolbar() {
        binding.templateAppBar.titleTextView.text = "템플릿 업로드"
        binding.templateAppBar.backButton.setOnClickListener { safePopBackStackOrPend() }
    }

    private fun setupClickListeners(templateDataUrl: String?) {
        binding.btnTemplateSendToDiring.setOnClickListener {
            if (!ensureNfcEnabledOrShowDialog()) return@setOnClickListener

            if (templateDataUrl != null) {
                showLoadingDialog("바이너리 데이터 준비 중...")
                viewModel.downloadTemplateBinary(templateDataUrl)
            } else {
                showFailDialog("전송할 템플릿 정보가 없습니다.")
            }
        }
    }

    private fun observeViewModel() {
        viewModel.binaryData.observe(viewLifecycleOwner) { result ->
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

    private fun getNfcAdapter(): NfcAdapter? {
        val nfcManager = context?.getSystemService(Context.NFC_SERVICE) as? NfcManager
        return nfcManager?.defaultAdapter
    }

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

    override fun onTagDiscovered(tag: Tag?) {
        if (tag == null || transferCompleted || isTransferring) return
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

            val selectResp = transceiveHex(isoDep, "00A4040007D2760000850101")
            if (!endsWith9000(selectResp)) {
                showFailDialog("기기 선택 실패: ${bytesToHex(selectResp)}")
                return
            }

            if (!writeBinaryByChunks(isoDep, data, 0)) return
            if (!refreshScreenAndWait(isoDep, 0)) {
                showFailDialog("전송은 되었지만 갱신이 완료되지 않았습니다. 다시 시도해주세요.")
                return
            }

            transferCompleted = true
            showSuccessDialogAndExit()
        } catch (e: IOException) {
            showFailDialog("태그가 떨어졌어요. 다시 대고 시도해 주세요.")
        } catch (e: Exception) {
            showFailDialog("전송 실패: ${e.message}")
        } finally {
            isTransferring = false
            try { isoDep.close() } catch (e: Exception) { /* no-op */ }
        }
    }

    private fun writeBinaryByChunks(isoDep: IsoDep, data: ByteArray, screenIndex: Int): Boolean {
        val chunkSize = 250
        val rowCount = ceil(data.size / chunkSize.toDouble()).toInt()
        val padded = data.copyOf(rowCount * chunkSize)

        val apdu = ByteArray(chunkSize + 5).apply {
            this[0] = 0xF0.toByte()
            this[1] = 0xD2.toByte()
            this[2] = screenIndex.toByte()
            this[4] = chunkSize.toByte()
        }

        for (rowIndex in 0 until rowCount) {
            apdu[3] = rowIndex.toByte()
            System.arraycopy(padded, rowIndex * chunkSize, apdu, 5, chunkSize)
            val resp = isoDep.transceive(apdu)
            if (!endsWith9000(resp)) {
                showFailDialog("전송 오류(row=$rowIndex): ${bytesToHex(resp)}")
                return false
            }
        }
        return true
    }

    private fun refreshScreenAndWait(isoDep: IsoDep, screenIndex: Int): Boolean {
        val refresh = byteArrayOf(0xF0.toByte(), 0xD4.toByte(), 0x05.toByte(), (screenIndex or 0x80).toByte(), 0x00.toByte())
        isoDep.transceive(refresh)
        return pollRefreshResult(isoDep)
    }

    private fun pollRefreshResult(isoDep: IsoDep): Boolean {
        val poll = byteArrayOf(0xF0.toByte(), 0xDE.toByte(), 0x00.toByte(), 0x00.toByte(), 0x01.toByte())
        repeat(1000) {
            try {
                val resp = isoDep.transceive(poll)
                 when (bytesToHex(resp)) {
                    "009000", "9000" -> return true
                    "019000" -> Thread.sleep(100)
                    else -> return false
                }
            } catch (e: IOException) {
                Log.e(TAG, "Polling failed", e)
                return false
            }
        }
        return false
    }

    private fun showNfcTransferDialog() {
        if (nfcTransferDialog == null) {
            val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_nfc_transfer, null)
            nfcTransferDialog = MaterialAlertDialogBuilder(requireContext())
                .setView(dialogView)
                .setCancelable(true)
                .setOnDismissListener { if (!isTransferring) disableNfcReaderMode() }
                .create()
                .apply { window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) }
        }
        nfcTransferDialog?.show()
        enableNfcReaderMode()
    }

    private fun showSuccessDialogAndExit() {
        activity?.runOnUiThread {
            if (!isAdded || activity == null) return@runOnUiThread

            mainViewModel.setLastTransferredImageUrl(arguments?.getString(ARG_TEMPLATE_IMAGE_URL))

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

                if (parentFragmentManager.isStateSaved) {
                    pendingPopBack = true
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
        if (isStateSaved) {
            pendingPopBack = true
        } else {
            parentFragmentManager.popBackStack()
        }
    }

    private fun transceiveHex(isoDep: IsoDep, hex: String): ByteArray {
        val req = hexToBytes(hex)
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
        return bytes.joinToString("") { "%02X".format(it) }
    }

    companion object {
        private const val TAG = "TemplatePreviewFragment"
        private const val ARG_TEMPLATE_NAME = "arg_template_name"
        private const val ARG_TEMPLATE_IMAGE_URL = "arg_template_image_url"
        private const val ARG_TEMPLATE_DATA_URL = "arg_template_data_url"

        fun newInstance(name: String, imageUrl: String, dataUrl: String) = TemplatePreviewFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_TEMPLATE_NAME, name)
                putString(ARG_TEMPLATE_IMAGE_URL, imageUrl)
                putString(ARG_TEMPLATE_DATA_URL, dataUrl)
            }
        }
    }
}
