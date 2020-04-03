package com.olbigames.finddifferencesgames.domain.games

import com.olbigames.finddifferencesgames.domain.interactor.UseCase
import com.olbigames.finddifferencesgames.domain.type.None
import javax.inject.Inject

class AddGame @Inject constructor(
    private val gamesRepository: GamesRepository
) : UseCase<None, AddGame.Params>() {

    override suspend fun run(params: Params) = gamesRepository.insertGame(params.game)

    data class Params(val game: GameEntity)
}