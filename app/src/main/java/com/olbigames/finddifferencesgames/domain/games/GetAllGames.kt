package com.olbigames.finddifferencesgames.domain.games

import com.olbigames.finddifferencesgames.clean.domain.interactor.UseCase
import com.olbigames.finddifferencesgames.clean.domain.type.None
import javax.inject.Inject

class GetAllGames @Inject constructor(
    private val gamesRepository: GamesRepository
) : UseCase<List<GameEntity>, None>() {

    override suspend fun run(params: None) = gamesRepository.allGames()
}