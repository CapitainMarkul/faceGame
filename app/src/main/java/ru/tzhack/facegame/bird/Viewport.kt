package ru.tzhack.facegame.bird

/**
 *  Управление игровой областью
 *
 *  Реализовать методы
 *  1. Перевод глобальных координат в координаты дисплея
 *  2. Центрирование на объекте пользователя
 *  3. Проверка попадает ли игровой объект в область дисплея
 */
class Viewport(private val screenY: Float) {

    companion object {
        // отступ снизу при центрировании на игровом объекта
        private const val BOTTOM_PADDING = 300f
    }
}