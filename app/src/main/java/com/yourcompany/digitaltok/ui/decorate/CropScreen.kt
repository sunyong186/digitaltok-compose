package com.yourcompany.digitaltok.ui.decorate

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.RectF
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.FrameLayout
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.github.chrisbanes.photoview.PhotoView
import java.io.File
import java.io.FileOutputStream

@Composable
fun CropScreen(
    imageUri: Uri,
    onCropSuccess: (Uri) -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current
    var photoViewRef by remember { mutableStateOf<PhotoView?>(null) }
    var overlayViewRef by remember { mutableStateOf<CropOverlayView?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Top Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "이미지 자르기",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        // Main Crop Container (PhotoView + CropOverlayView)
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            AndroidView(
                factory = { ctx ->
                    val frameLayout = FrameLayout(ctx).apply {
                        layoutParams = FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.MATCH_PARENT,
                            FrameLayout.LayoutParams.MATCH_PARENT
                        )
                    }

                    val photoView = PhotoView(ctx).apply {
                        layoutParams = FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.MATCH_PARENT,
                            FrameLayout.LayoutParams.MATCH_PARENT
                        )
                        scaleType = android.widget.ImageView.ScaleType.CENTER_INSIDE
                    }

                    val overlayView = CropOverlayView(ctx).apply {
                        layoutParams = FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.MATCH_PARENT,
                            FrameLayout.LayoutParams.MATCH_PARENT
                        )
                    }

                    overlayView.setGestureDelegate(photoView)

                    frameLayout.addView(photoView)
                    frameLayout.addView(overlayView)

                    photoViewRef = photoView
                    overlayViewRef = overlayView

                    Glide.with(ctx)
                        .asBitmap()
                        .load(imageUri)
                        .into(object : CustomTarget<Bitmap>() {
                            override fun onResourceReady(
                                resource: Bitmap,
                                transition: Transition<in Bitmap>?
                            ) {
                                photoView.setImageBitmap(resource)
                            }

                            override fun onLoadCleared(placeholder: Drawable?) {}
                        })

                    frameLayout
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        // Bottom Action Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onCancel,
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF333333)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "다시 선택",
                    fontSize = 16.sp,
                    color = Color.White
                )
            }

            Button(
                onClick = {
                    val pv = photoViewRef
                    val ov = overlayViewRef
                    if (pv != null && ov != null) {
                        val croppedBitmap = cropFromPhotoView(pv, ov.getFrameRectPx(), 200)
                        if (croppedBitmap != null) {
                            val savedUri = saveBitmapToCache(context, croppedBitmap)
                            onCropSuccess(savedUri)
                        } else {
                            Toast.makeText(context, "이미지 자르기에 실패했습니다.", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = DecorateColors.PointBlue
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "사용하기",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

private fun cropFromPhotoView(photoView: PhotoView, frameRect: RectF, size: Int): Bitmap? {
    val drawable = photoView.drawable as? BitmapDrawable ?: return null
    val bitmap = drawable.bitmap

    val matrix = Matrix()
    photoView.getDisplayMatrix(matrix)

    val inverse = Matrix()
    matrix.invert(inverse)

    val bitmapRect = RectF(frameRect)
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

private fun saveBitmapToCache(context: Context, bitmap: Bitmap): Uri {
    val dir = File(context.cacheDir, "crop")
    if (!dir.exists()) dir.mkdirs()
    val file = File(dir, "crop_${System.currentTimeMillis()}.png")
    FileOutputStream(file).use { out ->
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
    }
    return Uri.fromFile(file)
}
