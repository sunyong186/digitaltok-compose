package com.yourcompany.digitaltok.ui.decorate

import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.RectF
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.yourcompany.digitaltok.databinding.FragmentCropImageBinding
import com.yourcompany.digitaltok.ui.MainViewModel
import java.io.File
import java.io.FileOutputStream

class CropImageFragment : Fragment() {

    private var _binding: FragmentCropImageBinding? = null
    private val binding get() = _binding!!

    private val mainViewModel: MainViewModel by activityViewModels()

    private val imageUri: Uri by lazy {
        requireArguments().getString(ARG_IMAGE_URI)!!.toUri()
    }

    override fun onResume() {
        super.onResume()
        mainViewModel.setBottomNavVisibility(false)
    }

    override fun onPause() {
        super.onPause()
        mainViewModel.setBottomNavVisibility(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCropImageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.overlay.setGestureDelegate(binding.photoView)

        Glide.with(this)
            .asBitmap()
            .load(imageUri)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    binding.photoView.setImageBitmap(resource)
                }
                override fun onLoadCleared(placeholder: android.graphics.drawable.Drawable?) {}
            })

        binding.btnUse.setOnClickListener {
            val frame = binding.overlay.getFrameRectPx()
            val cropped = cropFromPhotoView(frame, 200)

            if (cropped == null) {
                Toast.makeText(requireContext(), "크롭 실패", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val uri = saveBitmap(cropped)
            setFragmentResult(REQUEST_KEY, bundleOf(RESULT_CROPPED_URI to uri.toString()))
            parentFragmentManager.popBackStack()
        }

        binding.btnRecrop.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun cropFromPhotoView(frame: RectF, size: Int): Bitmap? {
        val drawable = binding.photoView.drawable as? BitmapDrawable ?: return null
        val bitmap = drawable.bitmap

        val matrix = Matrix()
        binding.photoView.getDisplayMatrix(matrix)

        val inverse = Matrix()
        matrix.invert(inverse)

        val bitmapRect = RectF(frame)
        inverse.mapRect(bitmapRect)

        val left = bitmapRect.left.coerceAtLeast(0f)
        val top = bitmapRect.top.coerceAtLeast(0f)
        val right = bitmapRect.right.coerceAtMost(bitmap.width.toFloat())
        val bottom = bitmapRect.bottom.coerceAtMost(bitmap.height.toFloat())

        val w = (right - left).toInt()
        val h = (bottom - top).toInt()
        if (w <= 0 || h <= 0) return null

        val cropped = Bitmap.createBitmap(bitmap, left.toInt(), top.toInt(), w, h)
        return Bitmap.createScaledBitmap(cropped, size, size, true)
    }

    private fun saveBitmap(bitmap: Bitmap): Uri {
        val dir = File(requireContext().cacheDir, "crop")
        dir.mkdirs()
        val file = File(dir, "crop_${System.currentTimeMillis()}.png")
        FileOutputStream(file).use {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
        }
        return Uri.fromFile(file)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val REQUEST_KEY = "CropImageFragment_REQUEST_KEY"
        const val RESULT_CROPPED_URI = "RESULT_CROPPED_URI"
        private const val ARG_IMAGE_URI = "ARG_IMAGE_URI"

        fun newInstance(imageUri: Uri) =
            CropImageFragment().apply {
                arguments = bundleOf(ARG_IMAGE_URI to imageUri.toString())
            }
    }
}
