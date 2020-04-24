package com.olbigames.finddifferencesgames.domain.game

import com.olbigames.finddifferencesgames.domain.difference.DifferenceEntity
import com.olbigames.finddifferencesgames.domain.interactor.UseCase
import com.olbigames.finddifferencesgames.domain.type.Either
import com.olbigames.finddifferencesgames.domain.type.Failure
import com.olbigames.finddifferencesgames.domain.type.None
import javax.inject.Inject

class ResetGameDifferences @Inject constructor(
    private val getGameRepository: GameRepository
) : UseCase<None, ResetGameDifferences.Params>() {

    override suspend fun run(params: Params): Either<Failure, None> {
        params.differences.forEach { difference ->
            getGameRepository.differenceFounded(params.founded, difference.differenceId)
        }
        return Either.Right(None())
    }

    data class Params(val differences: List<DifferenceEntity>, val founded: Boolean)
}