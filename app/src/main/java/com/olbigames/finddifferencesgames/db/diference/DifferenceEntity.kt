package com.olbigames.finddifferencesgames.db.diference

import androidx.room.*
import com.google.gson.annotations.SerializedName
import com.olbigames.finddifferencesgames.db.game.GameEntity

@Entity(tableName = "games_difference",
    foreignKeys = [
        ForeignKey(entity = GameEntity::class, parentColumns = ["level"], childColumns = ["levelId"])
    ],
    indices = [Index("levelId")])
data class DifferenceEntity(
    @PrimaryKey
    @ColumnInfo(name = "differenceId")
    @SerializedName("differenceId")
    val differenceId: Int,

    @SerializedName("levelId")
    @ColumnInfo(name = "levelId")
    val levelId: Int = 0,

    @SerializedName("id")
    @ColumnInfo(name = "id")
    var id: Int,

    @SerializedName("x")
    @ColumnInfo(name = "x")
    var x: Int,

    @SerializedName("y")
    @ColumnInfo(name = "y")
    var y: Int,

    @SerializedName("r")
    @ColumnInfo(name = "r")
    var r: Int,

    @SerializedName("founded")
    @ColumnInfo(name = "founded")
    var founded: Boolean = false,

    @SerializedName("anim")
    @ColumnInfo(name = "anim")
    var anim: Float = 0.0f
)