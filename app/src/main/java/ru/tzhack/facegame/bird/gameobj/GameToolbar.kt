package ru.tzhack.facegame.bird.gameobj

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import ru.tzhack.facegame.R
import ru.tzhack.facegame.bird.utils.createBitmap


class GameToolbar(context: Context) {

    private val sideSprite = 90f
    var time = 60f
    var shotCount = 2

    private val shotBitmap = context.createBitmap(R.drawable.bonus_shot, sideSprite, sideSprite)

    fun update(dt: Float) {
        time -= dt
    }

    fun draw(canvas: Canvas, paint: Paint) {
        paint.color = Color.RED
        paint.style = Paint.Style.FILL
        paint.textSize = 100f
        canvas.drawText(time.toInt().toString(), 40f, 100f, paint)

        canvas.drawText(shotCount.toString(), 300f, 100f, paint)
        canvas.drawBitmap(shotBitmap, 200f, 20f, paint)
    }

    fun addTime() {
        time += 5
    }
}