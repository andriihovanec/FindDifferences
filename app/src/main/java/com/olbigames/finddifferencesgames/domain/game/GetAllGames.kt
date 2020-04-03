package com.olbigames.finddifferencesgames.domain.game

import com.olbigames.finddifferencesgames.domain.interactor.UseCase
import com.olbigames.finddifferencesgames.domain.type.None
import javax.inject.Inject

class GetAllGames @Inject constructor(
    private val gameRepository: GameRepository
) : UseCase<List<GameEntity>, None>() {

    override suspend fun run(params: None) = gameRepository.allGames()
}