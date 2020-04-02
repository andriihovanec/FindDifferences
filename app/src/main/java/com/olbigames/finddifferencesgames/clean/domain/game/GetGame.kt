package com.olbigames.finddifferencesgames.clean.domain.game

import com.olbigames.finddifferencesgames.clean.domain.interactor.UseCase
import com.olbigames.finddifferencesgames.db.game.GameEntity
import javax.inject.Inject

class GetGame @Inject constructor(
    private val getGameRepository: GetGameRepository
) : UseCase<GameEntity, GetGame.Params>() {

    override suspend fun run(params: Params) = getGameRepository.getGame(params.level)

    data class Params(var level: Int)
}