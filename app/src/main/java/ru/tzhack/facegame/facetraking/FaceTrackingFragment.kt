package ru.tzhack.facegame.facetraking

import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Rational
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.CameraX
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.ml.vision.face.FirebaseVisionFace
import ru.tzhack.facegame.R
import ru.tzhack.facegame.data.model.FaceEmoji
import ru.tzhack.facegame.databinding.FragmentFaceTrackingBinding
import ru.tzhack.facegame.facetraking.mlkit.listener.MlKitEmojiListener
import ru.tzhack.facegame.facetraking.util.fadeInOutAnim
import ru.tzhack.facegame.facetraking.util.fadeOutInAnim
import ru.tzhack.facegame.facetraking.view.AutoFitPreviewAnalysis
import kotlin.random.Random

class FaceTrackingFragment : Fragment() {

    companion object {
        val TAG: String = FaceTrackingFragment::class.java.simpleName

        fun createFragment() =
            FaceTrackingFragment().apply {
                arguments = Bundle().apply { }
            }
    }

    private lateinit var binding: FragmentFaceTrackingBinding

    private lateinit var currentEmoji: FaceEmoji

    private val emojiList = listOf(
        FaceEmoji.DOUBLE_EYE_CLOSE,
        FaceEmoji.LEFT_EYE_CLOSE,
        FaceEmoji.RIGHT_EYE_CLOSE,
        FaceEmoji.SMILE,
        FaceEmoji.MOUTH_OPEN,
        FaceEmoji.HEAD_BIAS_LEFT,
        FaceEmoji.HEAD_BIAS_RIGHT,
        FaceEmoji.HEAD_ROTATE_LEFT,
        FaceEmoji.HEAD_ROTATE_RIGHT
    )

    private var lockEmojiProcess = false

    private val mlKitEmojiListener = object : MlKitEmojiListener {
        override fun onEmojiObtained(emoji: FaceEmoji) {
            if (!lockEmojiProcess && currentEmoji == emoji) doneEmoji()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_face_tracking, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = DataBindingUtil.bind(view)
            ?: throw IllegalStateException("ViewDataBinding is null for ${FaceTrackingFragment::class.java.canonicalName}")

        updateEmojiOnScreen()

        with(binding.cameraView) {
            post {
                if (width > height) {
                    val newWidth = (height * 0.75).toInt() // 9/16
                    layoutParams = layoutParams.apply {
                        width = newWidth
                    }
                    requestLayout()

                    binding.faceOverlayView.layoutParams = binding.faceOverlayView.layoutParams.apply {
                        width = newWidth
                    }
                    binding.faceOverlayView.requestLayout()
                }

                setUpCameraX()
            }
        }
    }

    private fun setUpCameraX() {
        CameraX.unbindAll()

        val displayMetrics = DisplayMetrics().also { binding.cameraView.display.getRealMetrics(it) }
        val screenSize = android.util.Size(displayMetrics.widthPixels, displayMetrics.heightPixels)
        val aspectRatio = Rational(displayMetrics.widthPixels, displayMetrics.heightPixels)
        val rotation = binding.cameraView.display.rotation

        val autoFitPreviewAnalysis = AutoFitPreviewAnalysis.build(
            screenSize, aspectRatio, rotation, binding.cameraView, binding.faceOverlayView,
            mlKitEmojiListener = mlKitEmojiListener
        )

        CameraX.bindToLifecycle(this, autoFitPreviewAnalysis.previewUseCase, autoFitPreviewAnalysis.analysisUseCase)
    }

    private fun doneEmoji() {
        lockEmojiProcess()
        binding.doneOverlay.fadeInOutAnim { updateEmojiOnScreen() }
        binding.txtEmojiDescription.fadeOutInAnim()
    }

    private fun updateEmojiOnScreen() {
        currentEmoji = randNextEmoji()

        binding.txtEmojiDescription.setText(currentEmoji.resDescription)
        Glide.with(binding.emojiAnim)
            .asGif()
            .load(currentEmoji.resAnim)
            .into(binding.emojiAnim)

        unlockEmojiProcess()
    }

    private fun lockEmojiProcess() {
        lockEmojiProcess = true
    }

    private fun unlockEmojiProcess() {
        lockEmojiProcess = false
    }

    private fun randNextEmoji(): FaceEmoji = emojiList[Random.Default.nextInt(emojiList.size)]

    private fun printContourOnFace(face: FirebaseVisionFace) {
//        binding.faceOverlayView.updateContour(
//            face.boundingBox,
//            listOf(face.getContour(FirebaseVisionFaceContour.ALL_POINTS).points)
//        )
    }
}