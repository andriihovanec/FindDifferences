package com.olbigames.finddifferencesgames.db.game

import androidx.room.Embedded
import androidx.room.Relation
import com.olbigames.finddifferencesgames.db.diference.DifferenceEntity

data class GameWithDifferences(
    @Embedded
    val gameEntity: GameEntity,
    @Relation(
        parentColumn = "level",
        entityColumn = "differenceForLevel",
        entity = DifferenceEntity::class
    )
    val differences: List<DifferenceEntity>
)