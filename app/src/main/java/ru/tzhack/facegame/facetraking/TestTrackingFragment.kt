package ru.tzhack.facegame.facetraking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.firebase.ml.vision.face.FirebaseVisionFace
import com.google.firebase.ml.vision.face.FirebaseVisionFaceContour
import ru.tzhack.facegame.R
import ru.tzhack.facegame.data.model.FaceEmoji
import ru.tzhack.facegame.databinding.FragmentTestTrackingBinding
import ru.tzhack.facegame.facetraking.mlkit.MlKitEngine
import ru.tzhack.facegame.facetraking.mlkit.listener.MlKitDebugListener
import ru.tzhack.facegame.facetraking.mlkit.listener.MlKitEmojiListener
import ru.tzhack.facegame.facetraking.mlkit.listener.MlKitHeroListener
import ru.tzhack.facegame.facetraking.util.heroHorizontalAnim


class TestTrackingFragment : Fragment() {

    companion object {
        val TAG: String = TestTrackingFragment::class.java.simpleName

        fun createFragment() =
            TestTrackingFragment().apply {
                arguments = Bundle().apply { }
            }
    }

    private lateinit var binding: FragmentTestTrackingBinding

    private val mlKitHeroListener = object : MlKitHeroListener {
        override fun onHeroHorizontalAnim(headEulerAngleZ: Float) {
            /* Работаем с наклоном головы | Ось Z */
            animRedSquare(headEulerAngleZ)
        }

        override fun onHeroSpeedAnim(speedValue: Float) {
            //TODO: Не реализовано, требует доработок
        }

        override fun onHeroSuperPowerAnim() {
            Toast.makeText(activity, "SMILE!!!", Toast.LENGTH_SHORT).show()
        }

        override fun onHeroRightEyeAnim() {
            //TODO: Здесь можно перемещать главного героя вправо на определенную константу
        }

        override fun onHeroLeftEyeAnim() {
            //TODO: Здесь можно перемещать главного героя влево на определенную константу
        }

        override fun onError(exception: Exception) {
            //TODO:
        }
    }

    private val mlKitEmojiListener = object : MlKitEmojiListener {
        override fun onEmojiObtained(emoji: FaceEmoji) {
            //TODO:
        }
    }

    private val mlKitDebugListener = object : MlKitDebugListener {
        override fun onDebugInfo(face: FirebaseVisionFace?) {
            face?.let { printContourOnFace(it) }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_test_tracking, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = DataBindingUtil.bind(view)
            ?: throw IllegalStateException("ViewDataBinding is null for ${TestTrackingFragment::class.java.canonicalName}")

        with(binding.cameraView) {
            setLifecycleOwner(this@TestTrackingFragment)
            addFrameProcessor { frame ->
                MlKitEngine.extractDataFromFrame(
                    frame = frame,
                    listenerHero = mlKitHeroListener,
                    listenerEmoji = mlKitEmojiListener,
                    debugListener = mlKitDebugListener
                )
            }
        }
    }

    private fun animRedSquare(headEulerAngleZ: Float) {
        binding.txtHero.heroHorizontalAnim(headEulerAngleZ)
    }

    private fun printContourOnFace(face: FirebaseVisionFace) {
        binding.faceOverlayView.updateContour(
            face.boundingBox,
            listOf(
                face.getContour(FirebaseVisionFaceContour.LEFT_EYEBROW_BOTTOM).points,
                face.getContour(FirebaseVisionFaceContour.LEFT_EYEBROW_TOP).points,
                face.getContour(FirebaseVisionFaceContour.RIGHT_EYEBROW_BOTTOM).points,
                face.getContour(FirebaseVisionFaceContour.RIGHT_EYEBROW_TOP).points
            )
        )
    }
}