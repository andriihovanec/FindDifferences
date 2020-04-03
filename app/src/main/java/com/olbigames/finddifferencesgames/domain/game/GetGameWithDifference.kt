package com.olbigames.finddifferencesgames.domain.game

import com.olbigames.finddifferencesgames.domain.interactor.UseCase
import javax.inject.Inject

class GetGameWithDifference @Inject constructor(
    private val getGameRepository: GameRepository
) : UseCase<GameWithDifferences, GetGameWithDifference.Params>() {

    override suspend fun run(params: Params) =
        getGameRepository.getGameWithDifferences(params.level)

    data class Params(var level: Int)
}