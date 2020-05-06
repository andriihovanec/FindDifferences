package com.olbigames.finddifferencesgames.domain.game

import com.olbigames.finddifferencesgames.domain.interactor.UseCase
import com.olbigames.finddifferencesgames.domain.type.None
import javax.inject.Inject

class GameCompleted @Inject constructor(
    private val getGameRepository: GameRepository
) : UseCase<None, GameCompleted.Params>() {

    override suspend fun run(params: Params) =
        getGameRepository.gameCompleted(params.level, params.isCompleted)

    data class Params(val level: Int, val isCompleted: Boolean)
}