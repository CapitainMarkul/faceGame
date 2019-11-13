package ru.tzhack.facegame.facetraking.util

import com.google.firebase.ml.vision.face.FirebaseVisionFace

private const val correctProbabilityPercent = 0.75F

/**
 * Метод для проверки наличия улыбки на лице игрока.
 * */
fun FirebaseVisionFace.checkSmileOnFaceAvailable(): Boolean =
    smilingProbability > correctProbabilityPercent