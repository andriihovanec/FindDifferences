package com.olbigames.finddifferencesgames.cache.hint

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.olbigames.finddifferencesgames.cache.core.BaseDao
import com.olbigames.finddifferencesgames.data.hint.HiddenHintCache
import com.olbigames.finddifferencesgames.domain.hint.HiddenHintEntity

@Dao
interface HiddenHintDao : BaseDao<HiddenHintEntity>,
    HiddenHintCache {

    @Insert
    override fun insertHiddenHint(hint: HiddenHintEntity)

    @Query("UPDATE games_hidden_hint set hintFounded = :founded WHERE hintId = :hiddenHintId")
    override fun hiddenHintFounded(founded: Boolean, hiddenHintId: Int)
}