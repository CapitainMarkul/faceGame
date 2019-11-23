package ru.tzhack.facegame.bird

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import ru.tzhack.facegame.R

class Player(
    context: Context,
    private val screenX: Float
) {
    companion object {

        private const val K_SPRITE = 4f
        private const val WIDTH_SPRITE = 632f / K_SPRITE
        private const val HEIGHT_SPRITE = 716f / K_SPRITE

        private const val SPEED_VERTICAL = 200
        private const val MAX_SPEED_HORIZONTAL = 500
    }

    private var movement: Movement = Movement.Stopped

    var crashed = false

    val position = Position(
        left = screenX / 2f - WIDTH_SPRITE / 2f,
        top = 0f,
        width = WIDTH_SPRITE,
        height = HEIGHT_SPRITE
    )

    private val spriteAnimation = SpriteAnimation(
        arrayOf(
            Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(
                    context.resources,
                    R.drawable.frame1
                ),
                position.width.toInt(),
                position.height.toInt(),
                false
            ),
            Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(
                    context.resources,
                    R.drawable.frame2
                ),
                position.width.toInt(),
                position.height.toInt(),
                false
            )
        ),
        0.4f
    )

    fun update(dt: Float) {
        spriteAnimation.update(dt)

        if (!crashed) {
            position.top += SPEED_VERTICAL * dt
        }

        when (val state = movement) {
            is Movement.Left -> {
                if (position.left > 0) {
                    position.left -= MAX_SPEED_HORIZONTAL * dt * state.speedRatio
                }
            }
            is Movement.Right -> {
                if (position.left < screenX - position.width) {
                    position.left += MAX_SPEED_HORIZONTAL * dt * state.speedRatio
                }
            }
        }
    }

    fun draw(canvas: Canvas, paint: Paint, viewport: Viewport) {
        canvas.drawBitmap(
            spriteAnimation.getFrame(),
            position.left,
            viewport.worldToScreenPoint(position.top),
            paint
        )
    }

    fun setMovementState(movement: Movement) {
        this.movement = movement
    }

    fun getY(): Float {
        return position.top
    }

}