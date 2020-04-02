package com.olbigames.finddifferencesgames.utilities

import android.content.Context

class ActivityContext {

    companion object {

        private lateinit var context: Context

        fun setContext(con: Context) {
            context = con
        }
    }
}