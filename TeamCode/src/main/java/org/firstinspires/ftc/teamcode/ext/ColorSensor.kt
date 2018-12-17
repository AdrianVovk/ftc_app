package org.firstinspires.ftc.teamcode.ext

import com.qualcomm.robotcore.hardware.ColorSensor

fun ColorSensor.onTape(): Boolean {
    val onBlue = blue() - red() >= 10
    val onRed = red() - green() >= 10
    return onBlue || onRed
}