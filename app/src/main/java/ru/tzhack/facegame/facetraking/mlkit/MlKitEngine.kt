package ru.tzhack.facegame.facetraking.mlkit

import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions
import com.otaliastudios.cameraview.frame.Frame
import ru.tzhack.facegame.facetraking.util.checkSmileOnFaceAvailable
import ru.tzhack.facegame.facetraking.util.getVisionImageFromFrame

class MlKitEngine {

    companion object {

        enum class DetectorMode {
            ALL/*,
            ONLY_LANDMARKS,
            ONLY_CLASSIFICATION,
            ONLY_CONTOURS*/
        }

        private var faceDetector: FirebaseVisionFaceDetector? = null

        fun initMlKit() {
            // face classification and landmark detection
            val options = FirebaseVisionFaceDetectorOptions.Builder()
                .setPerformanceMode(FirebaseVisionFaceDetectorOptions.FAST)
//                .setLandmarkMode(FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS)
                .setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
                .setContourMode(FirebaseVisionFaceDetectorOptions.ALL_CONTOURS)
                .setMinFaceSize(0.1F)
                .build()

            faceDetector = FirebaseVision.getInstance().getVisionFaceDetector(options)
        }

        fun extractDataFromFrame(frame: Frame, listener: MlKitListener, debugListener: MlKitDebugListener? = null) {
            getFaceDetector().detectInImage(frame.getVisionImageFromFrame())
                .addOnSuccessListener {
                    if (it.isNotEmpty()) {
                        val face = it.first()

                        with(listener) {
                            onHeroHorizontalAnim(face.headEulerAngleZ)
                            //onHeroSpeedAnim(face.headEulerAngleZ)
                            if (face.checkSmileOnFaceAvailable()) onHeroSuperPowerAnim()

                            //Debug Info
                            debugListener?.onDebugInfo(face)
                        }
                    }
                }
                .addOnFailureListener { listener.onError(it) }
        }

        /* TODO: Обновление будет, когда начнется "бонус игра" */
        fun updateFaceDetectorTo(detectorMode: DetectorMode) {
            //New Options and faceDetector
            when (detectorMode) {
                DetectorMode.ALL -> Unit
            }
        }

        private fun getFaceDetector(): FirebaseVisionFaceDetector =
            faceDetector ?: throw Exception("MlKit is not configured! Call first 'initMlKit()' method.")
    }
}