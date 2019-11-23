package ru.tzhack.facegame.bird

class Position(
    var left: Float,
    var top: Float,
    val width: Float,
    val height: Float
) {

    val right: Float
        get() = left + width

    val bottom: Float
        get() = top - height

    fun contains(pos: Position, allowedY: Float): Boolean {
        return pos.right >= left && pos.left < right && pos.top - allowedY > bottom && pos.bottom + allowedY < top
    }
}