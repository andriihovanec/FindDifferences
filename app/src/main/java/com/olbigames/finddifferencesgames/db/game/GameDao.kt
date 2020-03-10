package com.olbigames.finddifferencesgames.db.game

import androidx.room.*

@Dao
interface GameDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(game: GameEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertList(game: List<GameEntity>)

    @Query("SELECT * FROM game_level")
    suspend fun getAll(): List<GameEntity>

    @Delete
    suspend fun delete(game: GameEntity)

    @Query("DELETE FROM game_level")
    suspend fun deleteAll()

    @Query("SELECT * FROM game_level WHERE level LIKE :searchLevel ")
    suspend fun findGame(searchLevel: String): GameEntity
}