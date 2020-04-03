package com.olbigames.finddifferencesgames.domain.games

import com.olbigames.finddifferencesgames.clean.domain.interactor.UseCase
import com.olbigames.finddifferencesgames.clean.domain.type.None
import com.olbigames.finddifferencesgames.clean.domain.game.DifferenceEntity
import javax.inject.Inject

class AddDifference @Inject constructor(
    private val gamesRepository: GamesRepository
) : UseCase<None, AddDifference.Params>() {

    override suspend fun run(params: Params) = gamesRepository.insertDifference(params.difference)

    data class Params(val difference: DifferenceEntity)
}