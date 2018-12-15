package org.firstinspires.ftc.teamcode.vision

import com.disnodeteam.dogecv.detectors.DogeCVDetector
import org.opencv.core.*
import org.opencv.imgproc.Imgproc

// Make the code a little more understandable
typealias Color = Scalar

internal class VisionDetector : DogeCVDetector() {
    var goldPos: Point? = null
    var silverPos: Point? = null
    private var rimY = 0.0
    private var lastTime = 0L

    override fun process(raw: Mat): Mat {
        val messages = mutableListOf<String>() // List of messages to draw on screen
        val returnMat = Mat()

        // Downscale the image
        val downscaled = Mat()
        val size = Size(raw.width() * Conf.DOWNSCALE_FACTOR, raw.height() * Conf.DOWNSCALE_FACTOR)
        Imgproc.resize(raw, downscaled, size)
        downscaled.copyTo(returnMat)

        // Find the gold cubes and silver spheres
        findRim(downscaled, returnMat, messages)
        findGold(downscaled, returnMat, messages)
        findSilver(downscaled, returnMat, messages)

        // Draw the messages & FPS on screen
        drawText(returnMat, messages)
        lastTime = System.currentTimeMillis()

        return returnMat
    }

    private fun findRim(input: Mat, output: Mat, messages: MutableList<String>) {
        val hls = convertColor(input, Imgproc.COLOR_RGB2HLS, Conf.RIM_MIN_HLS, Conf.RIM_MAX_HLS)
        if (Conf.CAM_OUTPUT_SILVER) hls.copyTo(output)

        // TODO
        messages.add("TODO: Rim detect")
    }

    private fun findGold(input: Mat, output: Mat, messages: MutableList<String>) {
        val yuv = convertColor(input, Imgproc.COLOR_RGB2YUV, Conf.GOLD_MIN_YUV, Conf.GOLD_MAX_YUV)
        if (Conf.CAM_OUTPUT_GOLD) yuv.copyTo(output)

        // Detect & draw contours
        val contours = mutableListOf<MatOfPoint>()
        Imgproc.findContours(yuv, contours, Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE)
        Imgproc.drawContours(output, contours, -1, Color(255.0, 255.0, 255.0))

        // TODO
        messages.add("TODO: Gold detect")
    }

    private fun findSilver(input: Mat, output: Mat, messages: MutableList<String>) {
        val hls = convertColor(input, Imgproc.COLOR_RGB2HLS, Conf.SILVER_MIN_HLS, Conf.SILVER_MAX_HLS)
        if (Conf.CAM_OUTPUT_SILVER) hls.copyTo(output)

        // TODO
        messages.add("TODO: Silver detect")
    }

    private fun drawText(returnMat: Mat, messages: MutableList<String>) {
        val fontSize = Imgproc.getTextSize("FPS: XXX", Conf.FONT, Conf.FONT_SCALE, Conf.FONT_THICKNESS, null)

        // Draw the fps counter
        val fps = 1000.0 / (System.currentTimeMillis() - lastTime)
        val fpsPos = Point(returnMat.width() - fontSize.width - 10.0, returnMat.height() - 10.0)
        Imgproc.putText(returnMat, "FPS: $fps", fpsPos, Conf.FONT, Conf.FONT_SCALE, Color(0.0, 255.0, 0.0), Conf.FONT_THICKNESS)

        // Render the messages
        messages.forEachIndexed { i, msg ->
            val origin = Point(10.0, 10.0 + (fontSize.height + 10) * i)
            Imgproc.putText(returnMat, msg, origin, Conf.FONT, Conf.FONT_SCALE, Color(255.0, 0.0, 255.0), Conf.FONT_THICKNESS)
        }
    }

    private fun convertColor(input: Mat, colorCode: Int, limLow: Scalar, limHigh: Scalar): Mat {
        val raw = Mat()
        val thresholded = Mat()
        Imgproc.cvtColor(input, raw, colorCode)
        Core.inRange(raw, limLow, limHigh, thresholded)
        return denoise(thresholded)
    }

    private fun denoise(input: Mat): Mat {
        TODO()
    }

    override fun useDefaults() {
        TODO("I should remove this function")
    }
}