package com.example.sibsiumap

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout

class ZoomableFrameLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var scaleFactor = 1.0f
    private var minScale = 1.0f
    private var maxScale = 5.0f

    private var posX = 0f
    private var posY = 0f
    private var lastTouchX = 0f
    private var lastTouchY = 0f
    private var activePointerId = INVALID_POINTER_ID

    // Точка фокуса для зума
    private var pivotXPoint = 0f
    private var pivotYPoint = 0f

    companion object {
        private const val INVALID_POINTER_ID = -1
    }

    private val scaleDetector = ScaleGestureDetector(context, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            scaleFactor *= detector.scaleFactor
            scaleFactor = scaleFactor.coerceIn(minScale, maxScale)
            pivotXPoint = detector.focusX
            pivotYPoint = detector.focusY
            invalidate()
            return true
        }
    })

    // Метод для приближения (например, на 50%)
    fun zoomIn(step: Float = 0.5f) {
        val targetScale = (scaleFactor + step).coerceIn(minScale, maxScale)
        animateZoom(targetScale)
    }

    // Метод для отдаления
    fun zoomOut(step: Float = 0.5f) {
        val targetScale = (scaleFactor - step).coerceIn(minScale, maxScale)
        animateZoom(targetScale)
    }

    // Сброс масштаба и позиции в исходное состояние
    fun resetZoom() {
        posX = 0f
        posY = 0f
        animateZoom(minScale)
    }

    // Плавная анимация изменения масштаба от центра View
    private fun animateZoom(targetScale: Float) {
        pivotXPoint = width / 2f
        pivotYPoint = height / 2f

        val animator = ValueAnimator.ofFloat(scaleFactor, targetScale)
        animator.duration = 250 // длительность анимации в мс
        animator.interpolator = DecelerateInterpolator()
        animator.addUpdateListener { animation ->
            scaleFactor = animation.animatedValue as Float
            if (scaleFactor == minScale) {
                posX = 0f
                posY = 0f
            }
            invalidate()
        }
        animator.start()
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        scaleDetector.onTouchEvent(ev)
        return super.onInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        scaleDetector.onTouchEvent(ev)

        when (ev.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                val pointerIndex = ev.actionIndex
                lastTouchX = ev.getX(pointerIndex)
                lastTouchY = ev.getY(pointerIndex)
                activePointerId = ev.getPointerId(0)
            }
            MotionEvent.ACTION_MOVE -> {
                val pointerIndex = ev.findPointerIndex(activePointerId)
                if (pointerIndex != -1) {
                    val x = ev.getX(pointerIndex)
                    val y = ev.getY(pointerIndex)

                    if (!scaleDetector.isInProgress) {
                        posX += x - lastTouchX
                        posY += y - lastTouchY
                        invalidate()
                    }
                    lastTouchX = x
                    lastTouchY = y
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                activePointerId = INVALID_POINTER_ID
            }
            MotionEvent.ACTION_POINTER_UP -> {
                val pointerIndex = ev.actionIndex
                val pointerId = ev.getPointerId(pointerIndex)
                if (pointerId == activePointerId) {
                    val newPointerIndex = if (pointerIndex == 0) 1 else 0
                    lastTouchX = ev.getX(newPointerIndex)
                    lastTouchY = ev.getY(newPointerIndex)
                    activePointerId = ev.getPointerId(newPointerIndex)
                }
            }
        }
        return true
    }

    override fun dispatchDraw(canvas: Canvas) {
        canvas.save()
        canvas.translate(posX, posY)
        canvas.scale(scaleFactor, scaleFactor, pivotXPoint, pivotYPoint)
        super.dispatchDraw(canvas)
        canvas.restore()
    }
}
