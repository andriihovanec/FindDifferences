package com.olbigames.finddifferencesgames.game.helper

import com.olbigames.finddifferencesgames.db.diference.DifferenceEntity
import com.olbigames.finddifferencesgames.repository.GameRepository
import java.util.*

class DifferencesHelper(private val differences: List<DifferenceEntity>, private val gameRepository: GameRepository) {

    //val gameWithDifferences = gameRepository.getGamesWithDifferences(level)

    suspend fun checkDifference(xx: Int, yy: Int): Int {
        for (i in 0 until differences.count()) {
            if (!differences[i].founded) {
                val xi: Int = differences[i].x
                val yi: Int = differences[i].y
                var ri: Int = differences[i].r
                if (ri < 35) {
                    ri = 35
                }
                ri *= ri
                val d = (xx - xi) * (xx - xi) + (yy - yi) * (yy - yi)
                if (d < ri * 1.5f) {
                    gameRepository.differenceFounded(true, differences[i].differenceId)
                    gameRepository.animateFoundedDifference(1000.0f, differences[i].differenceId)
                    return differences[i].id
                }
            }
        }
        return -1
    }

    suspend fun updateAnim(time: Float) {
        for (i in 0 until differences.count()) {
            if (differences[i].anim != 0.0f && differences[i].anim > time) {
                differences[i].anim -= time
                gameRepository.updateDifference(differences[i])
            } else {
                differences[i].anim = 0.0f
                gameRepository.updateDifference(differences[i])
            }
        }
    }

    fun getAlpha(i: Int): Float {
        return if (differences[i].anim == 0.0f) {
            1.0f
        } else {
            1.0f - differences[i].anim / 1000.0f
        }
    }

    fun getXid(id: Int): Int {
        for (i in 0 until differences.count()) {
            if (differences[i].id == id) {
                return differences[i].x
            }
        }
        return 0
    }

    fun getYid(id: Int): Int {
        for (i in 0 until differences.count()) {
            if (differences[i].id == id) {
                return differences[i].y
            }
        }
        return 0
    }

    fun getRandomDif(level: Int): Int {
        val notFoundedDifferences = ArrayList<DifferenceEntity>()
        val actualDifferences = gameRepository.getGamesWithDifferences(level).differences
        for (i in 0 until actualDifferences.count()) {
            if (!actualDifferences[i].founded) {
                notFoundedDifferences.add(actualDifferences[i])
            }
        }
        if (notFoundedDifferences.size == 0) {
            return -1
        }
        notFoundedDifferences.shuffle()
        return notFoundedDifferences[0].differenceId
    }
}