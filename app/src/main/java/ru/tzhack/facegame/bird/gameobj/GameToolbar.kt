package ru.tzhack.facegame.bird.gameobj

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint

/**
 *  Игровая информация:
 *  - Таймер
 *  - Кол-во выстрелов
 *  Реализовать методы
 *  1. [update]
 *  2. [draw]
 *  3. Метод для увелечения таймера
 */
class GameToolbar(context: Context) {

    // размер спрайта выстрела
    private val sideSprite = 90f
    // увелечение таймера при поднятии бонуса
    private val bonusTime = 5f

    /**
     * Обновление таймера
     */
    fun update(dt: Float) {

    }

    /**
     *  1. Отрисовка таймера
     *  2. кол-во выстрелов, иконку выстрела
     */
    fun draw(canvas: Canvas, paint: Paint) {

    }
}