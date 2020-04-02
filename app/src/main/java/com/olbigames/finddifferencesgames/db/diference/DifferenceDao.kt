package com.olbigames.finddifferencesgames.db.diference

import androidx.room.*
import com.olbigames.finddifferencesgames.clean.cache.DifferenceCache
import com.olbigames.finddifferencesgames.db.BaseDao
import com.olbigames.finddifferencesgames.db.game.GameWithDifferences

@Dao
abstract class DifferenceDao : BaseDao<DifferenceEntity>, DifferenceCache {

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