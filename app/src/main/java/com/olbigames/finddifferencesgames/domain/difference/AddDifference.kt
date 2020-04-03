package com.olbigames.finddifferencesgames.domain.difference

import com.olbigames.finddifferencesgames.domain.game.GameRepository
import com.olbigames.finddifferencesgames.domain.interactor.UseCase
import com.olbigames.finddifferencesgames.domain.type.None
import javax.inject.Inject

class AddDifference @Inject constructor(
    private val gameRepository: GameRepository
) : UseCase<None, AddDifference.Params>() {

    override suspend fun run(params: Params) = gameRepository.insertDifference(params.difference)

    data class Params(val difference: DifferenceEntity)
}