package ru.tzhack.facegame.bird.gameobj

import android.graphics.Canvas
import android.graphics.Paint
import ru.tzhack.facegame.bird.Viewport
import ru.tzhack.facegame.bird.gameobj.Bonus.Companion.create
import ru.tzhack.facegame.bird.utils.Position


enum class BonusType {
    SPEED_UP,
    SPEED_DOWN,
    SHOT,
    TIME
}

/**
 * Игровой бонус
 * Реализовать методы
 * 1. [create]
 * 2. [update]
 * 3. [draw]
 * 4. Проверка столкновений
 */
class Bonus(
    private val position: Position,
    private val speed: Float,
    val type: BonusType
) {

    companion object {
        // размер спрайта
        private const val SIDE_SPRITE = 400f / 3.5f

        // диапозон скорости падения бонуса
        private val SPEED_VERTICAL = 100..200

        // генерация
        // горизонтальный отступ от границы экрана
        private const val PADDING_HORIZONTAL = 50
        // растояние между бонусами
        private const val SPACE_Y = 1000

        // создать бонус когда игрок будет находится в координате
        var generateWhenPositionY = 700

        /**
         *  Создать объект
         */
        fun create(): Bonus {
            TODO()
        }
    }

    /**
     *  Обновление позиции
     */
    fun update(dt: Float) {
        position.top -= speed * dt
    }

    /**
     * 1. Отрисовка
     * 2. Проверить находится ли объект на экране в данный момент
     */
    fun draw(canvas: Canvas, paint: Paint, viewport: Viewport) {

    }

}