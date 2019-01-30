package org.firstinspires.ftc.teamcode.opmode

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.RobotHardware

@TeleOp()
class ArcadeTeleOp : LinearOpMode() {
    val robot: RobotHardware by lazy { RobotHardware(this) }

    override fun runOpMode() {
        var rbPressedLast = false
        var intakeDownBtnLast = false
        var intakeDown = false;
        var direction = 1
        var powerScale = 1.0
        robot.reset()
        waitForStart()

        while (opModeIsActive()) {
            // Invert the drive direction if necessary
            if (gamepad1.right_bumper && !rbPressedLast) direction *= -1
            rbPressedLast = gamepad1.right_bumper

            val drive = (-gamepad1.right_stick_y * direction).toDouble()
            val turn = gamepad1.right_stick_x.toDouble()

            robot.leftPower = (drive + turn) * powerScale
            robot.rightPower = (drive - turn) * powerScale
            robot.liftPower = -gamepad1.left_stick_y.toDouble()

            robot.slidesPower = -gamepad1.left_stick_x.toDouble()

            robot.intakePower = gamepad2.left_trigger * (if (gamepad2.left_bumper) 1 else -1).toDouble()

            robot.intakePaddle = gamepad2.y

            if (gamepad2.b && ! intakeDownBtnLast)
                intakeDown = !intakeDown
            robot.intakeDown = intakeDown
            intakeDownBtnLast = gamepad2.b

            robot.deposit = gamepad2.a
            if (gamepad2.x) robot.unlock()

            powerScale = if (gamepad1.left_bumper) 0.5 else 1.0

            if (gamepad1.b)
                robot.resetEncoders()

            //telemetry.addData("Right pos", robot.rightPosition)
            //telemetry.addData("Left pos", robot.leftPosition)
            telemetry.addData("Right Front Pos", robot.frontRightPosition)
            telemetry.addData("Right Back Pos", robot.backRightPosition)
            telemetry.addData("Left Front Pos", robot.frontLeftPosition)
            telemetry.addData("Left Back Pos", robot.backLeftPosition)

            telemetry.update()
        }

    }
}