package ru.tzhack.facegame.bird

import android.graphics.Bitmap

class SpriteAnimation(
    private val frames: Array<Bitmap>,
    cycleSec: Float
) {

    private var frame = 0
    private var currentFrameTime = 0f
    private val frameTime = cycleSec / frames.size

    fun update(dt: Float) {
        currentFrameTime += dt
        if (currentFrameTime > frameTime) {
            frame++
            currentFrameTime = 0f
            if (frame >= frames.size)
                frame = 0
        }
    }

    fun getFrame(): Bitmap {
        return frames[frame]
    }

}