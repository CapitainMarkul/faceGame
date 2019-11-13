package ru.tzhack.facegame

import android.app.Application
import ru.tzhack.facegame.facetraking.mlkit.MlKitEngine

class AppDelegate : Application() {

    override fun onCreate() {
        super.onCreate()

        MlKitEngine.initMlKit()
    }
}