package com.olbigames.finddifferencesgames.presentation.injection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.olbigames.finddifferencesgames.presentation.viewmodel.ViewModelFactory
import com.olbigames.finddifferencesgames.ui.game.GameViewModel
import com.olbigames.finddifferencesgames.ui.home.HomeViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {

    @Binds
    internal abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(GameViewModel::class)
    abstract fun bindPostsViewModel(accountViewModel: GameViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(HomeViewModel::class)
    abstract fun bindHomeViewModel(accountViewModel: HomeViewModel): ViewModel
}