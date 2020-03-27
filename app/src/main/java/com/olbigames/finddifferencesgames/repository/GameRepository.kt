package com.olbigames.finddifferencesgames.repository

import com.google.firebase.database.DatabaseReference
import com.olbigames.finddifferencesgames.db.diference.DifferenceDao
import com.olbigames.finddifferencesgames.db.diference.DifferenceEntity
import com.olbigames.finddifferencesgames.db.game.GameDao
import com.olbigames.finddifferencesgames.db.hiden_hint.HiddenHintsDao

class GameRepository(
    private val gameDao: GameDao,
    private val differenceDao: DifferenceDao,
    private val hiddenHintsDao: HiddenHintsDao
) {
    private lateinit var database: DatabaseReference
    suspend fun findGame(level: String) = gameDao.findGame(level)

    fun addHint(i: Int) {
        //hiddenHintsDao.insertHiddenHint(i)
    }

    fun setHiddenHintFounded(level: Any) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun subtractOneHint() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    suspend fun getGameWithDifferences(level: Int) = differenceDao.getGamesWithDifferences(level)

    suspend fun differenceFounded(founded: Boolean, differenceId: Int) =
        differenceDao.founded(founded, differenceId)

    suspend fun animateFoundedDifference(anim: Float, differenceId: Int) =
        differenceDao.animate(anim, differenceId)

    suspend fun updateDifference(difference: DifferenceEntity) = differenceDao.update(difference)

    suspend fun updateFoundedCount(level: Int) = gameDao.updateFoundedCount(level)

    fun foundedCount(level: Int) = gameDao.foundedCount(level)
}