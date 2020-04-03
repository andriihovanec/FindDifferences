package com.olbigames.finddifferencesgames.domain.game

import com.olbigames.finddifferencesgames.domain.games.GameEntity
import com.olbigames.finddifferencesgames.domain.interactor.UseCase
import javax.inject.Inject

class GetGame @Inject constructor(
    private val getGameRepository: GetGameRepository
) : UseCase<GameEntity, GetGame.Params>() {

    override suspend fun run(params: Params) = getGameRepository.getGame(params.level)

    data class Params(var level: Int)
}