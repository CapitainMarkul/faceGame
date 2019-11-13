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
import ru.tzhack.facegame.databinding.FragmentTrackingBinding
import ru.tzhack.facegame.facetraking.mlkit.MlKitDebugListener
import ru.tzhack.facegame.facetraking.mlkit.MlKitEngine
import ru.tzhack.facegame.facetraking.mlkit.MlKitListener
import ru.tzhack.facegame.facetraking.util.heroHorizontalAnim
import ru.tzhack.facegame.facetraking.util.maxHeadZ
import ru.tzhack.facegame.facetraking.util.minHeadZ
import ru.tzhack.facegame.facetraking.util.speedMultiply


class TrackingFragment : Fragment() {

    companion object {
        val TAG: String = TrackingFragment::class.java.simpleName

        fun createFragment() =
            TrackingFragment().apply {
                arguments = Bundle().apply { }
            }
    }

    private lateinit var binding: FragmentTrackingBinding

    private val mlKitListener = object : MlKitListener {
        override fun onHeroHorizontalAnim(headEulerAngleZ: Float) {
            /* Работаем с наклоном головы | Ось Z */
            animRedSquare(headEulerAngleZ)
        }

        override fun onHeroSpeedAnim(speedValue: Float) {
            //TODO:
        }

        override fun onHeroSuperPowerAnim() {
            Toast.makeText(activity, "SMILE!!!", Toast.LENGTH_SHORT).show()
        }

        override fun onHeroRightEyeAnim() {
            //TODO:
        }

        override fun onHeroLeftEyeAnim() {
            //TODO:
        }

        override fun onError(exception: Exception) {
            //TODO:
        }
    }

    private val mlKitDebugListener = object : MlKitDebugListener {
        override fun onDebugInfo(face: FirebaseVisionFace?) {
            face?.let {printContourOnFace(it) }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_tracking, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = DataBindingUtil.bind(view)
            ?: throw IllegalStateException("ViewDataBinding is null for ${TrackingFragment::class.java.canonicalName}")

        with(binding.cameraView) {
            setLifecycleOwner(this@TrackingFragment)
            addFrameProcessor { frame -> MlKitEngine.extractDataFromFrame(frame, mlKitListener, mlKitDebugListener) }
        }
    }

    private fun animRedSquare(headEulerAngleZ: Float) {
        binding.txtHero.heroHorizontalAnim(headEulerAngleZ)

        /* FIXME: for Debug */
        binding.txtHeroAngle.heroHorizontalAnim(headEulerAngleZ)
        val angleValue = when {
            headEulerAngleZ < minHeadZ -> minHeadZ
            headEulerAngleZ > maxHeadZ -> maxHeadZ
            else                       -> headEulerAngleZ
        } / maxHeadZ * speedMultiply

        binding.txtHeroAngle.text = "$angleValue"
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