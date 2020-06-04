package com.olbigames.finddifferencesgames.presentation.injection

import android.content.Context
import com.olbigames.finddifferencesgames.data.difference.DifferenceCache
import com.olbigames.finddifferencesgames.data.game.GameCache
import com.olbigames.finddifferencesgames.data.game.GameRepositoryImpl
import com.olbigames.finddifferencesgames.data.hint.HiddenHintCache
import com.olbigames.finddifferencesgames.domain.game.GameRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule(private val context: Context) {

    @Singleton
    @Provides
    fun provideAppContext(): Context = context

    @Singleton
    @Provides
    fun provideGamesRepository(
        gameCache: GameCache,
        differenceCache: DifferenceCache,
        hiddenHintCache: HiddenHintCache
    ): GameRepository {

        return GameRepositoryImpl(
            gameCache,
            differenceCache,
            hiddenHintCache
        )
    }
}