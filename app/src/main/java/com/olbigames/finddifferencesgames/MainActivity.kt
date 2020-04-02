package com.olbigames.finddifferencesgames

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.olbigames.finddifferencesgames.utilities.ActivityContext

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
        ActivityContext.setContext(this)
        context = this
    }
}
