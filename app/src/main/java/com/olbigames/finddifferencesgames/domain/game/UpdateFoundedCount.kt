package com.olbigames.finddifferencesgames.domain.game

import com.olbigames.finddifferencesgames.clean.domain.interactor.UseCase
import com.olbigames.finddifferencesgames.clean.domain.type.None
import javax.inject.Inject

class UpdateFoundedCount @Inject constructor(
    private val getGameRepository: GetGameRepository
) : UseCase<None, UpdateFoundedCount.Params>() {

    override suspend fun run(params: Params) = getGameRepository.updateFoundedCount(params.level)

    data class Params(val level: Int)
}