package com.olbigames.finddifferencesgames.repository

sealed class SurfaceStatus {
    object Start : SurfaceStatus()
    object Resume : SurfaceStatus()
    object Pause : SurfaceStatus()
    object Destroy : SurfaceStatus()
}