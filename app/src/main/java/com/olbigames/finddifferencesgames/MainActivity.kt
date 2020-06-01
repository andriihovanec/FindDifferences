package com.olbigames.finddifferencesgames

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.MobileAds

class MainActivity : AppCompatActivity() {

    companion object {

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
        initADMOB()
    }

    private fun initADMOB() {
        val admobAppId = this.resources.getString(R.string.admob_app_id)
        MobileAds.initialize(this, admobAppId)
    }
}
