package org.firstinspires.ftc.teamcode.opmode

import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.RobotHardware
import org.firstinspires.ftc.teamcode.vision.Vision

@TeleOp class VisionTest : OpMode() {
    private val vision : Vision by lazy { Vision(hardwareMap) }

    override fun init() {
        telemetry = MultipleTelemetry(telemetry, FtcDashboard.getInstance().telemetry)

        vision.init()
        msStuckDetectStop = 5000 // Give some more time for Vuforia/DogeCV to stop running
    }

    override fun init_loop() {
        super.init_loop()
        vision.updateDashboard()
    }

    override fun loop() {
        vision.updateDashboard()

        telemetry.addData("Cube Position", vision.cubePosition)
        telemetry.update()
    }

    override fun stop() {
        vision.cleanup()
    }

}