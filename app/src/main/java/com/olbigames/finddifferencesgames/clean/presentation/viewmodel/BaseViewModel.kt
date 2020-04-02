package com.olbigames.finddifferencesgames.clean.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.olbigames.finddifferencesgames.clean.domain.HandleOnce
import com.olbigames.finddifferencesgames.clean.domain.type.Failure

open class BaseViewModel : ViewModel() {

    var failureData: MutableLiveData<HandleOnce<Failure>> = MutableLiveData()

    protected fun handleFailure(failure: Failure) {
        failureData.value = HandleOnce(failure)
    }
}