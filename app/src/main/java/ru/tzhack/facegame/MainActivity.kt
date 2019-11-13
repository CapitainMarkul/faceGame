package ru.tzhack.facegame

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.tzhack.facegame.facetraking.TrackingFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, TrackingFragment.createFragment(), TrackingFragment.TAG)
            .commit()

    }
}
