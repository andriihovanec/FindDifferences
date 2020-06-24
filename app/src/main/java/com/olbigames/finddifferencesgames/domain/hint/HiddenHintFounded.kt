package com.olbigames.finddifferencesgames.domain.hint

import com.olbigames.finddifferencesgames.domain.game.GameRepository
import com.olbigames.finddifferencesgames.domain.interactor.UseCase
import com.olbigames.finddifferencesgames.domain.type.None
import javax.inject.Inject

class HiddenHintFounded @Inject constructor(
    private val gameRepository: GameRepository
) : UseCase<None, HiddenHintFounded.Params>() {

    override suspend fun run(params: Params) = gameRepository.hiddenHintFounded(params.founded, params.hintId)

    data class Params(val founded: Boolean, val hintId: Int)
}