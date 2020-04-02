package com.olbigames.finddifferencesgames.db.game

import androidx.room.*
import com.olbigames.finddifferencesgames.clean.cache.GameCache
import com.olbigames.finddifferencesgames.db.BaseDao

@Dao
interface GameDao : BaseDao<GameEntity>,
    GameCache {

    @Insert
    override fun insertGame(game: GameEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertList(games: List<GameEntity>)

    @Transaction
    @Query("SELECT * FROM games")
    override fun getAllGames(): List<GameEntity>

    @Query("DELETE FROM games")
    suspend fun deleteAll()

    @Query("SELECT * FROM games WHERE level LIKE :searchLevel")
    suspend fun findGame(searchLevel: String): GameEntity

    @Query("UPDATE games SET foundedCount = foundedCount + 1 WHERE level =:level")
    override fun updateFoundedCount(level: Int)

    @Query("SELECT foundedCount FROM games WHERE level =:level")
    override fun foundedCount(level: Int): Int

    @Query("SELECT * FROM games WHERE level LIKE :level")
    override fun getGame(level: Int): GameEntity

    /*@Transaction
    @Query("SELECT * FROM game_level")
    fun getAllGamesWithDifferences(): List<GameWithDifferences>*/

    /*@Transaction
    @Query("SELECT * FROM game_level WHERE level LIKE :searchLevel")
    fun getGameWithDifferences(searchLevel: Int): List<GameWithDifferences>*/
}