package com.olbigames.finddifferencesgames

import android.app.Application
import android.content.Context
import com.olbigames.finddifferencesgames.presentation.injection.AppModule
import com.olbigames.finddifferencesgames.presentation.injection.CacheModule
import com.olbigames.finddifferencesgames.presentation.injection.ViewModelModule
import com.olbigames.finddifferencesgames.ui.game.DownloadLevelFragment
import com.olbigames.finddifferencesgames.ui.game.GameFragment
import com.olbigames.finddifferencesgames.ui.home.GameListFragment
import dagger.Component
import javax.inject.Singleton

class App : Application() {

    init {
        instance = this
    }

    companion object {
        lateinit var appComponent: AppComponent

        private var instance: App? = null

        fun applicationContext() : Context {
            return instance!!.applicationContext
        }
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
}