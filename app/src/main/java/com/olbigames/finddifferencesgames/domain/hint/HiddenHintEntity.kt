package com.olbigames.finddifferencesgames.domain.hint

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.olbigames.finddifferencesgames.domain.game.GameEntity

@Entity(
    tableName = "games_hidden_hint",
    foreignKeys = [
        ForeignKey(
            entity = GameEntity::class,
            parentColumns = ["level"],
            childColumns = ["levelId"]
        )
    ]
)
data class HiddenHintEntity(
    @PrimaryKey
    @ColumnInfo(name = "hintId")
    @SerializedName("hintId")
    val hintId: Int,
    @ColumnInfo(name = "levelId")
    @SerializedName("levelId")
    val levelId: Int,
    @ColumnInfo(name = "hintCoordinateAxisX")
    @SerializedName("hintCoordinateAxisX")
    var hintCoordinateAxisX: Int,
    @ColumnInfo(name = "hintCoordinateAxisY")
    @SerializedName("hintCoordinateAxisY")
    var hintCoordinateAxisY: Int,
    @ColumnInfo(name = "radius")
    @SerializedName("radius")
    val radius: Int,
    @ColumnInfo(name = "hintFounded")
    @SerializedName("hintFounded")
    val founded: Boolean
)