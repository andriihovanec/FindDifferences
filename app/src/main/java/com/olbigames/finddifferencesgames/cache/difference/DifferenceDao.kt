package com.olbigames.finddifferencesgames.cache.difference

import androidx.room.*
import com.olbigames.finddifferencesgames.cache.core.BaseDao
import com.olbigames.finddifferencesgames.domain.game.DifferenceEntity
import com.olbigames.finddifferencesgames.domain.games.GameWithDifferences

@Dao
abstract class DifferenceDao : BaseDao<DifferenceEntity>,
    DifferenceCache {

    @Insert
    abstract override fun insertDifference(difference: DifferenceEntity)

    @Query("UPDATE games_difference SET founded = :foundedIds")
    abstract suspend fun setFoundedIds(foundedIds: List<Int>)

    @Transaction
    @Query("SELECT * FROM games WHERE level = :level IN (SELECT DISTINCT(levelId) FROM games_difference)")
    abstract override fun getGameWithDifferences(level: Int): GameWithDifferences

    @Query("UPDATE games_difference SET founded = :founded WHERE differenceId =:differenceId")
    abstract override fun differenceFounded(founded: Boolean, differenceId: Int)

    @Update
    abstract override fun updateDifference(difference: DifferenceEntity)

    @Query("UPDATE games_difference SET anim = :anim WHERE differenceId =:differenceId")
    abstract override fun animateFoundedDifference(anim: Float, differenceId: Int)
}