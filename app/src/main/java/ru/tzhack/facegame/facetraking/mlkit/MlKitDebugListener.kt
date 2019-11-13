package ru.tzhack.facegame.facetraking.mlkit

import com.google.firebase.ml.vision.face.FirebaseVisionFace

interface MlKitDebugListener {

    /**
     * Метод для ведения отладочных работ с MlKit.
     *
     * @param face полная модель пользователя.
     * */
    fun onDebugInfo(face: FirebaseVisionFace?)
}