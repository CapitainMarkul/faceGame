package ru.tzhack.facegame.facetraking.util

import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.view.ViewCompat

/* TODO: Make Private to Release */
const val speedMultiply = 150F
const val maxHeadZ = 25F
const val minHeadZ = maxHeadZ * -1

fun View.heroHorizontalAnim(headEulerAngleZ: Float) {
    val angleValue = when {
        headEulerAngleZ < minHeadZ -> minHeadZ
        headEulerAngleZ > maxHeadZ -> maxHeadZ
        else                       -> headEulerAngleZ
    } / maxHeadZ * speedMultiply

    translationTo(angleValue)
}

/*** Anim */
private fun View.translationTo(coordinateX: Float) {
    ViewCompat.animate(this)
        .translationXBy(coordinateX)
        .setInterpolator(LinearInterpolator())
        .start()
}