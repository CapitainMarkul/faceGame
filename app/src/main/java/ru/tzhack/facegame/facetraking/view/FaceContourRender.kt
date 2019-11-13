package ru.tzhack.facegame.facetraking.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.google.firebase.ml.vision.common.FirebaseVisionPoint

class FaceContourRender @JvmOverloads constructor(
    context: Context,
    private val attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val dotSize = 4f

    private val paintRoot = Paint().apply {
        color = context.getColor(android.R.color.holo_red_dark)
    }

    private val paintRoot2 = Paint().apply {
        color = context.getColor(android.R.color.holo_green_dark)
    }

    private val paintBox = Paint().apply {
        color = context.getColor(android.R.color.holo_green_dark)
        style = Paint.Style.STROKE
    }

    private val faceLandmark = mutableListOf<FirebaseVisionPoint>()
    private val faceContour = mutableListOf<List<FirebaseVisionPoint>>()

    private var rect = Rect()

    private var widthScaleFactor = 1.0F
    private var heightScaleFactor = 1.0F

    fun updateContour(faceRect: Rect?, points: List<List<FirebaseVisionPoint>>) {
        faceRect?.let {
            rect = it.apply {
                set(
                    left.translateX().toInt(),
                    top.translateY().toInt(),
                    right.translateX().toInt(),
                    bottom.translateY().toInt()
                )
            }
        }

        faceContour.clear()
        faceContour.addAll(points)

        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (this.width != 0 && this.height != 0) {
            widthScaleFactor = (canvas.width / this.width).toFloat()
            heightScaleFactor = (canvas.height / this.height).toFloat()

            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)

            faceLandmark.forEach {
                canvas.drawCircle(it.x.translateX(), it.y.translateY(), dotSize, paintRoot)
            }

            faceContour.forEachIndexed { index, contour ->
                contour.forEach { point ->
                    canvas.drawCircle(
                        point.x.translateX(),
                        point.y.translateY(),
                        dotSize,
                        if (index > 1) paintRoot else paintRoot2
                    )
                }
            }

            canvas.drawRect(rect, paintBox)
        }
    }

    private fun Float.translateX(): Float = width - scaleX()
    private fun Float.scaleX(): Float = this * widthScaleFactor
    private fun Float.translateY(): Float = this * heightScaleFactor

    private fun Int.translateX(): Float = width - scaleX()
    private fun Int.scaleX(): Float = this * widthScaleFactor
    private fun Int.translateY(): Float = this * heightScaleFactor
}