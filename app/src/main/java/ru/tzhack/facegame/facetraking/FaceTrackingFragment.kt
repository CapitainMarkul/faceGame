package ru.tzhack.facegame.facetraking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.ml.vision.face.FirebaseVisionFace
import com.google.firebase.ml.vision.face.FirebaseVisionFaceContour
import ru.tzhack.facegame.R
import ru.tzhack.facegame.data.model.FaceEmoji
import ru.tzhack.facegame.databinding.FragmentFaceTrackingBinding
import ru.tzhack.facegame.facetraking.mlkit.MlKitEngine
import ru.tzhack.facegame.facetraking.mlkit.listener.MlKitDebugListener
import ru.tzhack.facegame.facetraking.mlkit.listener.MlKitEmojiListener
import ru.tzhack.facegame.facetraking.util.fadeInOutAnim
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
        FaceEmoji.LEFT_EYE_CLOSE,
        FaceEmoji.RIGHT_EYE_CLOSE,
        FaceEmoji.SMILE,
        FaceEmoji.HEAD_BIAS_LEFT,
        FaceEmoji.HEAD_BIAS_RIGHT,
        FaceEmoji.MOUTH_OPEN,
        FaceEmoji.HEAD_ROTATE_LEFT,
        FaceEmoji.HEAD_ROTATE_RIGHT
    )

    private var lockEmojiProcess = false

    private val mlKitEmojiListener = object : MlKitEmojiListener {
        override fun onEmojiObtained(emoji: FaceEmoji) {
            if (!lockEmojiProcess && currentEmoji == emoji) doneEmoji()
        }
    }

    private val mlKitDebugListener = object : MlKitDebugListener {
        override fun onDebugInfo(face: FirebaseVisionFace?) {
            face?.let { printContourOnFace(it) }
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
            setLifecycleOwner(this@FaceTrackingFragment)
            addFrameProcessor { frame ->
                MlKitEngine.extractDataFromFrame(
                    frame = frame,
                    listenerEmoji = mlKitEmojiListener,
                    debugListener = mlKitDebugListener
                )
            }
        }
    }

    private fun doneEmoji() {
        lockEmojiProcess()
        binding.doneOverlay.fadeInOutAnim { updateEmojiOnScreen() }
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
        binding.faceOverlayView.updateContour(
            face.boundingBox,
            listOf(face.getContour(FirebaseVisionFaceContour.ALL_POINTS).points)
        )
    }
}