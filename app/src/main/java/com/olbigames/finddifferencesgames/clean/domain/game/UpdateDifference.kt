package com.olbigames.finddifferencesgames.clean.domain.game

import com.olbigames.finddifferencesgames.clean.domain.interactor.UseCase
import com.olbigames.finddifferencesgames.clean.domain.type.None
import com.olbigames.finddifferencesgames.db.diference.DifferenceEntity
import javax.inject.Inject

class UpdateDifference @Inject constructor(
    private val getGameRepository: GetGameRepository
) : UseCase<None, UpdateDifference.Params>() {

    override suspend fun run(params: Params) = getGameRepository.updateDifference(params.difference)

    data class Params(val difference: DifferenceEntity)
}