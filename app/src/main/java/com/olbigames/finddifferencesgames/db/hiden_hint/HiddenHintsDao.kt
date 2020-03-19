package com.olbigames.finddifferencesgames.db.hiden_hint

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy

@Dao
interface HiddenHintsDao {

    //@Insert(onConflict = OnConflictStrategy.IGNORE)
    //suspend fun insertHiddenHint(hiddenHint: HiddenHintEntity)
}