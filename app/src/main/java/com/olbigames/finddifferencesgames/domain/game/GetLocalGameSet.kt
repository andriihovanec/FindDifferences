package com.olbigames.finddifferencesgames.domain.game

import com.olbigames.finddifferencesgames.domain.difference.DifferenceEntity
import com.olbigames.finddifferencesgames.domain.hint.HiddenHintEntity
import com.olbigames.finddifferencesgames.domain.interactor.UseCase
import com.olbigames.finddifferencesgames.domain.type.Either
import com.olbigames.finddifferencesgames.domain.type.Failure
import com.olbigames.finddifferencesgames.utilities.Constants.IMAGE_EXTENSION
import com.olbigames.finddifferencesgames.utilities.LocalDifferencesSet
import javax.inject.Inject

class GetLocalGameSet @Inject constructor(
    private val gameRepository: GameRepository
) : UseCase<List<GameEntity>, GetLocalGameSet.Params>() {

    private var startCount = 0
    private var startHintCount = 0

    override suspend fun run(params: Params): Either<Failure, List<GameEntity>> {
        for (level in params.start..params.end) {
            insertGameInDb(level)
        }
        return gameRepository.allGames()
    }

    private fun insertGameInDb(level: Int) {
        val mainFileName = getFileName(level, 1)
        val differentFileName = getFileName(level, 2)

        gameRepository.insertGame(
            GameEntity(
                level,
                "$mainFileName${IMAGE_EXTENSION}",
                mainFileName,
                differentFileName
            )
        )

        insertDifferenceInDb(level)
        insertHiddenHintInDb(level)
    }

    private fun insertDifferenceInDb(level: Int) {
        val gameDifferences = createDifferenceList(level)
        gameDifferences.forEach { difference ->
            gameRepository.insertDifference(difference)
        }
    }

    private fun createDifferenceList(level: Int): List<DifferenceEntity> {
        val differencesList = mutableListOf<DifferenceEntity>()
        var difference: DifferenceEntity
        val diffCount = 10
        for (id in 1..diffCount) {
            difference = DifferenceEntity(
                "$level$id".toInt(),
                level,
                id,
                LocalDifferencesSet.local_differences_data[startCount + 1],
                LocalDifferencesSet.local_differences_data[startCount + 2],
                LocalDifferencesSet.local_differences_data[startCount + 3],
                false,
                0.0f
            )
            startCount += 4
            differencesList.add(difference)
        }
        return differencesList
    }

    private fun insertHiddenHintInDb(level: Int) {
        val hiddenHint = createHiddenHint(level)
        gameRepository.insertHiddenHint(hiddenHint)
    }

    private fun createHiddenHint(level: Int): HiddenHintEntity {
        val hint = HiddenHintEntity(
            level,
            level,
            LocalDifferencesSet.hidden_hints_data[startHintCount + 1],
            LocalDifferencesSet.hidden_hints_data[startHintCount + 2],
            LocalDifferencesSet.hidden_hints_data[startHintCount + 3],
            false
        )
        startHintCount += 4
        return hint
    }

    private fun getFileName(level: Int, imageSuffix: Int): String {
        return when (level) {
            in 1..9 -> "pic000$level" + "_$imageSuffix"
            in 10..99 -> "pic00$level" + "_$imageSuffix"
            else -> "pic0$level" + "_$imageSuffix"
        }
    }

    data class Params(val start: Int, val end: Int)
}