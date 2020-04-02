package com.olbigames.finddifferencesgames.clean.presentation.injection

import android.content.Context
import com.olbigames.finddifferencesgames.clean.cache.DifferenceCache
import com.olbigames.finddifferencesgames.clean.cache.GameCache
import com.olbigames.finddifferencesgames.clean.data.GetGameRepositoryImpl
import com.olbigames.finddifferencesgames.clean.domain.game.GetGameRepository
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

        return GetGameRepositoryImpl(gameCache, differenceCache)
    }
}