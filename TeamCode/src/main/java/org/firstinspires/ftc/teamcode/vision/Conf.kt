package org.firstinspires.ftc.teamcode.vision

import com.acmerobotics.dashboard.config.Config
import org.opencv.core.Core
import org.opencv.core.Scalar
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc
import kotlin.math.roundToInt

@Config
internal object Conf {
    // General options
    var DOWNSCALE_FACTOR = 1.0

    // Gold color data
    var GOLD_MIN_YUV = Scalar(100.0, 0.0, 100.0)
    var GOLD_MAX_YUV = Scalar(255.0, 100.0, 200.0)

    // Silver color data
    var SILVER_MIN_HLS = Scalar(0.0, 215.0, 0.0)
    var SILVER_MAX_HLS = Scalar(255.0, 255.0, 255.0)

    // Rim color data
    var RIM_MIN_HLS = Scalar(0.0, 0.0, 0.0)
    var RIM_MAX_HLS = Scalar(255.0, 50.0, 255.0)

    // Output camera mode
    var CAM_OUTPUT_GOLD = false
    var CAM_OUTPUT_SILVER = false
    var CAM_OUTPUT_RIM = false

    // Denoise function
    var EROSION_ITERATIONS = 2;
    var ERODE_KERNEL = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, Size(11.0, 11.0))!!
    var BLUR_RADIUS = 36.0 * DOWNSCALE_FACTOR

    // Fond
    var FONT = Core.FONT_HERSHEY_COMPLEX_SMALL
    var FONT_SCALE = 1.0
    var FONT_THICKNESS = 1
}