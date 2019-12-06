package ru.tzhack.facegame.bird.gameobj

import android.graphics.Canvas
import android.graphics.Paint
import ru.tzhack.facegame.bird.Viewport
import ru.tzhack.facegame.bird.gameobj.Bullet.Companion.create
import ru.tzhack.facegame.bird.utils.Position

/**
 *  Выстрел
 * Реализовать методы
 * 1. [create]
 * 2. [draw]
 * 3. Метод для установки состояния "столкнулся"
 * 4. [update]
 * 5. Утилизация объекта после перемещения на максимальную дистанцию
 */
class Bullet() {

    companion object {

        // размер спрайта
        private const val K_SPRITE = 2f
        private const val widthFly = 89f / K_SPRITE
        private const val heightFly = 360f / K_SPRITE
        private const val sideCrashed = 224f
        private const val CRASHED_VISIBLE_MAX_TIME = 0.5f

        // характеристики объекта
        private const val SPEED = 600
        private const val MAX_DISTANCE = 600

        /**
         *  Создание объекта
         */
        fun create(birdPosition: Position): Bullet {
            TODO()
        }
    }

    /**
     *  Смещение позиции если ещё нет столкновения
     */
    fun update(dt: Float) {

    }

    /**
     * 1. Отрисовка
     */
    fun draw(canvas: Canvas, paint: Paint, viewport: Viewport) {

    }
}