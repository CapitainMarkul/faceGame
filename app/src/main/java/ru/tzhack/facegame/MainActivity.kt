package ru.tzhack.facegame

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.tzhack.facegame.bird.BirdFragment
import ru.tzhack.facegame.bird.GameOverListener
import ru.tzhack.facegame.facetraking.FaceTrackingFragment

class MainActivity : AppCompatActivity(), GameOverListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, BirdFragment.createFragment(), BirdFragment.TAG)
            .commit()

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
}
