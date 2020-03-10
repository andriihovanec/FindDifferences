package com.olbigames.finddifferencesgames.db.hiden_hint

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "hidden_hint")
data class HiddenHintEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "x")
    var x: Float = 0.0f,
    @ColumnInfo(name = "y")
    var y: Float = 0.0f,
    @ColumnInfo(name = "r")
    var r: Float = 0.0f,
    @ColumnInfo(name = "f")
    var f: Float = 0.0f
) {
}