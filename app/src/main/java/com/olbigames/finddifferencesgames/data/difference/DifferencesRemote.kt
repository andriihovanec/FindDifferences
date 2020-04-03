package com.olbigames.finddifferencesgames.data.difference

import java.io.File

interface DifferencesRemote {
    fun downloadGameDifferences(differenceStorePath: String, file: File?)
}