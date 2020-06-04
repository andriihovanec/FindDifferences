package com.olbigames.finddifferencesgames.domain.hint

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
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
    val hintId: Int,
    @ColumnInfo(name = "levelId")
    val levelId: Int,
    @ColumnInfo(name = "x")
    var hintCoordinateAxisX: Float,
    @ColumnInfo(name = "y")
    var hintCoordinateAxisY: Float,
    @ColumnInfo(name = "radius")
    val radius: Float,
    @ColumnInfo(name = "hintFounded")
    val founded: Boolean
)