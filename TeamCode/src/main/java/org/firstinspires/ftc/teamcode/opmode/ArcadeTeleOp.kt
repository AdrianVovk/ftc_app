package org.firstinspires.ftc.teamcode.opmode

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.RobotHardware

@TeleOp()
class ArcadeTeleOp : LinearOpMode() {
    private val robot: RobotHardware by lazy { RobotHardware(this) }

    override fun runOpMode() {
        // Some state for toggles
        var g1ADisable = false
        var slowMode = false
        var g1BDisable = false
        var reverseMode = false
        var g2ADisable = false
        var g2XDisable = false
        var intakePosition = 2

        // Wait for the start button
        robot.reset()
        waitForStart()

        // The main loop
        while (opModeIsActive()) {

            // Update the state

            // G1: a (toggle) => slow mode
            if (gamepad1.a && !g1ADisable) slowMode = !slowMode
            g1ADisable = gamepad1.a

            // G1: b (toggle) => reverse
            if (gamepad1.b && !g1BDisable) reverseMode = !reverseMode
            g1BDisable = gamepad1.b

            //G2: a => Intake normal/down
            //G2: x => Intake normal/transfer
            if(gamepad2.a && !g2ADisable)
                intakePosition = if (intakePosition != 0) 0 else 1
            g2ADisable = gamepad2.a
            if(gamepad2.x && !g2XDisable)
                intakePosition = if (intakePosition != 1) 1 else 2
            g2XDisable = gamepad2.x

            // Move the robot

            // G1: Right stick => arcade drive
            val direction = if (reverseMode) -1.0 else 1.0
            val powerScale = if (slowMode) 0.5 else 1.0
            val drive = -gamepad1.right_stick_y * direction
            val turn = gamepad1.right_stick_x.toDouble()
            robot.leftPower = (drive + turn) * powerScale
            robot.rightPower = (drive - turn) * powerScale

            // G1: Left trigger => lift down; Right trigger => lift up
            robot.liftPower = -gamepad2.right_stick_y.toDouble()

            // G2: Left Stick Y => Horiz slides in/out
            robot.slidesPower = -gamepad2.left_stick_y.toDouble()

            // G1: DPad Up => Unlock lift
            if (gamepad1.dpad_up) robot.unlock()

            // G1: Left bumper => Reverse intake; Right bumper => Forward intake
            // G2: Left trigger => Reverse intake; Right trigger => Forward intake
            robot.intakePower = (gamepad2.left_trigger - gamepad2.right_trigger).toDouble() +
                    (if (gamepad1.right_bumper) 1.0 else if (gamepad1.left_bumper) -1.0 else 0.0)

            // G2: Left bumper => Depositor Up; Right bumper = Depositor Down
            if (gamepad2.left_bumper)
                robot.deposit = false
            else if (gamepad2.right_bumper)
                robot.deposit = true

            // G2: B => Paddle
            //robot.intakePaddle = true
            robot.intakePaddle = !gamepad2.b

            // Deal with intakeDown toggle
            robot.intakePosition = when (intakePosition) {
                0 -> 1.0
                1 -> 0.5
                2 -> 0.0
                else -> 1.0
            }

            // Telemetry and debug

            // G1: DPad Down => Reset Encoders (For testing)
            if (gamepad1.dpad_down) robot.resetEncoders()

            // Telemetry
            telemetry.addData("Right Pos", robot.rightPosition)
            telemetry.addData("Left Pos", robot.leftPosition)
            telemetry.addLine("-----")
            telemetry.addData("Right Front Pos", robot.frontRightPosition)
            telemetry.addData("Right Back Pos", robot.backRightPosition)
            telemetry.addData("Left Front Pos", robot.frontLeftPosition)
            telemetry.addData("Left Back Pos", robot.backLeftPosition)
            telemetry.update()
        }

    }
}