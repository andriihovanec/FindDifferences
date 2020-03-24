package com.olbigames.finddifferencesgames.db.game

import androidx.room.Embedded
import androidx.room.Relation
import com.olbigames.finddifferencesgames.db.diference.DifferenceEntity

/**
 * This class captures the relationship between a [GameEntity] and a games [DifferenceEntity], which is
 * used by Room to fetch the related entities.
 */
data class GameWithDifferences(
    @Embedded
    val gameEntity: GameEntity,

    @Relation(
        parentColumn = "level",
        entityColumn = "levelId",
        entity = DifferenceEntity::class
    )
    val differences: List<DifferenceEntity>
)