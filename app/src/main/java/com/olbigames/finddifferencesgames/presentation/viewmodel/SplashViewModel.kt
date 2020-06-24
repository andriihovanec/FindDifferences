package com.olbigames.finddifferencesgames.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.olbigames.finddifferencesgames.utilities.HandleOnce
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

class SplashViewModel @Inject constructor() : BaseViewModel() {

    private val _splashCompleted = MutableLiveData<HandleOnce<Boolean>>()
    val splashCompleted = _splashCompleted

    fun setSplashTime() {
        viewModelScope.launch(Dispatchers.Main) {
            delay(3000)
            _splashCompleted.value = HandleOnce(true)
        }
    }
}