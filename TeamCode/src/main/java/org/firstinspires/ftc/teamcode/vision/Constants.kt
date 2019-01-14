package org.firstinspires.ftc.teamcode.vision

import com.acmerobotics.dashboard.config.Config
import org.opencv.core.Core
import org.opencv.core.Scalar

enum class InputCamera {
    REAR, FRONT, WEBCAM
}

enum class OutputType {
    NORMAL, GOLD, SILVER, RIM
}

@Config("Vision")
object Constants {
    // General
    @JvmField
    var DOWNSCALE_FACTOR = 0.8
    @JvmField
    var INPUT_CAM = InputCamera.WEBCAM
    @JvmField
    var OUTPUT_VIEW = OutputType.NORMAL
    @JvmField
    var SHOW_UNFILTERED_CONTOURS = false
    @JvmField
    var MIN_Y = 150.0
    @JvmField
    var MIN_DETECT_DISTANCE = 50.0

    // Gold data
    var GOLD_MIN_YUV = Scalar(100.0, 0.0, 100.0)
    var GOLD_MAX_YUV = Scalar(255.0, 100.0, 200.0)
    @JvmField
    var MIN_GOLD_AREA_UNSCALED = 1000
    val MIN_GOLD_AREA: Double
        get() = MIN_GOLD_AREA_UNSCALED * DOWNSCALE_FACTOR * DOWNSCALE_FACTOR
    @JvmField
    var MAX_GOLD_CIRCULARITY = 2.0

    // Silver data
    @JvmField
    var SILVER_MIN_L = 185.0
    val SILVER_MIN_HLS: Scalar
        get() = Scalar(0.0, SILVER_MIN_L, 0.0)
    var SILVER_MAX_HLS = Scalar(255.0, 255.0, 255.0)
    @JvmField
    var MIN_SILVER_AREA_UNSCALED = 900
    val MIN_SILVER_AREA: Double
       get() = MIN_SILVER_AREA_UNSCALED * DOWNSCALE_FACTOR * DOWNSCALE_FACTOR
    @JvmField
    var MIN_SILVER_ARCLEN = 90
    @JvmField
    var MIN_SILVER_CIRCULARITY = 0.6

    // Denoise function
    @JvmField
    var EROSION_ITERATIONS = 3
    @JvmField
    var BLUR_RADIUS_BASE = 36.0
    val BLUR_RADIUS: Double
        get() = BLUR_RADIUS_BASE * DOWNSCALE_FACTOR

    // Font
    @JvmField
    var FONT = Core.FONT_HERSHEY_COMPLEX_SMALL
    @JvmField
    var FONT_SCALE = 1.3
    @JvmField
    var FONT_THICKNESS = 2

}
