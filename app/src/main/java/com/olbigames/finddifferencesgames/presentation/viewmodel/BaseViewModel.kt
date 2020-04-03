package com.olbigames.finddifferencesgames.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.olbigames.finddifferencesgames.domain.HandleOnce
import com.olbigames.finddifferencesgames.clean.domain.type.Failure

open class BaseViewModel : ViewModel() {

    var failureData: MutableLiveData<_root_ide_package_.com.olbigames.finddifferencesgames.domain.HandleOnce<Failure>> = MutableLiveData()

    protected fun handleFailure(failure: Failure) {
        failureData.value =
            _root_ide_package_.com.olbigames.finddifferencesgames.domain.HandleOnce(failure)
    }
}