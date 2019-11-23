package ru.tzhack.facegame.bird

import android.graphics.Point
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_bird.*
import ru.tzhack.facegame.R
import ru.tzhack.facegame.facetraking.TestTrackingFragment
import ru.tzhack.facegame.facetraking.mlkit.MlKitEngine
import ru.tzhack.facegame.facetraking.mlkit.listener.MlKitHeroListener
import kotlin.math.absoluteValue

class BirdFragment : Fragment() {

    companion object {
        val TAG: String = TestTrackingFragment::class.java.simpleName

        fun createFragment(): Fragment = BirdFragment()

        private const val angleMaxSpeed = 30f
        private const val angleStopped = 3f
    }

    private var game: Game? = null

    private val mlKitHeroListener = object : MlKitHeroListener {
        override fun onHeroHorizontalAnim(headEulerAngleZ: Float) {
//            Log.d("onHeroHorizontalAnim", headEulerAngleZ.toString())
            game?.let {
                val absolute = headEulerAngleZ.absoluteValue
                if (absolute < angleStopped) {
                    it.setMovementState(Movement.Stopped)
                } else {
                    val ratio = absolute / angleMaxSpeed
                    if (headEulerAngleZ > 0) {
                        it.setMovementState(Movement.Right(ratio))
                    } else {
                        it.setMovementState(Movement.Left(ratio))
                    }
                }
            }
        }

        override fun onHeroSpeedAnim(speedValue: Float) {

        }

        override fun onHeroSuperPowerAnim() {

        }

        override fun onHeroRightEyeAnim() {

        }

        override fun onHeroLeftEyeAnim() {

        }

        override fun onError(exception: Exception) {

        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_bird, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        camera_view.run {
            setLifecycleOwner(this@BirdFragment)
            addFrameProcessor { frame ->
                MlKitEngine.extractDataFromFrame(
                    frame = frame,
                    listenerHero = mlKitHeroListener
                )
            }
        }

        val size = Point()
        requireActivity().windowManager.defaultDisplay.getSize(size)
        game = Game(requireContext(), size)
        game_container.addView(game)
        game!!.endGameListener = {
            AlertDialog.Builder(requireContext())
                .setTitle("Game over")
                .setPositiveButton(
                    "Ok"
                ) { _, _ ->
                }
                .create()
                .show()
        }
    }

    override fun onResume() {
        super.onResume()
        game?.start()
    }

    override fun onPause() {
        super.onPause()
        game?.pause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        game = null
    }
}