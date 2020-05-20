package com.olbigames.finddifferencesgames

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.MobileAds

class MainActivity : AppCompatActivity() {

    companion object {

        var gameCount = 0

        private lateinit var context: Context

        fun getContext(): Context {
            return context
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        context = this
        InitADMOB()
    }

    private fun InitADMOB() {
        val admob_app_id = this.resources.getString(R.string.admob_app_id)
        MobileAds.initialize(this, admob_app_id)
    }
}
