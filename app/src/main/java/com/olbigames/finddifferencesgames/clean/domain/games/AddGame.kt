package com.olbigames.finddifferencesgames.clean.domain.games

import com.olbigames.finddifferencesgames.clean.domain.interactor.UseCase
import com.olbigames.finddifferencesgames.clean.domain.type.None
import com.olbigames.finddifferencesgames.db.game.GameEntity
import javax.inject.Inject

class AddGame @Inject constructor(
    private val gamesRepository: GamesRepository
) : UseCase<None, AddGame.Params>() {

    override suspend fun run(params: Params) = gamesRepository.insertGame(params.game)

    data class Params(val game: GameEntity)
}