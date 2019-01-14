package org.firstinspires.ftc.teamcode.vision

import com.acmerobotics.dashboard.FtcDashboard
import com.disnodeteam.dogecv.CameraViewDisplay
import com.disnodeteam.dogecv.Dogeforia
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.robotcore.external.hardware.camera.CameraName
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer

class Vision(private val hardware: HardwareMap) {

    enum class CubePosition {
        LEFT, RIGHT, CENTER, UNKNOWN
    }

    //////////////////
    // Vision Setup //
    //////////////////

    private val params = VuforiaLocalizer.Parameters().apply {
        vuforiaLicenseKey = "AcyPCov/////AAABmSDbwrb0ZEiDttYV7rcRVBJZck+hOLvbd6tGd1xaHSf4b1UiFh1OQagZxNLol03/mVebiaI/2jo5YvXKrDdHTraBV9CUfHDePhGd4ASNQ56gA7RNGc+v7GaZbVxvc3mPlMkzP/lLrxEvSIc6l3b43B1IyQGPNwjh8Xky3ClKkVVA/GoYjMZCxXyba8cQliDhuHVZ1AB9lBd4fUjtLOy86tbL6EbnAu9+NJeOnhOPN8HOYruqBu6UvL39kKqrSBCZPxiwrxUuSvZSL8hiPV92Ad3r74el5TvvhO/OZVgfQw9dReHK3Ef+KNzZWRv3jcrel2BTYRCD9nugBmF7GnkHymeZkmbIchW/OcWgxMUbf1vF"
        fillCameraMonitorViewParent = true

        if (Constants.INPUT_CAM == InputCamera.WEBCAM)
            cameraName = hardware["Webcam 1"] as CameraName
        else
            cameraDirection = if (Constants.INPUT_CAM == InputCamera.REAR)
                VuforiaLocalizer.CameraDirection.BACK
            else
                VuforiaLocalizer.CameraDirection.FRONT
    }

    private val vision = VisionDetector().apply {
        init(hardware.appContext, CameraViewDisplay.getInstance(), 0, true)
    }

    private val vuforia = Dogeforia(params).apply {
        enableConvertFrameToBitmap()
        enableDogeCV()
        setDogeCVDetector(vision)
    }

    ////////////////
    // Public API //
    ////////////////

    fun init() {
        vuforia.start()
    }

    /**
     * Updates the FTC Dashboard feed
     */
    fun updateDashboard() {
        val dash = FtcDashboard.getInstance() ?: return
        val frame = vision.rawView.bitmap ?: return
        dash.imageQuality = 75
        dash.sendImage(frame)
    }

    fun cleanup() {
        vuforia.stop()
    }

    val cubePosition: CubePosition
        get() = when {
            vision.goldPos == null || vision.silverPos == null ->
                CubePosition.UNKNOWN
            (vision.goldPos!!.x - vision.silverPos!!.x) < -Constants.MIN_DETECT_DISTANCE ->
                CubePosition.LEFT
            (vision.goldPos!!.x - vision.silverPos!!.x) > Constants.MIN_DETECT_DISTANCE ->
                CubePosition.RIGHT
            else ->
                CubePosition.CENTER
        }
}