package com.olbigames.finddifferencesgames.clean.domain.games

import com.olbigames.finddifferencesgames.clean.domain.interactor.UseCase
import com.olbigames.finddifferencesgames.clean.domain.type.None
import com.olbigames.finddifferencesgames.db.game.GameEntity
import javax.inject.Inject

class GetAllGames @Inject constructor(
    private val gamesRepository: GamesRepository
) : UseCase<List<GameEntity>, None>() {

    override suspend fun run(params: None) = gamesRepository.allGames()
}