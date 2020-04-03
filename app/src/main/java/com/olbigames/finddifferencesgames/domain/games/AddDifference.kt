package com.olbigames.finddifferencesgames.domain.games

import com.olbigames.finddifferencesgames.domain.game.DifferenceEntity
import com.olbigames.finddifferencesgames.domain.interactor.UseCase
import com.olbigames.finddifferencesgames.domain.type.None
import javax.inject.Inject

class AddDifference @Inject constructor(
    private val gamesRepository: GamesRepository
) : UseCase<None, AddDifference.Params>() {

    override suspend fun run(params: Params) = gamesRepository.insertDifference(params.difference)

    data class Params(val difference: DifferenceEntity)
}