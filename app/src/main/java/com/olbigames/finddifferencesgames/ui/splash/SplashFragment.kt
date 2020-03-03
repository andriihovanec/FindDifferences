package com.olbigames.finddifferencesgames.ui.splash


import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.olbigames.finddifferencesgames.R
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashFragment : Fragment(R.layout.fragment_splash) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setSplashTime()
    }

    private fun setSplashTime() {
        GlobalScope.launch {
            delay(3000)
            navigateToHome()
        }
    }

    private fun navigateToHome() {
        findNavController()
            .navigate(
                R.id.action_splashFragment_to_homeFragment,
                null,
                NavOptions.Builder()
                    .setEnterAnim(R.anim.fragment_fade_enter)
                    .setExitAnim(R.anim.fragment_fade_exit)
                    .setPopEnterAnim(R.anim.fragment_fade_enter)
                    .setPopExitAnim(R.anim.fragment_fade_exit)
                    .setPopUpTo(
                        R.id.splashFragment,
                        true
                    ).build()
            )
    }
}
