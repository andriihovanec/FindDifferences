package com.olbigames.finddifferencesgames.presentation.injection

import android.content.Context
import android.content.SharedPreferences
import com.olbigames.finddifferencesgames.cache.GameDatabase
import com.olbigames.finddifferencesgames.data.difference.DifferenceCache
import com.olbigames.finddifferencesgames.data.game.GameCache
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class CacheModule {

    @Provides
    @Singleton
    fun provideSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
    }

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