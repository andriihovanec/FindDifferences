package com.olbigames.finddifferencesgames.db.diference

import androidx.room.*

@Dao
interface DifferenceDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertDifference(difference: DifferenceEntity)

    /*@Query("SELECT * FROM difference WHERE founded")
    suspend fun getFoundedIds(): List<Int>*/

    @Query("UPDATE difference SET founded = :foundedIds")
    suspend fun setFoundedIds(foundedIds: List<Int>)

    /*@Query("SELECT * FROM difference WHERE differenceForLevel LIKE :searchLevel ")
    suspend fun findDifference(searchLevel: Int): DifferenceEntity*/

    /*@Query("UPDATE difference SET founded = founded + 1")
    suspend fun updateFounded()*/
}