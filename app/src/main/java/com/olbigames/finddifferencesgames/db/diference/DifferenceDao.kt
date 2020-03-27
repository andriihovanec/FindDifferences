package com.olbigames.finddifferencesgames.db.diference

import androidx.lifecycle.LiveData
import androidx.room.*
import com.olbigames.finddifferencesgames.db.BaseDao
import com.olbigames.finddifferencesgames.db.game.GameEntity
import com.olbigames.finddifferencesgames.db.game.GameWithDifferences
import com.olbigames.finddifferencesgames.extension.getDistinct

@Dao
abstract class DifferenceDao : BaseDao<DifferenceEntity> {

    /*@Query("SELECT * FROM difference WHERE founded")
    suspend fun getFoundedIds(): List<Int>*/

    @Query("UPDATE games_difference SET founded = :foundedIds")
    abstract suspend fun setFoundedIds(foundedIds: List<Int>)

    /*@Query("SELECT * FROM difference WHERE differenceForLevel LIKE :searchLevel ")
    suspend fun findDifference(searchLevel: Int): DifferenceEntity*/

    /**
     * This query will tell Room to query both the [GameEntity] and [DifferenceEntity] tables and handle
     * the object mapping.
     */
    @Transaction
    @Query("SELECT * FROM games WHERE level = :level IN (SELECT DISTINCT(levelId) FROM games_difference)")
    protected abstract fun getGamesWithDifferencesLive(level: Int): LiveData<GameWithDifferences>

    fun getDistinctGamesWithDifferencesLive(level: Int): LiveData<GameWithDifferences> = getGamesWithDifferencesLive(level).getDistinct()

    @Transaction
    @Query("SELECT * FROM games WHERE level = :level IN (SELECT DISTINCT(levelId) FROM games_difference)")
    abstract suspend fun getGamesWithDifferences(level: Int): GameWithDifferences

    @Query("UPDATE games_difference SET founded = :founded WHERE differenceId =:differenceId")
    abstract suspend fun founded(founded: Boolean, differenceId: Int)

    @Query("UPDATE games_difference SET anim = :anim WHERE differenceId =:differenceId")
    abstract suspend fun animate(anim: Float, differenceId: Int)
}