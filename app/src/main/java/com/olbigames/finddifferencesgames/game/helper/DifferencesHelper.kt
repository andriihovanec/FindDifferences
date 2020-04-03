package com.olbigames.finddifferencesgames.game.helper

import com.olbigames.finddifferencesgames.domain.difference.DifferenceEntity
import com.olbigames.finddifferencesgames.game.GameRenderer
import com.olbigames.finddifferencesgames.ui.game.listeners.GameChangedListener

class DifferencesHelper(
    private val differences: List<DifferenceEntity>,
    private val gameChangedListener: GameChangedListener
) : GameRenderer.DifferencesProvider {

    fun checkDifference(xx: Int, yy: Int): Int {
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
                    updateFoundedDifference(i)
                    return differences[i].id
                }
            }
        }
        return -1
    }

    private fun updateFoundedDifference(id: Int) {
        gameChangedListener.differenceFounded(true, differences[id].differenceId)
        gameChangedListener.updateFoundedCount(differences[id].levelId)
        gameChangedListener.animateFoundedDifference(
            1000.0f,
            differences[id].differenceId
        )
    }

    fun updateAnim(time: Float) {
        for (i in 0 until differences.count()) {
            if (differences[i].anim != 0.0f && differences[i].anim > time) {
                differences[i].anim -= time
                gameChangedListener.updateDifference(differences[i])
            } else {
                differences[i].anim = 0.0f
                gameChangedListener.updateDifference(differences[i])
            }
        }
    }

    fun getAlpha(i: Int): Float {
        return if (differences[i].anim == 0.0f) {
            1.0f
        } else {
            1.0f - (differences[i].anim / 1000.0f)
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

    override fun getDifferences(): List<DifferenceEntity> = differences
}