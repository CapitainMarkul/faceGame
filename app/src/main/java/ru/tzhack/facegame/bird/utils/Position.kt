package ru.tzhack.facegame.bird.utils

/**
 *  Позиция и размер игрового объекта
 */
data class Position(
    var left: Float,
    var top: Float,
    val width: Float,
    val height: Float
) {

    val right: Float
        get() = left + width

    val bottom: Float
        get() = top - height

    fun contains(pos: Position): Boolean {
        return pos.right >= left && pos.left < right && pos.top > bottom && pos.bottom < top
    }

    fun containsY(pos: Position): Boolean {
        return pos.top > bottom && pos.bottom < top
    }
}