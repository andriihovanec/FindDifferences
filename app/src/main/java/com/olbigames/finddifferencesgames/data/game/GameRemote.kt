package com.olbigames.finddifferencesgames.data.game

import java.io.File

interface GameRemote {
    fun downloadGameImage(differenceStorePath: String, file: File?)
}