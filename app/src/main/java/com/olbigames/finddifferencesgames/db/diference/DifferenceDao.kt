package com.olbigames.finddifferencesgames.db.diference

import androidx.room.*
import com.olbigames.finddifferencesgames.db.game.GameEntity
import com.olbigames.finddifferencesgames.db.game.GameWithDifferences

@Dao
interface DifferenceDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertDifference(difference: DifferenceEntity)

    /*@Query("SELECT * FROM difference WHERE founded")
    suspend fun getFoundedIds(): List<Int>*/

    @Query("UPDATE games_difference SET founded = :foundedIds")
    suspend fun setFoundedIds(foundedIds: List<Int>)

    /*@Query("SELECT * FROM difference WHERE differenceForLevel LIKE :searchLevel ")
    suspend fun findDifference(searchLevel: Int): DifferenceEntity*/

    /**
     * This query will tell Room to query both the [GameEntity] and [DifferenceEntity] tables and handle
     * the object mapping.
     */
    @Transaction
    @Query("SELECT * FROM games WHERE level = :level IN (SELECT DISTINCT(levelId) FROM games_difference)")
    fun getGamesWithDifferences(level: Int): GameWithDifferences

    @Query("UPDATE games_difference SET founded = :founded WHERE differenceId =:differenceId")
    suspend fun founded(founded: Boolean, differenceId: Int)

    @Query("UPDATE games_difference SET anim = :anim WHERE differenceId =:differenceId")
    suspend fun animate(anim: Float, differenceId: Int)

    @Update
    suspend fun updateDifference(difference: DifferenceEntity)
}