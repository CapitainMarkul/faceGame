package ru.tzhack.facegame.facetraking.util

import com.google.firebase.ml.vision.face.FirebaseVisionFace
import com.google.firebase.ml.vision.face.FirebaseVisionFaceContour.*

private const val correctSmileProbabilityPercent = 0.75F
private const val correctCloseEyeProbabilityPercent = 0.10F
private const val correctMouthOpenDelta = 80F

private const val correctHeadRotateDelta = 90F

// For Help:
// https://firebase.google.com/docs/ml-kit/images/examples/face_contours.svg

/**
 * Метод для проверки открытого рта на лице игрока.
 * */
fun FirebaseVisionFace.checkOpenMouthOnFaceAvailable(): Boolean {
    val mouthCenterPointIndex = 4
    val upperLitTop = getContour(UPPER_LIP_TOP).points[mouthCenterPointIndex].y
    val lowerLitBottom = getContour(LOWER_LIP_BOTTOM).points[mouthCenterPointIndex].y

    val resultDelta = lowerLitBottom - upperLitTop
    return resultDelta > correctMouthOpenDelta
}

/**
 * Метод для проверки поворота головы игрока налево.
 * */
fun FirebaseVisionFace.checkHeadLeftRotateAvailable(): Boolean {
    val noseCenterPointIndex = 1
    val noseCenter = getContour(NOSE_BRIDGE).points[noseCenterPointIndex].x

    val leftBorder = boundingBox.right /* Берем наоборот, т.к. работаем с зеркальным изображением */

    val resultDelta = leftBorder - noseCenter
    return resultDelta < correctHeadRotateDelta
}

/**
 * Метод для проверки поворота головы игрока направо.
 * */
fun FirebaseVisionFace.checkHeadRightRotateAvailable(): Boolean {
    val noseCenterPointIndex = 1
    val noseCenter = getContour(NOSE_BRIDGE).points[noseCenterPointIndex].x

    val leftBorder = boundingBox.left /* Берем наоборот, т.к. работаем с зеркальным изображением */

    val resultDelta = noseCenter - leftBorder
    return resultDelta < correctHeadRotateDelta
}

/**
 * Метод для проверки наличия улыбки на лице игрока.
 * */
fun FirebaseVisionFace.checkSmileOnFaceAvailable(): Boolean =
    smilingProbability > correctSmileProbabilityPercent

/**
 * Метод для проверки подмигивания правым глазом.
 * */
fun FirebaseVisionFace.checkRightEyeCloseOnFaceAvailable(): Boolean =
    rightEyeOpenProbability < correctCloseEyeProbabilityPercent

/**
 * Метод для проверки подмигивания левым глазом.
 * */
fun FirebaseVisionFace.checkLeftEyeCloseOnFaceAvailable(): Boolean =
    leftEyeOpenProbability < correctCloseEyeProbabilityPercent