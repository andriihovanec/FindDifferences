package com.olbigames.finddifferencesgames.db.game

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "games")
class GameEntity(
    @PrimaryKey
    @ColumnInfo(name = "level")
    val level: Int,
    @ColumnInfo(name = "imageName")
    val filename: String,
    @ColumnInfo(name = "pathToMainFile")
    val pathToMainFile: String,
    @ColumnInfo(name = "pathToDifferentFile")
    val pathToDifferentFile: String,
    @ColumnInfo(name = "count")
    var differentCount: Int = 0,
    @ColumnInfo(name = "foundedCount")
    var foundedCount: Int = 0
)
