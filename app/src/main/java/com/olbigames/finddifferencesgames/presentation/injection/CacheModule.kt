package com.olbigames.finddifferencesgames.presentation.injection

import android.content.Context
import com.olbigames.finddifferencesgames.cache.difference.DifferenceCache
import com.olbigames.finddifferencesgames.cache.game.GameCache
import com.olbigames.finddifferencesgames.cache.GameDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class CacheModule {

    @Provides
    @Singleton
    fun provideAppDatabase(context: Context): GameDatabase {
        return GameDatabase.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideGameCache(gameDatabase: GameDatabase): GameCache {
        return gameDatabase.gameDao
    }

    @Provides
    @Singleton
    fun provideDifferenceCache(gameDatabase: GameDatabase): DifferenceCache {
        return gameDatabase.differenceDao
    }
}