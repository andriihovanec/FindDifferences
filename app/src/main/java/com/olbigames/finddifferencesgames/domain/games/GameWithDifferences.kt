package com.olbigames.finddifferencesgames.domain.games

import androidx.room.Embedded
import androidx.room.Relation
import com.olbigames.finddifferencesgames.clean.domain.game.DifferenceEntity
import com.olbigames.finddifferencesgames.clean.domain.games.GameEntity

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