package com.olbigames.finddifferencesgames.domain.difference

import com.google.gson.annotations.SerializedName

data class DifferencesListFromJson(
    @SerializedName("differences") val differences: List<DifferenceEntity>
)