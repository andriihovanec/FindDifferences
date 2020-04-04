package com.olbigames.finddifferencesgames.renderer.helper

import com.olbigames.finddifferencesgames.domain.difference.DifferenceEntity
import com.olbigames.finddifferencesgames.domain.difference.UpdateDifference

class DifferencesHelper(val updateDifferenceUseCase: UpdateDifference) {

    fun checkDifference(differences: List<DifferenceEntity>, xx: Int, yy: Int): Int {
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
                    return differences[i].id
                }
            }
        }
        return -1
    }

    fun updateAnim(differences: List<DifferenceEntity>, time: Float) {
        for (i in 0 until differences.count()) {
            if (differences[i].anim != 0.0f && differences[i].anim > time) {
                differences[i].anim -= time
                updateDifferenceUseCase(UpdateDifference.Params(differences[i]))
            } else {
                differences[i].anim = 0.0f
                updateDifferenceUseCase(UpdateDifference.Params(differences[i]))
            }
        }
    }

    fun getAlpha(differences: List<DifferenceEntity>, i: Int): Float {
        return if (differences[i].anim == 0.0f) {
            1.0f
        } else {
            1.0f - (differences[i].anim / 1000.0f)
        }
    }

    fun getXid(differences: List<DifferenceEntity>, id: Int): Int {
        for (i in 0 until differences.count()) {
            if (differences[i].id == id) {
                return differences[i].x
            }
        }
        return 0
    }

    fun getYid(differences: List<DifferenceEntity>, id: Int): Int {
        for (i in 0 until differences.count()) {
            if (differences[i].id == id) {
                return differences[i].y
            }
        }
        return 0
    }

    fun getRandomDif(level: Int): Int {
        /*val notFoundedDifferences = ArrayList<DifferenceEntity>()
        val actualDifferences = gameRepository.getGameWithDifferences(level).value!!.differences
        for (i in 0 until actualDifferences.count()) {
            if (!actualDifferences[i].founded) {
                notFoundedDifferences.add(actualDifferences[i])
            }
        }
        if (notFoundedDifferences.size == 0) {
            return -1
        }
        notFoundedDifferences.shuffle()
        return notFoundedDifferences[0].differenceId*/
        return -1
    }
}