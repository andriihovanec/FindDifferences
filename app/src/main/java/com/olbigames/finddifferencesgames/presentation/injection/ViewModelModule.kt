package com.olbigames.finddifferencesgames.presentation.injection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.olbigames.finddifferencesgames.presentation.viewmodel.DownloadLevelViewModel
import com.olbigames.finddifferencesgames.presentation.viewmodel.ViewModelFactory
import com.olbigames.finddifferencesgames.presentation.viewmodel.GameViewModel
import com.olbigames.finddifferencesgames.presentation.viewmodel.GameListViewModel
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
    @ViewModelKey(GameListViewModel::class)
    abstract fun bindGameListViewModel(accountViewModel: GameListViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(DownloadLevelViewModel::class)
    abstract fun bindDownloadLevelViewModel(accountViewModel: DownloadLevelViewModel): ViewModel
}