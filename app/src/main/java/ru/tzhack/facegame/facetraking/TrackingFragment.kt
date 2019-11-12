package ru.tzhack.facegame.facetraking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions
import com.google.firebase.ml.vision.face.FirebaseVisionFaceLandmark
import com.otaliastudios.cameraview.frame.Frame
import ru.tzhack.facegame.R
import ru.tzhack.facegame.databinding.FragmentTrackingBinding

class TrackingFragment : Fragment() {

    companion object {
        val TAG: String = TrackingFragment::class.java.simpleName

        fun createFragment() =
            TrackingFragment().apply {
                arguments = Bundle().apply { }
            }
    }

    private lateinit var objectDetector: FirebaseVisionFaceDetector

    private lateinit var binding: FragmentTrackingBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_tracking, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = DataBindingUtil.bind(view)
            ?: throw IllegalStateException("ViewDataBinding is null for ${TrackingFragment::class.java.canonicalName}")

        configureMlKit()

        with(binding.cameraView) {
            setLifecycleOwner(this@TrackingFragment)
            addFrameProcessor { frame ->
                extractDataFromFrame(frame) { result ->
                    binding.txtLandscape.text = result
                }
            }
        }
    }

    private fun configureMlKit() {
        // face classification and landmark detection
        val options = FirebaseVisionFaceDetectorOptions.Builder()
            .setPerformanceMode(FirebaseVisionFaceDetectorOptions.FAST)
            .setLandmarkMode(FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS)
            .setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
            .setMinFaceSize(0.1F)
            .build()

        objectDetector = FirebaseVision.getInstance().getVisionFaceDetector(options)
    }

    private fun extractDataFromFrame(frame: Frame, callback: (String) -> Unit) {
        objectDetector.detectInImage(getVisionImageFromFrame(frame))
            .addOnSuccessListener {
                if (it.isNotEmpty()) {
                    val face = it.first()
                    val result =
                        "LEFT_EYE: \n${face.getLandmark(FirebaseVisionFaceLandmark.LEFT_EYE)?.position}\n\n" +
                                "RIGHT_EYE: \n${face.getLandmark(FirebaseVisionFaceLandmark.RIGHT_EYE)?.position}\n\n" +
                                "NOSE_BASE: \n${face.getLandmark(FirebaseVisionFaceLandmark.NOSE_BASE)?.position}\n\n" +
                                "LEFT_EAR: \n${face.getLandmark(FirebaseVisionFaceLandmark.LEFT_EAR)?.position}\n\n" +
                                "Left RIGHT_EAR: \n${face.getLandmark(FirebaseVisionFaceLandmark.RIGHT_EAR)?.position}\n\n" +
                                "MOUTH_LEFT: \n${face.getLandmark(FirebaseVisionFaceLandmark.MOUTH_LEFT)?.position}\n\n" +
                                "MOUTH_RIGHT: \n${face.getLandmark(FirebaseVisionFaceLandmark.MOUTH_RIGHT)?.position}\n\n" +
                                "MOUTH_BOTTOM: \n${face.getLandmark(FirebaseVisionFaceLandmark.MOUTH_BOTTOM)?.position}\n\n" +
                                "\n\n" +
                                "SMILE: ${face.smilingProbability}\n" +
                                "LEFT_EYE_PROPAB: ${face.leftEyeOpenProbability}\n" +
                                "RIGHT_EYE_PROPAB: ${face.rightEyeOpenProbability}\n" +
                                "\n\n" +
                                "HEAD_Y: \n${face.headEulerAngleY}\n" +
                                "HEAD_Z: \n${face.headEulerAngleZ}"

                                callback(result)
                }
            }
            .addOnFailureListener {
                callback("Unable to detect an object")
            }
    }

    private fun getVisionImageFromFrame(frame: Frame): FirebaseVisionImage {
        //ByteArray for the captured frame
        val data = frame.data

        val frameRotation = when (frame.rotation) {
            in (0 until 90)    -> FirebaseVisionImageMetadata.ROTATION_0
            in (90 until 180)  -> FirebaseVisionImageMetadata.ROTATION_90
            in (180 until 270) -> FirebaseVisionImageMetadata.ROTATION_180
            in (270 until 360) -> FirebaseVisionImageMetadata.ROTATION_270
            else               -> FirebaseVisionImageMetadata.ROTATION_0
        }

        /* TODO: переписать под фиксированные величины */
        //Metadata that gives more information on the image that is to be converted to FirebaseVisionImage
        val imageMetaData = FirebaseVisionImageMetadata.Builder()
            .setWidth(frame.size.width)
            .setHeight(frame.size.height)
            .setFormat(FirebaseVisionImageMetadata.IMAGE_FORMAT_NV21)
            .setRotation(frameRotation)
            .build()

        return FirebaseVisionImage.fromByteArray(data, imageMetaData)
    }
}