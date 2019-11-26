package ru.tzhack.facegame

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import ru.tzhack.facegame.bird.BirdFragment
import ru.tzhack.facegame.bird.GameOverListener
import ru.tzhack.facegame.facetraking.FaceTrackingFragment

private const val CAMERA_PERMISSION_REQUEST_CODE = 101

class MainActivity : AppCompatActivity(), GameOverListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
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

//        supportFragmentManager.beginTransaction()
//            .replace(R.id.fragment_container, BirdFragment.createFragment(), BirdFragment.TAG)
//            .commit()

//        supportFragmentManager.beginTransaction()
//            .replace(R.id.fragment_container, TrackingFragment.createFragment(), TrackingFragment.TAG)
//            .commit()

//        supportFragmentManager.beginTransaction()
//            .replace(R.id.fragment_container, NearbyFragment.createFragment(), NearbyFragment.TAG)
//            .commit()

//        supportFragmentManager.beginTransaction()
//            .replace(R.id.fragment_container, FaceTrackingFragment.createFragment(), FaceTrackingFragment.TAG)
//            .commit()

    }

    override fun onGameOver() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, FaceTrackingFragment.createFragment(), FaceTrackingFragment.TAG)
            .commit()
    }

    private fun hasCameraPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, FaceTrackingFragment.createFragment(), FaceTrackingFragment.TAG)
                    .commit()
            }
        }
    }
}
