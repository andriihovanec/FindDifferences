package com.olbigames.finddifferencesgames.extension

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.json.JSONObject

fun Any.asJson(gson: Gson = GsonBuilder().setPrettyPrinting().create()): String {
    val s = gson.toJson(this)
    return s
}

fun Any.toJson(gson: Gson = GsonBuilder().setPrettyPrinting().create()): JSONObject = JSONObject(gson.toJson(this))

fun List<*>.itemsToJson(gson: Gson = GsonBuilder().setPrettyPrinting().create()): List<String> =
    this.map { gson.toJson(it) }.toMutableList()