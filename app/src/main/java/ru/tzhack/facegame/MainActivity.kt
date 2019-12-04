package ru.tzhack.facegame

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import ru.tzhack.facegame.bird.BirdFragment
import ru.tzhack.facegame.bird.GameOverListener
import ru.tzhack.facegame.facetraking.FaceGameOverListener
import ru.tzhack.facegame.facetraking.FaceTrackingFragment

private const val CAMERA_PERMISSION_REQUEST_CODE = 101

class MainActivity : AppCompatActivity(), GameOverListener, FaceGameOverListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        showGameFragment()
    }

    override fun onGameStarted() {
        hideStartControl()
    }

    override fun onGameOver() {
        showGameFragment()
    }

    override fun onFaceGameOverPositive() {
        showStartControl()
        showBonusGameFragment()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showGameFragment()
            } else {
                finish()
            }
        }
    }

    private fun hasCameraPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    }

    private fun showGameFragment() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (hasCameraPermissions()) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, FaceTrackingFragment.createFragment(), FaceTrackingFragment.TAG)
                    .commit()
            } else {
                requestPermissions(arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_REQUEST_CODE)
            }
        } else {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, FaceTrackingFragment.createFragment(), FaceTrackingFragment.TAG)
                .commit()
        }
    }

    private fun showBonusGameFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, BirdFragment.createFragment(), BirdFragment.TAG)
            .commit()
    }

    private fun showStartControl() {
        txt_title.visibility = View.VISIBLE
        btn_start_game.visibility = View.VISIBLE
        txt_input_title.visibility = View.VISIBLE
        txt_input.visibility = View.VISIBLE
    }

    private fun hideStartControl() {
        txt_title.visibility = View.GONE
        btn_start_game.visibility = View.GONE
        txt_input_title.visibility = View.GONE
        txt_input.visibility = View.GONE
    }
}
