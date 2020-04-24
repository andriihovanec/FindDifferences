package com.olbigames.finddifferencesgames.domain.game

import com.olbigames.finddifferencesgames.domain.interactor.UseCase
import com.olbigames.finddifferencesgames.domain.type.None
import javax.inject.Inject

class ResetFoundedCount @Inject constructor(
    private val getGameRepository: GameRepository
) : UseCase<None, ResetFoundedCount.Params>() {

    override suspend fun run(params: Params) = getGameRepository.resetFoundedCount(params.level)

    data class Params(val level: Int)
}