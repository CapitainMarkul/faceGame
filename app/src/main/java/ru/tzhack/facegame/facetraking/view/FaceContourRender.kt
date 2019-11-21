package ru.tzhack.facegame.facetraking.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.google.firebase.ml.vision.common.FirebaseVisionPoint

/* TODO: Требует доработок */
class FaceContourRender @JvmOverloads constructor(
    context: Context,
    private val attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val dotSize = 3f

    private val paintWhite = Paint().apply {
        color = ContextCompat.getColor(context, android.R.color.white)
    }

    private val paintRoot = Paint().apply {
        color = ContextCompat.getColor(context, android.R.color.holo_red_dark)
    }

    private val paintRoot2 = Paint().apply {
        color = ContextCompat.getColor(context, android.R.color.holo_green_dark)
    }

    private val paintBox = Paint().apply {
        color = ContextCompat.getColor(context, android.R.color.holo_blue_light)
        style = Paint.Style.STROKE
    }

    private val faceLandmark = mutableListOf<FirebaseVisionPoint>()
    private val faceContour = mutableListOf<List<FirebaseVisionPoint>>()

    private var rect = Rect()

    private var widthScaleFactor = 1.0F
    private var heightScaleFactor = 1.0F

    fun updateContour(faceRect: Rect?, points: List<List<FirebaseVisionPoint>>) {
        faceRect?.let {
            rect = it.apply { set(left, top, right, bottom) }
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
                canvas.drawCircle(it.x, it.y, dotSize, paintRoot)
            }

            faceContour.forEachIndexed { index, contour ->
                contour.forEach { point ->
                    canvas.drawCircle(
                        point.x,
                        point.y,
                        dotSize,
                        paintWhite
                    )
                }
            }

            canvas.drawRect(rect, paintBox)
        }
    }
}