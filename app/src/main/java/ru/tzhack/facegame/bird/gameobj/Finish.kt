package ru.tzhack.facegame.bird.gameobj

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import ru.tzhack.facegame.bird.Viewport

/**
 * Финишная линия
 * Реализовать методы
 * 1. [draw]
 */
class Finish(
    positionY: Float,
    width: Float,
    context: Context
) {

    // размер спрайта
    private val sideSprite = 300f / 4f

    /**
     * 1. Отрисовка
     * 2. Проверить находится ли объект на экране в данный момент
     */
    fun draw(canvas: Canvas, paint: Paint, viewport: Viewport) {

    }
}