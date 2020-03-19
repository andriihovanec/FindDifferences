package com.olbigames.finddifferencesgames.repository

import com.google.firebase.database.DatabaseReference
import com.olbigames.finddifferencesgames.db.diference.DifferenceDao
import com.olbigames.finddifferencesgames.db.game.GameDao
import com.olbigames.finddifferencesgames.db.game.GameEntity
import com.olbigames.finddifferencesgames.db.hiden_hint.HiddenHintsDao

class GameRepository(
    private val gameDao: GameDao,
    private val differenceDao: DifferenceDao,
    private val hiddenHintsDao: HiddenHintsDao
) {
    private lateinit var database: DatabaseReference
    suspend fun findGame(level: String): GameEntity {
        return gameDao.findGame(level)
    }

    /*suspend fun getDifference(searchLevel: Int): DifferenceEntity {
        return differenceDao.findDifference(searchLevel)
    }*/

    suspend fun addFoundedDifferenceId(id: Int) {
        //val foundedList = differenceDao.getFoundedIds().toMutableList()
        //foundedList.add(id)
        //differenceDao.setFoundedIds(foundedList)
    }

    fun addHint(i: Int) {
        //hiddenHintsDao.insertHiddenHint(i)
    }

    fun setHiddenHintFounded(level: Any) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun subtractOneHint() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}