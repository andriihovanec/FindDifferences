package com.olbigames.finddifferencesgames.db.diference

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "difference")
data class DifferenceEntity(
    @PrimaryKey
    @ColumnInfo(name = "differenceId")
    val differenceId: Int,

    @ColumnInfo(name = "gameCreatorLevel")
    val differenceForLevel: Int = 0,

    @ColumnInfo(name = "id")
    var id: Int,

    @ColumnInfo(name = "x")
    var x: Int,

    @ColumnInfo(name = "y")
    var y: Int,

    @ColumnInfo(name = "r")
    var r: Int,

    @ColumnInfo(name = "founded")
    var founded: Boolean = false,

    @ColumnInfo(name = "anim")
    var anim: Float = 0.0f
)