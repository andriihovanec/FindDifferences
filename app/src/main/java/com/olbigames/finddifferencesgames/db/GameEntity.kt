package com.olbigames.finddifferencesgames.db

import android.net.Uri
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
    @ColumnInfo(name = "uri")
    val uri: String
)
