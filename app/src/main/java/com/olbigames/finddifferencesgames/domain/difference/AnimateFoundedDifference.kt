package com.olbigames.finddifferencesgames.domain.difference

import com.olbigames.finddifferencesgames.domain.game.GameRepository
import com.olbigames.finddifferencesgames.domain.interactor.UseCase
import com.olbigames.finddifferencesgames.domain.type.None
import javax.inject.Inject

class AnimateFoundedDifference @Inject constructor(
    private val getGameRepository: GameRepository
) : UseCase<None, AnimateFoundedDifference.Params>() {

    override suspend fun run(params: Params) =
        getGameRepository.animateFoundedDifference(params.anim, params.differenceId)

    data class Params(val anim: Float, val differenceId: Int)
}