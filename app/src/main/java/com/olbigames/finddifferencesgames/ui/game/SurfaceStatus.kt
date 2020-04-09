package com.olbigames.finddifferencesgames.ui.game

sealed class SurfaceStatus {
    object Started: SurfaceStatus()
    object Cleared: SurfaceStatus()
    object Paused: SurfaceStatus()
}