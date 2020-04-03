package com.olbigames.finddifferencesgames.domain.game

import com.olbigames.finddifferencesgames.domain.interactor.UseCase
import com.olbigames.finddifferencesgames.domain.type.None
import javax.inject.Inject

class UpdateDifference @Inject constructor(
    private val getGameRepository: GetGameRepository
) : UseCase<None, UpdateDifference.Params>() {

    override suspend fun run(params: Params) = getGameRepository.updateDifference(params.difference)

    data class Params(val difference: DifferenceEntity)
}