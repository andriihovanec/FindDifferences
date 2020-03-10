package com.olbigames.finddifferencesgames.db.game

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "game_level")
class GameEntity(
    @PrimaryKey
    @ColumnInfo(name = "level")
    val level: String,
    @ColumnInfo(name = "imageName")
    val filename: String,
    @ColumnInfo(name = "pathToMainFile")
    val pathToMainFile: String,
    @ColumnInfo(name = "pathToDifferentFile")
    val pathToDifferentFile: String
    //@ColumnInfo(name = "differenceCount")
    //val differenceCount: String,
    //@ColumnInfo(name = "hintCount")
    //val hintCount: String
)
