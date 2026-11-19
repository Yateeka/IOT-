package com.csc8223.posefl

val POSES = listOf(
    "Screen Up", "Screen Down", "Portrait Up",
    "Portrait Down", "Landscape Left", "Landscape Right"
)

enum class FeatureConfig { BASELINE, GRAV_NORM, ROBUST }

const val G = 9.81f
const val STD_MAX = 1.5f            // ROBUST config drops shaky windows above this
const val WINDOW_NS = 1_000_000_000L // 1-second windows