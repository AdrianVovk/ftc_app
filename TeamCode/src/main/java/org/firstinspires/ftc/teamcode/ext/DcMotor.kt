package org.firstinspires.ftc.teamcode.ext

import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorSimple
import org.firstinspires.ftc.teamcode.Constants

fun DcMotor.init(reverse: Boolean = false) = apply {
    direction = if (reverse) DcMotorSimple.Direction.REVERSE else DcMotorSimple.Direction.FORWARD
    zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
    resetEncoder()
    power = 0.0
}

fun DcMotor.resetEncoder() {
    power = 0.0
    mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
    mode = DcMotor.RunMode.RUN_USING_ENCODER
}

fun DcMotor.useEncoder() {
    mode = DcMotor.RunMode.RUN_USING_ENCODER
}

fun DcMotor.runToPosition(target: Int, maxPower: Double) {
    mode = DcMotor.RunMode.RUN_TO_POSITION
    targetPosition = target
    power = maxPower * Constants.DRIVE_POWER_SCALE
}