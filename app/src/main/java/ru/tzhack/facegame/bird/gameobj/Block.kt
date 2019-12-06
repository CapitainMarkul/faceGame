package ru.tzhack.facegame.bird.gameobj

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import ru.tzhack.facegame.bird.Viewport
import ru.tzhack.facegame.bird.gameobj.Block.Companion.generate

/**
 * Препядствие
 *
 * Реализовать методы
 * 1. [generate]
 * 2. [draw]
 * 3. Проверка столкновений
 */
class Block() {

    companion object {
        // размер спрайта
        private const val K_SPRITE = 1.5f
        private const val HEIGHT_SPRITE = 276f / K_SPRITE
        private const val WIDTH_SPRITE = 828f / K_SPRITE
        private const val OVERLAY_HORIZONTAL_SPRITE = 120f / K_SPRITE
        private const val COLLISION_ALLOWED_SPRITE = 75f / K_SPRITE

        // генерация
        // минимальный размер блока
        private const val minWidth = 100f
        // максимальный разброс ширины
        private const val rangeRandomWidth = 600
        // растояние между блоками по горизонтали
        private const val spaceSize = 300f
        // растояние между блоками по вертикали
        private const val wallsSpacing = 300
        // координата первого блока
        private const val startY = 1000f

        /**
         *  Генерация блоков
         */
        fun generate(context: Context, screenX: Float, count: Int): List<Block> {
            val walls = ArrayList<Block>()

            return walls
        }
    }

    /**
     * 1. Отрисовка
     * 2. Проверить находится ли объект на экране в данный момент
     */
    fun draw(canvas: Canvas, paint: Paint, viewport: Viewport) {

    }
}