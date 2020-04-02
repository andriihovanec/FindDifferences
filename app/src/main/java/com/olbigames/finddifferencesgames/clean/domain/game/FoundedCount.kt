package com.olbigames.finddifferencesgames.clean.domain.game

import com.olbigames.finddifferencesgames.clean.domain.interactor.UseCase
import javax.inject.Inject

class FoundedCount @Inject constructor(
    private val getGameRepository: GetGameRepository
) : UseCase<Int, FoundedCount.Params>() {

    override suspend fun run(params: Params) = getGameRepository.foundedCount(params.level)

    data class Params(val level: Int)
}