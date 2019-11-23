package ru.tzhack.facegame.bird

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.view.MotionEvent
import android.view.SurfaceView

sealed class Movement {
    object Stopped : Movement()

    /**
     *  @param speedRatio 0..1
     */
    class Left(val speedRatio: Float) : Movement()

    class Right(val speedRatio: Float) : Movement()
}

@SuppressLint("ViewConstructor")
class Game(
    context: Context,
    size: Point
) : SurfaceView(context),
    Runnable {

    var endGameListener: (() -> Unit)? = null

    private var playing = false
    private var thread: Thread? = null

    private val manualInput = true

    private var canvas: Canvas = Canvas()
    private val paint: Paint = Paint()

    private val wallsSize = 1
    private val player: Player = Player(context, size.x.toFloat())
    private val walls: List<Wall> = Wall.generate(context, size.x.toFloat(), wallsSize)

    private val viewport = Viewport(size.y.toFloat())

    private val input = Input(size.x)

    private val backgroundColor = Color.rgb(127, 199, 255)

    fun start() {
        if (!playing) {
            playing = true
            thread = Thread(this)
            thread!!.start()
        }
    }

    fun pause() {
        if (playing) {
            playing = false
            thread?.join()
            thread = null
        }
    }

    fun setMovementState(movement: Movement) {
        player.setMovementState(movement)
    }

    override fun run() {
        var lastFrameTime = SystemClock.uptimeMillis()
        while (playing) {
            val time = SystemClock.uptimeMillis()
            val deltaTime = (time - lastFrameTime) / 1000f
            lastFrameTime = time

            update(deltaTime)

            draw()


//            val timeThisFrame = SystemClock.uptimeMillis() - time
//            if (timeThisFrame >= 1) {
//                val fps = 1000 / timeThisFrame
//                Log.d("thread", "fps:$fps")
//            }
        }
    }

    private fun update(dt: Float) {
        var collision = false
        walls.forEach {
            if (it.collision(player.position)) {
                collision = true
                return@forEach
            }
        }

        player.crashed = collision

        player.update(dt)
        viewport.setWorldY(player.getY())

        if (player.position.bottom > walls.last().getTop()) {
            Handler(Looper.getMainLooper()).post {
                endGameListener?.invoke()
            }
            pause()
        }
    }

    private fun draw() {
        if (holder.surface.isValid) {
            val lockCanvas = holder.lockCanvas()
            if (lockCanvas != null) {
                canvas = lockCanvas

                canvas.drawColor(backgroundColor)

                walls.forEach {
                    it.draw(canvas, paint, viewport)
                }

                player.draw(canvas, paint, viewport)

                holder.unlockCanvasAndPost(canvas)
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(motionEvent: MotionEvent): Boolean {
        if (manualInput) {
            input.handleInput(motionEvent)
        }
        return true
    }

    internal inner class Input(private val screenWidth: Int) {

        fun handleInput(motionEvent: MotionEvent) {
            val x = motionEvent.getX(0).toInt()

            when (motionEvent.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    if (x > screenWidth / 2) {
                        player.setMovementState(Movement.Right(1f))
                    } else {
                        player.setMovementState(Movement.Left(1f))
                    }
                }
                MotionEvent.ACTION_UP   -> {
                    player.setMovementState(Movement.Stopped)
                }
            }
        }
    }
}