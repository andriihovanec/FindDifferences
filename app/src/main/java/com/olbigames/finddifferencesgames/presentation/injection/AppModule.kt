package com.olbigames.finddifferencesgames.presentation.injection

import android.content.Context
import com.olbigames.finddifferencesgames.cache.difference.DifferenceCache
import com.olbigames.finddifferencesgames.cache.game.GameCache
import com.olbigames.finddifferencesgames.data.GamesRepositoryImpl
import com.olbigames.finddifferencesgames.data.GetGameRepositoryImpl
import com.olbigames.finddifferencesgames.clean.domain.game.GetGameRepository
import com.olbigames.finddifferencesgames.clean.domain.games.GamesRepository
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
    fun provideGetGameRepository(
        gameCache: GameCache,
        differenceCache: DifferenceCache
    ): GetGameRepository {

        return GetGameRepositoryImpl(
            gameCache,
            differenceCache
        )
    }

    @Singleton
    @Provides
    fun provideGamesRepository(
        gameCache: GameCache,
        differenceCache: DifferenceCache
    ): GamesRepository {

        return GamesRepositoryImpl(
            gameCache,
            differenceCache
        )
    }
}