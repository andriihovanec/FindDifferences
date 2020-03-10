package com.olbigames.finddifferencesgames.db.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.olbigames.finddifferencesgames.extension.asJson

class ListConverter {
    @TypeConverter
    fun toList(data: String): List<Int> = Gson().fromJson(data, object : TypeToken<List<Int>>() {}.type)

    @TypeConverter
    fun fromList(list: List<Int>): String = list.asJson()
}