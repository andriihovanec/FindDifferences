package com.olbigames.finddifferencesgames

import android.media.AudioManager
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.TypedValue
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private var displayW = 0
    private var displayH = 0
    private var banner_height = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)

        displayW = metrics.widthPixels
        displayH = metrics.heightPixels

        this.volumeControlStream = AudioManager.STREAM_MUSIC
        banner_height = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            50f,
            resources.displayMetrics
        ).toInt()
    }


}
