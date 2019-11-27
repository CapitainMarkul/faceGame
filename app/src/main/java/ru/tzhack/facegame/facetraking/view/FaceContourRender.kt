package ru.tzhack.facegame.facetraking.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.google.firebase.ml.vision.common.FirebaseVisionPoint
import com.otaliastudios.cameraview.size.Size

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

    private val faceContour = mutableListOf<List<FirebaseVisionPoint>>()

    private var rect = Rect()

    private var widthScaleFactor = 1.0F
    private var heightScaleFactor = 1.0F

    private var translateX = 0.0F
    private var translateY = 0.0F

    //960.0 / 1280.0 + 0.5 = 1.25
    fun updateContour(frameSize: Size, faceRect: Rect?, points: List<List<FirebaseVisionPoint>>) {
        frameSize.let {
            heightScaleFactor = if(it.height > height) {
                (height.toFloat() / it.height.toFloat())/* * 0.75F*/
            } else if(it.height < height && it.width != width) {
                (it.width.toFloat() / it.height.toFloat()) + 0.5F
            } else {
                1.0F
            }

            widthScaleFactor = if(it.width > width) {
                (width.toFloat() / it.width.toFloat())/* * 0.75F*/
            } else if (it.width < width && it.height != height) {
                (it.width.toFloat() / it.height.toFloat()) + 0.5F
            } /*else if(it.width == width && it.height > height) {
//                (height.toFloat() / it.height.toFloat())
            }*/ else {
                1.0F
            }
        }

        faceRect?.let {
            rect = it.apply {
                set(
                        (left * widthScaleFactor + translateX).toInt(),
                        (top * heightScaleFactor + translateY).toInt(),
                        (right * widthScaleFactor + translateX).toInt(),
                        (bottom * heightScaleFactor + translateY).toInt()
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
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)

            faceContour.forEachIndexed { index, contour ->
                contour.forEach { point ->
                    canvas.drawCircle(
                            point.x * widthScaleFactor + translateX,
                            point.y * heightScaleFactor + translateY,
                            dotSize,
                            paintWhite
                    )
                }
            }

            canvas.drawRect(rect, paintBox)
        }
    }
}