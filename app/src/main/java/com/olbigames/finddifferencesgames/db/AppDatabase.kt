package com.olbigames.finddifferencesgames.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.olbigames.finddifferencesgames.db.converter.ListConverter
import com.olbigames.finddifferencesgames.db.diference.DifferenceDao
import com.olbigames.finddifferencesgames.db.diference.DifferenceEntity
import com.olbigames.finddifferencesgames.db.game.GameDao
import com.olbigames.finddifferencesgames.db.game.GameEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(
    entities = [GameEntity::class, DifferenceEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(ListConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun gameDao(): GameDao
    abstract fun differenceDao(): DifferenceDao

    class GameDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        /** onCreate() add data in database whenever the app is started,
         * onOpen() add when the database is open, and add an Activity for adding words.
         */
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    populateDatabase(database.gameDao())
                }
            }
        }

        suspend fun populateDatabase(gameDao: GameDao) {
            gameDao.deleteAll()
            //add own games
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): AppDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "game_level"
                )
                    .addCallback(GameDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}