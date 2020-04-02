package com.olbigames.finddifferencesgames.clean.domain.games

import com.olbigames.finddifferencesgames.clean.domain.interactor.UseCase
import com.olbigames.finddifferencesgames.clean.domain.type.None
import com.olbigames.finddifferencesgames.db.diference.DifferenceEntity
import javax.inject.Inject

class AddDifference @Inject constructor(
    private val gamesRepository: GamesRepository
) : UseCase<None, AddDifference.Params>() {

    data class Params(val difference: DifferenceEntity)

    override suspend fun run(params: Params) = gamesRepository.insertDifference(params.difference)
}