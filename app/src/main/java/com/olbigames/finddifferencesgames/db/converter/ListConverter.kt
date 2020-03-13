package com.olbigames.finddifferencesgames.db.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ListConverter {
    @TypeConverter
    fun fromList(stat: List<Int>): String {
        return Gson().toJson(stat)
    }

    @TypeConverter
    fun toList(jsonImages: String): List<Int> {
        val notesType = object : TypeToken<List<Int>>() {}.type
        return Gson().fromJson<List<Int>>(jsonImages, notesType)
    }
}