package com.olbigames.finddifferencesgames.domain.game

import com.olbigames.finddifferencesgames.domain.interactor.UseCase
import com.olbigames.finddifferencesgames.domain.type.None
import javax.inject.Inject

class AddGame @Inject constructor(
    private val gameRepository: GameRepository
) : UseCase<None, AddGame.Params>() {

    override suspend fun run(params: Params) = gameRepository.insertGame(params.game)

    data class Params(val game: GameEntity)
}