package ru.tzhack.facegame.bird

import android.content.Context
import android.graphics.Point
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.firebase.ml.vision.face.FirebaseVisionFace
import com.otaliastudios.cameraview.size.Size
import kotlinx.android.synthetic.main.fragment_bird.*
import ru.tzhack.facegame.R
import ru.tzhack.facegame.facetraking.mlkit.MlKitEngine
import ru.tzhack.facegame.facetraking.mlkit.listener.MlKitDebugListener
import ru.tzhack.facegame.facetraking.mlkit.listener.MlKitHeroListener
import kotlin.math.absoluteValue

interface GameOverListener {
    fun onGameStarted()
    fun onBonusLevel()
    fun onGameOver()
}

class BirdFragment : Fragment() {

    companion object {
        val TAG: String = BirdFragment::class.java.simpleName

        fun createFragment(): Fragment = BirdFragment()

        private const val angleMaxSpeed = 30f
        private const val angleStopped = 3f
    }

    private var game: Game? = null
    private var gameOverListener: GameOverListener? = null

    private val mlKitHeroListener = object : MlKitHeroListener {
        override fun onHeroHorizontalAnim(headEulerAngleZ: Float) {
            game?.let {
                val absolute = headEulerAngleZ.absoluteValue
                if (absolute < angleStopped) {
                    it.setMovementState(Movement.Stopped)
                } else {
                    var ratio = absolute / angleMaxSpeed
                    if (ratio > 1f) {
                        ratio = 1f
                    }
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
            game?.run {
                if (pause) {
                    gameOverListener?.onGameStarted()
                }
                pause = false
            }
        }

        override fun onHeroRightEyeAnim() {
            game?.shot()
        }

        override fun onHeroLeftEyeAnim() {
            game?.shot()
        }

        override fun onError(exception: Exception) {

        }
    }

    private val mlKitDebugListener = object : MlKitDebugListener {
        override fun onDebugInfo(frameSize: Size, face: FirebaseVisionFace?) {
//            face?.let { printContourOnFace(it) }
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
                    listenerHero = mlKitHeroListener,
                    debugListener = mlKitDebugListener
                )
            }
        }

        val size = Point()
        requireActivity().windowManager.defaultDisplay.getSize(size)
        game = Game(requireContext(), size)
        game_container.addView(game)
        game!!.endGameListener = { timeOver ->
            if (timeOver) {
                AlertDialog.Builder(requireContext())
                    .setTitle("Увы :(")
                    .setMessage("Ты не смог добраться до финиша. Попробуешь еще раз?")
                    .setCancelable(false)
                    .setPositiveButton(
                        "Конечно"
                    ) { _, _ -> gameOverListener?.onGameOver() }
                    .create()
                    .show()
            } else {
                AlertDialog.Builder(requireContext())
                    .setTitle("Поздравляем!")
                    .setMessage("Ты добрался до бонус уровня. Готов начать?")
                    .setCancelable(false)
                    .setPositiveButton(
                        "Конечно"
                    ) { _, _ -> gameOverListener?.onBonusLevel() }
                    .create()
                    .show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        game?.start()
    }

    override fun onPause() {
        super.onPause()
        game?.stop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        game = null
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        when (context) {
            is GameOverListener -> gameOverListener = context
        }
    }
}