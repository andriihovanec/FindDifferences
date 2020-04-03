package com.olbigames.finddifferencesgames.domain.difference

import com.olbigames.finddifferencesgames.domain.game.GameRepository
import com.olbigames.finddifferencesgames.domain.interactor.UseCase
import com.olbigames.finddifferencesgames.domain.type.None
import javax.inject.Inject

class DifferenceFounded @Inject constructor(
    private val getGameRepository: GameRepository
) : UseCase<None, DifferenceFounded.Params>() {

    override suspend fun run(params: Params) =
        getGameRepository.differenceFounded(params.founded, params.differenceId)

    data class Params(val founded: Boolean, val differenceId: Int)
}