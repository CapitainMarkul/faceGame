package ru.tzhack.facegame.facetraking.mlkit

import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.face.FirebaseVisionFace
import com.google.firebase.ml.vision.face.FirebaseVisionFaceContour
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions
import com.otaliastudios.cameraview.frame.Frame
import ru.tzhack.facegame.data.model.FaceEmoji
import ru.tzhack.facegame.facetraking.mlkit.listener.MlKitDebugListener
import ru.tzhack.facegame.facetraking.mlkit.listener.MlKitEmojiListener
import ru.tzhack.facegame.facetraking.mlkit.listener.MlKitHeroListener
import ru.tzhack.facegame.facetraking.util.*

const val maxHeadZ = 25F
const val minHeadZ = maxHeadZ * -1

object MlKitEngine {

    private var faceDetector: FirebaseVisionFaceDetector? = null

//    private var analyzing = false

    fun initMlKit() {
        // face classification and landmark detection
        val options = FirebaseVisionFaceDetectorOptions.Builder()
            .setPerformanceMode(FirebaseVisionFaceDetectorOptions.FAST)
            .setContourMode(FirebaseVisionFaceDetectorOptions.ALL_CONTOURS)
            .setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
            .setMinFaceSize(0.9F)
            .build()

        faceDetector = FirebaseVision.getInstance().getVisionFaceDetector(options)
    }

    fun extractDataFromFrame(
        frame: Frame,
        listenerHero: MlKitHeroListener? = null,
        listenerEmoji: MlKitEmojiListener? = null,
        debugListener: MlKitDebugListener? = null
    ) {
//        if (analyzing) return
//        analyzing = true

        getFaceDetector().detectInImage(frame.getVisionImageFromFrame())
            .addOnSuccessListener { faces ->
                if (faces.isNotEmpty()) {
                    // Работаем только с одним лицом
                    val face = faces.first()

                    // Защита от ситуации, когда точки не были получены
                    if (face.getContour(FirebaseVisionFaceContour.ALL_POINTS).points.isNotEmpty()) {
                        listenerHero?.let { calculateHeroActions(face, it) }
                        listenerEmoji?.let { calculateEmojiActions(face, it) }

                        //Debug Info
                        debugListener?.onDebugInfo(face)
                    }
                }

//                analyzing = false
            }
            .addOnFailureListener {
                listenerHero?.onError(it)
//                analyzing = false
            }
    }

    //FIXME: must be Private !!!!!!!!!!!!!!
    private fun calculateHeroActions(face: FirebaseVisionFace, listener: MlKitHeroListener) {
        listener.onHeroHorizontalAnim(face.headEulerAngleZ)
        //onHeroSpeedAnim(face.headEulerAngleZ)
        if (face.checkSmileOnFaceAvailable()) listener.onHeroSuperPowerAnim()
        if (face.checkRightEyeCloseOnFaceAvailable()) listener.onHeroRightEyeAnim()
        if (face.checkLeftEyeCloseOnFaceAvailable()) listener.onHeroLeftEyeAnim()
    }

    //FIXME: must be Private !!!!!!!!!!!!!!
    private fun calculateEmojiActions(face: FirebaseVisionFace, listener: MlKitEmojiListener) {
        // Наклоны головы
        if (face.headEulerAngleZ >= maxHeadZ) listener.onEmojiObtained(FaceEmoji.HEAD_BIAS_RIGHT)
        else if (face.headEulerAngleZ <= minHeadZ) listener.onEmojiObtained(FaceEmoji.HEAD_BIAS_LEFT)

        // Подмигивания
        if(face.checkDoubleEyeCloseOnFaceAvailable()) listener.onEmojiObtained(FaceEmoji.DOUBLE_EYE_CLOSE)
        else {
            if (face.checkLeftEyeCloseOnFaceAvailable()) listener.onEmojiObtained(FaceEmoji.LEFT_EYE_CLOSE)
            if (face.checkRightEyeCloseOnFaceAvailable()) listener.onEmojiObtained(FaceEmoji.RIGHT_EYE_CLOSE)
        }

        // Улыбка
        if (face.checkSmileOnFaceAvailable()) listener.onEmojiObtained(FaceEmoji.SMILE)

        // Открыт рот
        if (face.checkOpenMouthOnFaceAvailable()) listener.onEmojiObtained(FaceEmoji.MOUTH_OPEN)

        // Повороты головы
        if (face.checkHeadLeftRotateAvailable()) listener.onEmojiObtained(FaceEmoji.HEAD_ROTATE_LEFT)
        else if (face.checkHeadRightRotateAvailable()) listener.onEmojiObtained(FaceEmoji.HEAD_ROTATE_RIGHT)
    }

    private fun getFaceDetector(): FirebaseVisionFaceDetector = faceDetector
        ?: throw Exception("MlKit is not configured! Call first 'initMlKit()' method.")
}