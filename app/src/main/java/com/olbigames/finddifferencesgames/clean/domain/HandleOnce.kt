package com.olbigames.finddifferencesgames.clean.domain

open class HandleOnce<out T>(private val content: T) {

    private var hasBeenHandled = false

    fun getContentIfNotHandle(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }
}