package com.olbigames.finddifferencesgames.domain.game

import com.olbigames.finddifferencesgames.domain.interactor.UseCase
import com.olbigames.finddifferencesgames.domain.type.None
import javax.inject.Inject

class SubtractOneHint @Inject constructor(
    private val getGameRepository: GameRepository
) : UseCase<None, SubtractOneHint.Params>() {

    override suspend fun run(params: Params) =
        getGameRepository.subtractOneHint(params.level)

    data class Params(val level: Int)
}