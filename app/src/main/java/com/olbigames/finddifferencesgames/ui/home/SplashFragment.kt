package com.olbigames.finddifferencesgames.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.olbigames.finddifferencesgames.App
import com.olbigames.finddifferencesgames.R
import com.olbigames.finddifferencesgames.presentation.viewmodel.SplashViewModel
import javax.inject.Inject

class SplashFragment : Fragment(R.layout.fragment_splash) {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: SplashViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.appComponent.inject(this)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory)[SplashViewModel::class.java]
        viewModel.setSplashTime()
        splashCompletedNotify()
    }

    private fun splashCompletedNotify() {
        viewModel.splashCompleted.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandle()?.let { completed ->
                if (completed) navigateToHome()
            }
        })
    }

    private fun navigateToHome() {
        findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToHomeFragment())
    }
}
