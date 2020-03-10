package com.olbigames.finddifferencesgames.db.diference

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface DifferenceDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertDifference(difference: DifferenceEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertId(id: DifferenceEntity)

    @Query("SELECT * FROM difference WHERE level LIKE :searchLevel ")
    suspend fun findDifference(searchLevel: String): DifferenceEntity

    @Query("UPDATE difference SET founded = founded + 1")
    suspend fun updateFounded()
}