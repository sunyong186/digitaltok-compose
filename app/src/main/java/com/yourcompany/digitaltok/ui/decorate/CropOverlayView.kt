package com.yourcompany.digitaltok.ui.decorate

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class CropOverlayView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private val dimPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0x99000000.toInt()
        style = Paint.Style.FILL
    }

    private val clearPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }

    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = dp(2f)
    }

    private val frameSizePx = dp(200f)
    private val frameRect = RectF()

    private var dragging = false
    private var lastX = 0f
    private var lastY = 0f

    private var gestureDelegate: View? = null
    fun setGestureDelegate(view: View) {
        gestureDelegate = view
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        val left = (w - frameSizePx) / 2f
        val top = (h - frameSizePx) / 2f
        frameRect.set(left, top, left + frameSizePx, top + frameSizePx)
    }

    fun getFrameRectPx(): RectF = RectF(frameRect)

    override fun onDraw(canvas: Canvas) {
        val sc = canvas.saveLayer(0f, 0f, width.toFloat(), height.toFloat(), null)
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), dimPaint)
        canvas.drawRect(frameRect, clearPaint)
        canvas.drawRect(frameRect, borderPaint)
        canvas.restoreToCount(sc)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {

        // 핀치 줌은 어디서든 사진으로
        if (event.pointerCount > 1) {
            dragging = false
            gestureDelegate?.dispatchTouchEvent(event)
            return true
        }

        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                lastX = event.x
                lastY = event.y

                if (frameRect.contains(event.x, event.y)) {
                    dragging = true
                    return true
                }
                return gestureDelegate?.dispatchTouchEvent(event) ?: false
            }

            MotionEvent.ACTION_MOVE -> {
                if (dragging) {
                    frameRect.offset(event.x - lastX, event.y - lastY)
                    clampFrame()
                    lastX = event.x
                    lastY = event.y
                    invalidate()
                    return true
                }
                return gestureDelegate?.dispatchTouchEvent(event) ?: false
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                dragging = false
                return true
            }
        }
        return false
    }

    private fun clampFrame() {
        val dx = when {
            frameRect.left < 0 -> -frameRect.left
            frameRect.right > width -> width - frameRect.right
            else -> 0f
        }
        val dy = when {
            frameRect.top < 0 -> -frameRect.top
            frameRect.bottom > height -> height - frameRect.bottom
            else -> 0f
        }
        frameRect.offset(dx, dy)
    }

    private fun dp(v: Float): Float = v * resources.displayMetrics.density
}
