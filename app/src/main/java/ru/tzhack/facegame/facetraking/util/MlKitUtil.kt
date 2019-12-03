package ru.tzhack.facegame.facetraking.util

import com.google.firebase.ml.vision.face.FirebaseVisionFace
import com.google.firebase.ml.vision.face.FirebaseVisionFaceContour.*

private const val correctSmileProbabilityPercent = 0.75F
private const val correctCloseEyeProbabilityPercent = 0.20F
private const val correctMouthOpenDelta = 80F

private const val correctHeadLeftRotateDelta = 70F
private const val correctHeadRightRotateDelta = 70F

private const val correctEyeBrownMoveDelta = 6F

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

    val faceCenterLeftPointIndex = 9
    val faceLeft = getContour(FACE).points[faceCenterLeftPointIndex].x

    val resultDelta = faceLeft - noseCenter
    return resultDelta < correctHeadLeftRotateDelta
}

/**
 * Метод для проверки поворота головы игрока направо.
 * */
fun FirebaseVisionFace.checkHeadRightRotateAvailable(): Boolean {
    val noseCenterPointIndex = 1
    val noseCenter = getContour(NOSE_BRIDGE).points[noseCenterPointIndex].x

    val faceCenterRightPointIndex = 27
    val faceRight = getContour(FACE).points[faceCenterRightPointIndex].x

    val resultDelta = noseCenter - faceRight
    return resultDelta < correctHeadRightRotateDelta
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
    leftEyeOpenProbability < correctCloseEyeProbabilityPercent

/**
 * Метод для проверки подмигивания левым глазом.
 * */
fun FirebaseVisionFace.checkLeftEyeCloseOnFaceAvailable(): Boolean =
    rightEyeOpenProbability < correctCloseEyeProbabilityPercent

/**
 * Метод для проверки подмигивания обоими глазами.
 *
 * (Необходимо исключить моргания)
 * */
private var doubleCloseCount = 0

fun FirebaseVisionFace.checkDoubleEyeCloseOnFaceAvailable(): Boolean {
    val doubleClose = checkRightEyeCloseOnFaceAvailable() && checkLeftEyeCloseOnFaceAvailable()
    return if (doubleClose && ++doubleCloseCount > 5) {
        doubleCloseCount = 0
        true
    } else false
}

/**
 * Метод для проверки вижения левой брови на лице игрока.
 * */
//fun FirebaseVisionFace.checkLeftEyeBrownMoveOnFaceAvailable(): Boolean {
//    val eyeBrownCenterPointIndex = 2
//    val eyeBrownCenter = getContour(LEFT_EYEBROW_TOP).points[eyeBrownCenterPointIndex].y
//
//    val faceCenterRightPointIndex = 33
//    val faceRight = getContour(FACE).points[faceCenterRightPointIndex].y
//
//    val resultDelta = eyeBrownCenter - faceRight
//    Log.e("TAG", "Right: $resultDelta")
//    return resultDelta < correctEyeBrownMoveDelta
//}

/**
 * Метод для проверки вижения правой брови на лице игрока.
 * */
//fun FirebaseVisionFace.checkRightEyeBrownMoveOnFaceAvailable(): Boolean {
//    val eyeBrownCenterPointIndex = 2
//    val eyeBrownCenter = getContour(LEFT_EYEBROW_TOP).points[eyeBrownCenterPointIndex].y
//
//    val faceCenterRightPointIndex = 33
//    val faceRight = getContour(FACE).points[faceCenterRightPointIndex].y
//
//    val resultDelta = eyeBrownCenter - faceRight
//    Log.e("TAG", "Right: $resultDelta")
//    return resultDelta < correctEyeBrownMoveDelta
//}

/**
 * Метод для проверки вижения обеих бровей на лице игрока.
 * */
//fun FirebaseVisionFace.checkDoubleEyeBrownMoveOnFaceAvailable(): Boolean =
//        checkLeftEyeBrownMoveOnFaceAvailable() || checkRightEyeBrownMoveOnFaceAvailable()