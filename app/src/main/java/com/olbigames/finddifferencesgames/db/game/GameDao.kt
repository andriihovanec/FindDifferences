package com.olbigames.finddifferencesgames.db.game

import androidx.lifecycle.LiveData
import androidx.room.*
import com.olbigames.finddifferencesgames.db.BaseDao

@Dao
interface GameDao : BaseDao<GameEntity> {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertList(game: List<GameEntity>)

    @Query("SELECT * FROM games")
    suspend fun getAll(): List<GameEntity>

    @Query("DELETE FROM games")
    suspend fun deleteAll()

    @Query("SELECT * FROM games WHERE level LIKE :searchLevel")
    suspend fun findGame(searchLevel: String): GameEntity

    @Query("UPDATE games SET foundedCount = foundedCount + 1 WHERE level =:level")
    suspend fun updateFoundedCount(level: Int)

    @Query("SELECT foundedCount FROM games WHERE level =:level")
    fun foundedCount(level: Int): LiveData<Int>

    /*@Transaction
    @Query("SELECT * FROM game_level")
    fun getAllGamesWithDifferences(): List<GameWithDifferences>*/

    /*@Transaction
    @Query("SELECT * FROM game_level WHERE level LIKE :searchLevel")
    fun getGameWithDifferences(searchLevel: Int): List<GameWithDifferences>*/
}