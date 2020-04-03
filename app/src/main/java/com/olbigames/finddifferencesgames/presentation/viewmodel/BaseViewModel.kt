package com.olbigames.finddifferencesgames.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.olbigames.finddifferencesgames.domain.HandleOnce
import com.olbigames.finddifferencesgames.domain.type.Failure

open class BaseViewModel : ViewModel() {

    var failureData: MutableLiveData<HandleOnce<Failure>> = MutableLiveData()

    protected fun handleFailure(failure: Failure) {
        failureData.value = HandleOnce(failure)
    }
}