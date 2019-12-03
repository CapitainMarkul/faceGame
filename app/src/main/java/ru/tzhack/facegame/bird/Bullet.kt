package ru.tzhack.facegame.bird

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import ru.tzhack.facegame.R

class Bullet(val position: Position) {

    var crashedMode = false
    private val startTopPosition = position.top

    companion object {
        const val shotDebounce = 2000

        private const val SPEED = 600
        private const val MAX_DISTANCE = 600

        private const val K_SPRITE = 2f
        private const val widthFly = 89f / K_SPRITE
        private const val heightFly = 360f / K_SPRITE
        private lateinit var bitmapFly: Bitmap
        private const val sideCrashed = 224f
        private lateinit var bitmapCrashed: Bitmap

        fun init(context: Context) {
            bitmapFly = context.createBitmap(R.drawable.bullet_flies, widthFly, heightFly)
            bitmapCrashed = context.createBitmap(R.drawable.bullet_crashed, sideCrashed, sideCrashed)
        }

        fun create(birdPosition: Position): Bullet {
            return Bullet(
                Position(
                    left = birdPosition.left - widthFly / 2 + birdPosition.width / 2,
                    top = birdPosition.top + heightFly,
                    width = widthFly,
                    height = heightFly
                )
            )
        }
    }

    fun isDistanceOver(): Boolean {
        return position.top - startTopPosition >= MAX_DISTANCE
    }

    fun update(dt: Float) {
        position.top += SPEED * dt
    }

    fun draw(canvas: Canvas, paint: Paint, viewport: Viewport) {
        canvas.drawBitmap(
            if (crashedMode) bitmapCrashed else bitmapFly,
            position.left,
            viewport.worldToScreenPoint(position.top),
            paint
        )
    }
}