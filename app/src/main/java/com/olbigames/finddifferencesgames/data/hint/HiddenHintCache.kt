package com.olbigames.finddifferencesgames.data.hint

import com.olbigames.finddifferencesgames.domain.hint.HiddenHintEntity

interface HiddenHintCache {

    fun insertHiddenHint(hint: HiddenHintEntity)
    fun hiddenHintFounded(founded: Boolean, hiddenHintId: Int)
}