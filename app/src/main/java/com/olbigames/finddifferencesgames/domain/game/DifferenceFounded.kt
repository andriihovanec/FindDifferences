package com.olbigames.finddifferencesgames.domain.game

import com.olbigames.finddifferencesgames.clean.domain.interactor.UseCase
import com.olbigames.finddifferencesgames.clean.domain.type.None
import javax.inject.Inject

class DifferenceFounded @Inject constructor(
    private val getGameRepository: GetGameRepository
) : UseCase<None, DifferenceFounded.Params>() {

    override suspend fun run(params: Params) =
        getGameRepository.differenceFounded(params.founded, params.differenceId)

    data class Params(val founded: Boolean, val differenceId: Int)
}