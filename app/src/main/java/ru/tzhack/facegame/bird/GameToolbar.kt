package ru.tzhack.facegame.bird

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint

class GameToolbar {

    var time = 50f

    fun update(dt: Float) {
        time -= dt
    }

    fun draw(canvas: Canvas, paint: Paint) {
        paint.color = Color.RED
        paint.style = Paint.Style.FILL
        paint.textSize = 100f
        canvas.drawText(time.toInt().toString(), 40f, 100f, paint)
    }

    fun addTime() {
        time += 5
    }
}