package com.olbigames.finddifferencesgames.db.game

import androidx.room.*

@Dao
interface GameDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(game: GameEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertList(game: List<GameEntity>)

    @Query("SELECT * FROM games")
    suspend fun getAll(): List<GameEntity>

    @Delete
    suspend fun delete(game: GameEntity)

    @Query("DELETE FROM games")
    suspend fun deleteAll()

    @Query("SELECT * FROM games WHERE level LIKE :searchLevel")
    suspend fun findGame(searchLevel: String): GameEntity

    @Query("UPDATE games SET foundedCount = foundedCount + 1 WHERE level =:level")
    suspend fun updateFoundedCount(level: Int)

    /*@Transaction
    @Query("SELECT * FROM game_level")
    fun getAllGamesWithDifferences(): List<GameWithDifferences>*/

    /*@Transaction
    @Query("SELECT * FROM game_level WHERE level LIKE :searchLevel")
    fun getGameWithDifferences(searchLevel: Int): List<GameWithDifferences>*/
}