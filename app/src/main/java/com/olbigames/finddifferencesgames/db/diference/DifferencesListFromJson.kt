package com.olbigames.finddifferencesgames.db.diference

import com.google.gson.annotations.SerializedName

data class DifferencesListFromJson(
    @SerializedName("differences") val differences: List<DifferenceEntity>
)