package com.olbigames.finddifferencesgames

import android.app.Application
import android.content.Context
import com.olbigames.finddifferencesgames.presentation.injection.AppModule
import com.olbigames.finddifferencesgames.presentation.injection.CacheModule
import com.olbigames.finddifferencesgames.presentation.injection.ViewModelModule
import com.olbigames.finddifferencesgames.ui.game.DownloadLevelFragment
import com.olbigames.finddifferencesgames.ui.game.GameFragment
import com.olbigames.finddifferencesgames.ui.home.GameListFragment
import com.olbigames.finddifferencesgames.ui.home.SplashFragment
import dagger.Component
import javax.inject.Singleton

class App : Application() {

    companion object {
        lateinit var appComponent: AppComponent
    }

    override fun onCreate() {
        super.onCreate()
        initAppComponent()
    }

    private fun initAppComponent() {
        appComponent = DaggerAppComponent.builder()
            .appModule(
                AppModule(
                    this
                )
            ).build()
    }
}

@Singleton
@Component(modules = [AppModule::class, CacheModule::class, ViewModelModule::class])
interface AppComponent {

    fun inject(fragment: GameFragment)
    fun inject(fragment: GameListFragment)
    fun inject(fragment: DownloadLevelFragment)
    fun inject(fragment: SplashFragment)
}