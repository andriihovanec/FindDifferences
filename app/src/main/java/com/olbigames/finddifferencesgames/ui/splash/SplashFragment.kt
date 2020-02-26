package com.olbigames.finddifferencesgames.ui.splash


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.olbigames.finddifferencesgames.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashFragment : Fragment(R.layout.fragment_splash) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setSplashTime()
    }

    private fun setSplashTime() {
        GlobalScope.launch(context = Dispatchers.Main) {
            delay(3000)
            findNavController()
                .navigate(
                    R.id.action_splashFragment_to_homeFragment,
                    null,
                    NavOptions.Builder()
                        .setPopUpTo(
                            R.id.splashFragment,
                            true).build()
                )
        }
    }
}
