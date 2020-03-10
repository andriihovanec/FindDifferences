package com.olbigames.finddifferencesgames.db.diference

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "difference")
data class DifferenceEntity(
    @PrimaryKey
    @ColumnInfo(name = "level")
    val level: String,
    @ColumnInfo(name = "id")
    var id: List<Int>,
    @ColumnInfo(name = "x")
    var x: List<Int>,
    @ColumnInfo(name = "y")
    var y: List<Int>,
    @ColumnInfo(name = "r")
    var r: List<Int>,
    @ColumnInfo(name = "founded")
    var founded: Int = 0,
    @ColumnInfo(name = "anim")
    var anim: Float = 0.0f,
    @ColumnInfo(name = "count")
    var count: Int = 0

) {
}