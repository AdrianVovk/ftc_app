package org.firstinspires.ftc.teamcode.vision

import com.disnodeteam.dogecv.detectors.DogeCVDetector
import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import kotlin.math.roundToInt

// Make the code a little more understandable
typealias Color = Scalar

internal class VisionDetector : DogeCVDetector() {
    var goldPos: Point? = null
    var silverPos: Point? = null
    private var lastTime = 0L

    override fun process(raw: Mat): Mat {
        val messages = mutableListOf<String>() // List of messages to draw on screen
        val returnMat = Mat()

        // Downscale the image
        val downscaled = Mat()
        val size = Size(raw.width() * Constants.DOWNSCALE_FACTOR, raw.height() * Constants.DOWNSCALE_FACTOR)
        Imgproc.resize(raw, downscaled, size)
        downscaled.copyTo(returnMat)

        // Find the gold cubes and silver spheres
        findGold(downscaled, returnMat, messages)
        findSilver(downscaled, returnMat, messages)

        // Draw the y value
        val cutoffLeft = Point(0.0, Constants.MIN_Y)
        val cutoffRight = Point(returnMat.width().toDouble(), Constants.MIN_Y)
        Imgproc.line(returnMat, cutoffLeft, cutoffRight, Color(255.0, 255.0, 255.0), 3)

        // Draw the messages & FPS on screen
        drawText(returnMat, messages)
        lastTime = System.currentTimeMillis()

        return returnMat
    }

    private fun findGold(input: Mat, output: Mat, messages: MutableList<String>) {
        val yuv = convertColor(input, Imgproc.COLOR_RGB2YUV, Constants.GOLD_MIN_YUV, Constants.GOLD_MAX_YUV)
        if (Constants.OUTPUT_VIEW == OutputType.GOLD) yuv.copyTo(output)

        // Detect & draw contours
        val contours = mutableListOf<MatOfPoint>()
        Imgproc.findContours(yuv, contours, Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE)
        if (Constants.SHOW_UNFILTERED_CONTOURS)
            Imgproc.drawContours(output, contours, -1, Color(0.0, 128.0, 0.0), 3)

        val filtered = contours.filter {
            val area = Imgproc.contourArea(it)
            val arcLen = Imgproc.arcLength(MatOfPoint2f(*it.toArray()), true)
            val circularity = 4 * Math.PI * area / (arcLen * arcLen)
            val detectY = it.toPoint().y > Constants.MIN_Y
            detectY && area >= Constants.MIN_GOLD_AREA && circularity <= Constants.MAX_GOLD_CIRCULARITY
        }
        Imgproc.drawContours(output, filtered, -1, Color(0.0, 255.0, 0.0), 3)

        if (filtered.isEmpty()) {
            messages.add("No gold minerals detected")
            goldPos = null
            return
        }

        goldPos = calculateAveragePoint(filtered)
        Imgproc.circle(output, goldPos, 5, Color(0.0, 255.0, 0.0), 3)
        messages.add("Gold: ${goldPos?.x}, ${goldPos?.y}")
    }

    private fun findSilver(input: Mat, output: Mat, messages: MutableList<String>) {
        val hls = convertColor(input, Imgproc.COLOR_RGB2HLS, Constants.SILVER_MIN_HLS, Constants.SILVER_MAX_HLS)
        if (Constants.OUTPUT_VIEW == OutputType.SILVER) hls.copyTo(output)

        val contours = mutableListOf<MatOfPoint>()
        Imgproc.findContours(hls, contours, Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE)
        if (Constants.SHOW_UNFILTERED_CONTOURS)
            Imgproc.drawContours(output, contours, -1, Color(128.0, 0.0, 0.0), 3)

        val filtered = contours.filter {
            val area = Imgproc.contourArea(it)
            val arcLen = Imgproc.arcLength(MatOfPoint2f(*it.toArray()), true)
            val circularity = 4 * Math.PI * area / (arcLen * arcLen)
            val detectY = it.toPoint().y > Constants.MIN_Y
            detectY && area >= Constants.MIN_SILVER_AREA && arcLen >= Constants.MIN_SILVER_ARCLEN && circularity > Constants.MIN_SILVER_CIRCULARITY
        }
        Imgproc.drawContours(output, filtered, -1, Color(255.0, 0.0, 0.0), 3)

        if (filtered.isEmpty()) {
            messages.add("No silver minerals detected")
            silverPos = null
            return
        }

        silverPos = calculateAveragePoint(filtered)
        Imgproc.circle(output, silverPos, 5, Color(255.0, 0.0, 0.0), 3)
        messages.add("Silver: ${silverPos?.x}, ${silverPos?.y}")
    }

    private fun drawText(returnMat: Mat, messages: MutableList<String>) {
        val fontSize = Imgproc.getTextSize("FPS: XX", Constants.FONT, Constants.FONT_SCALE, Constants.FONT_THICKNESS, null)

        // Draw the fps counter
        val fps = (1000.0 / (System.currentTimeMillis() - lastTime)).roundToInt()
        val fpsPos = Point(returnMat.width() - fontSize.width - 20.0, returnMat.height() - 10.0)
        Imgproc.putText(returnMat, "FPS: $fps", fpsPos, Constants.FONT, Constants.FONT_SCALE, Color(0.0, 255.0, 0.0), Constants.FONT_THICKNESS)

        // Render the messages
        messages.forEachIndexed { i, msg ->
            val origin = Point(10.0, 20.0 + (fontSize.height + 10) * i)
            Imgproc.putText(returnMat, msg, origin, Constants.FONT, Constants.FONT_SCALE, Color(255.0, 0.0, 255.0), Constants.FONT_THICKNESS)
        }
    }

    ///////////
    // Utils //
    ///////////

    private fun convertColor(input: Mat, colorCode: Int, limLow: Scalar, limHigh: Scalar): Mat {
        val raw = Mat()
        val thresholded = Mat()
        Imgproc.cvtColor(input, raw, colorCode)
        Core.inRange(raw, limLow, limHigh, thresholded)
        return denoise(thresholded)
    }

    private fun denoise(input: Mat): Mat {
        val blurred = Mat()
        val eroded = Mat()

        val erodeKernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, Size(11.0, 11.0))
        Imgproc.blur(input, blurred, Size(Constants.BLUR_RADIUS, Constants.BLUR_RADIUS))
        Imgproc.erode(blurred, eroded, erodeKernel, Point(0.0, 0.0), Constants.EROSION_ITERATIONS)

        return eroded
    }

    private fun MatOfPoint.toPoint(): Point {
        val moments = Imgproc.moments(this)
        return Point(moments.m10 / moments.m00, moments.m01 / moments.m00)
    }

    private fun calculateAveragePoint(contours: List<MatOfPoint>): Point {
        val points = contours.map { it.toPoint() }

        var sumX = 0.0
        var sumY = 0.0
        for (p in points) {
            sumX += p.x
            sumY += p.y
        }

        return Point(sumX / points.size, sumY / points.size)
    }

    /////////////
    // Useless //
    /////////////

    override fun useDefaults() {
        // NOTE: This function is useless
    }
}