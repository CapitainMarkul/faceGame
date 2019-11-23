package ru.tzhack.facegame.bird

import android.content.Context
import android.graphics.*
import ru.tzhack.facegame.R
import kotlin.random.Random


class Wall(
    private val posLeftTube: Position,
    private val posRightTube: Position
) {

    companion object {
        private lateinit var bitmap: Bitmap

        private const val drawOutline = false

        private const val K_SPRITE = 1.5f
        private const val HEIGHT_SPRITE = 276f / K_SPRITE
        private const val WIDTH_SPRITE = 828f / K_SPRITE
        private const val OVERLAY_SPRITE = 120f / K_SPRITE
        private const val COLLISION_ALLOWED_SPRITE = 75f / K_SPRITE

        //генерация
        private const val minWidth = 100f
        private const val rangeRandomWidth = 600
        private const val spaceSize = 300f
        private const val wallsSpacing = 300

        private const val startY = 1000f

        fun generate(context: Context, screenX: Float, size: Int): List<Wall> {
            bitmap = Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(
                    context.resources,
                    R.drawable.pad1
                ),
                WIDTH_SPRITE.toInt(),
                HEIGHT_SPRITE.toInt(),
                false
            )

            val walls = ArrayList<Wall>()
            var y = startY
            for (i in 0 until size) {
                val leftPos = 0f
                val leftWidth = minWidth + Random.Default.nextInt(rangeRandomWidth)

                val rightPos = leftPos + leftWidth + spaceSize
                val rightWidth = screenX - rightPos

                walls += Wall(
                    posLeftTube = Position(leftPos, y, leftWidth, HEIGHT_SPRITE),
                    posRightTube = Position(rightPos, y, rightWidth, HEIGHT_SPRITE)
                )

                y += wallsSpacing + HEIGHT_SPRITE
            }

            return walls
        }
    }

    fun collision(position: Position): Boolean {
        if (posLeftTube.contains(position, COLLISION_ALLOWED_SPRITE) ||
            posRightTube.contains(position, COLLISION_ALLOWED_SPRITE)
        ) {
            return true
        }

        return false
    }

    fun draw(canvas: Canvas, paint: Paint, viewport: Viewport) {
        if (!viewport.nowOnScreen(posLeftTube)) {
            return
        }

        val widthSpriteWithOverlay = WIDTH_SPRITE - OVERLAY_SPRITE

        var x = posLeftTube.right - WIDTH_SPRITE
        while (x > posLeftTube.left - widthSpriteWithOverlay) {
            canvas.drawBitmap(
                bitmap,
                x,
                viewport.worldToScreenPoint(posLeftTube.top),
                paint
            )
            x -= widthSpriteWithOverlay
        }

        x = posRightTube.right - posRightTube.width % widthSpriteWithOverlay
        while (x >= posRightTube.left) {
            canvas.drawBitmap(
                bitmap,
                x,
                viewport.worldToScreenPoint(posRightTube.top),
                paint
            )
            x -= widthSpriteWithOverlay
        }

        if (drawOutline) {
            paint.style = Paint.Style.STROKE
            paint.color = Color.BLACK
            canvas.drawRect(
                posLeftTube.left,
                viewport.worldToScreenPoint(posLeftTube.top - COLLISION_ALLOWED_SPRITE),
                posLeftTube.right,
                viewport.worldToScreenPoint(posLeftTube.bottom + COLLISION_ALLOWED_SPRITE),
                paint
            )
            canvas.drawRect(
                posRightTube.left,
                viewport.worldToScreenPoint(posRightTube.top - COLLISION_ALLOWED_SPRITE),
                posRightTube.right,
                viewport.worldToScreenPoint(posRightTube.bottom + COLLISION_ALLOWED_SPRITE),
                paint
            )
        }
    }

    fun getTop(): Float {
        return posLeftTube.top
    }
}