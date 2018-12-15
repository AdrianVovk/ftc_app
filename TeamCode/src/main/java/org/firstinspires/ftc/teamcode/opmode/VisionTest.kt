package org.firstinspires.ftc.teamcode.opmode

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import org.firstinspires.ftc.teamcode.vision.Vision

@Autonomous()
class VisionTest : OpMode() {
    val vision : Vision by lazy { Vision(hardwareMap) }

    override fun init() {
        vision.init()
    }

    override fun loop() {
        vision.updateDashboard()

        telemetry.addData("Cube Position", vision.cubePosition)
        telemetry.update();
    }

    override fun stop() {
        msStuckDetectStop = 2000 // Give some more time for Vuforia/DogeCV to stop running
        vision.cleanup()
    }

}