package com.olbigames.finddifferencesgames.domain.game

import com.olbigames.finddifferencesgames.domain.interactor.UseCase
import javax.inject.Inject

class HiddenHintCount @Inject constructor(
    private val getGameRepository: GameRepository
) : UseCase<Int, HiddenHintCount.Params>() {

    override suspend fun run(params: Params) = getGameRepository.hiddenHintCount(params.level)

    data class Params(val level: Int)
}