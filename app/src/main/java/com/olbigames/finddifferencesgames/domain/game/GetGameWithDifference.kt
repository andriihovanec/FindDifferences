package com.olbigames.finddifferencesgames.domain.game

import com.olbigames.finddifferencesgames.clean.domain.interactor.UseCase
import com.olbigames.finddifferencesgames.clean.domain.games.GameWithDifferences
import javax.inject.Inject

class GetGameWithDifference @Inject constructor(
    private val getGameRepository: GetGameRepository
) : UseCase<GameWithDifferences, GetGameWithDifference.Params>() {

    override suspend fun run(params: Params) =
        getGameRepository.getGameWithDifferences(params.level)

    data class Params(var level: Int)
}