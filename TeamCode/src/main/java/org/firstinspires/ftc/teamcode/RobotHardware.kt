package org.firstinspires.ftc.teamcode

import com.acmerobotics.dashboard.config.Config
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.ext.init
import org.firstinspires.ftc.teamcode.ext.onTape
import org.firstinspires.ftc.teamcode.ext.resetEncoder
import org.firstinspires.ftc.teamcode.ext.runToPosition
import kotlin.math.roundToInt

@Config
object Constants {
    var TICKS_IN_ROT = 537.6
    var INCHES_IN_ROT = 17.0
    var DEGRESS_IN_ROT = 60.0

    val INCHES_TO_TICKS
        get() = TICKS_IN_ROT / INCHES_IN_ROT
    val DEGRESS_TO_TICKS
        get() = TICKS_IN_ROT / DEGRESS_IN_ROT

    var DRIVE_POWER_SCALE = 1.0
    var LIFT_POWER_SCALE = 1.0
}

class RobotHardware(map: HardwareMap) {

    ///////////////////////////
    // Hardware map and init //
    ///////////////////////////

    // Hardware map
    private val dLF = map.dcMotor["dLF"].init(true)
    private val dLB = map.dcMotor["dLB"].init(false)
    private val dRF = map.dcMotor["dRF"].init(false)
    private val dRB = map.dcMotor["dRB"].init(true)
    private val leftLift = map.dcMotor["leftLift"].init()
    private val rightLift = map.dcMotor["rightLift"].init()
    private val liftLock = map.servo["ratchet"]
    private val leftColor = map.colorSensor["leftColor"]
    private val rightColor = map.colorSensor["rightColor"]

    // Access to the OpMode
    private var opMode: LinearOpMode? = null

    // This is the constructor that should probably be used
    constructor(opMode: LinearOpMode) : this(opMode.hardwareMap) {
        this.opMode = opMode
    }

    // Used to stop loops if necessary
    private val running: Boolean
        get() = opMode?.isStopRequested?.not() ?: true

    var sleep: Long = 0
        get() = field
        set(value) {
            field = value
            if (running) Thread.sleep(value)
        }

    /////////////////////
    // Simple controls //
    /////////////////////

    var leftPower: Double
        get() = (dLF.power + dLB.power) / 2
        set(value) {
            dLF.power = value * Constants.DRIVE_POWER_SCALE
            dLB.power = value * Constants.DRIVE_POWER_SCALE
        }

    var rightPower: Double
        get() = (dRF.power + dRB.power) / 2
        set(value) {
            dRF.power = value * Constants.DRIVE_POWER_SCALE
            dRB.power = value * Constants.DRIVE_POWER_SCALE
        }

    var liftPower: Double
        get() = (leftLift.power + rightLift.power) / 2
        set(value) {
            leftLift.power = value * Constants.LIFT_POWER_SCALE
            rightLift.power = value * Constants.LIFT_POWER_SCALE
        }

    val currentPosition: Double
        get() = (leftPosition + rightPosition) / 2.0

    val leftPosition: Double
        get() = ((dLF.currentPosition + dLB.currentPosition) / 2.0) / Constants.INCHES_TO_TICKS

    val rightPosition: Double
        get() = ((dRF.currentPosition + dRB.currentPosition) / 2.0) / Constants.INCHES_TO_TICKS

    fun stop() {
        leftPower = 0.0
        rightPower = 0.0
        sleep = 100
    }

    fun reset() {
        stop()
        resetEncoders()
    }

    fun resetEncoders() {
        dLF.resetEncoder()
        dLB.resetEncoder()
        dRF.resetEncoder()
        dRB.resetEncoder()
    }

    ////////////////////
    // Smart Controls //
    ////////////////////

    private fun moveTicks(l: Int, lPow: Double, r: Int, rPow: Double) {
        stop() // Stop moving (for accuracy reasons)
        dLF.runToPosition(l, lPow)
        dLB.runToPosition(l, lPow)
        dRF.runToPosition(r, rPow)
        dRB.runToPosition(r, rPow)
        while (running && (dLF.isBusy && dLB.isBusy) || (dRF.isBusy || dRB.isBusy));
        stop() // Stop the motors
    }

    fun drive(leftIn: Double, leftSpeed: Double = 1.0, rightIn: Double = leftIn, rightSpeed: Double = leftSpeed) {
        val lTicks = (leftIn * Constants.INCHES_TO_TICKS).roundToInt()
        val rTicks = (rightIn * Constants.INCHES_TO_TICKS).roundToInt()
        moveTicks(lTicks, leftSpeed, rTicks, rightSpeed)
    }

    fun turn(degrees: Int, speed: Double = 1.0) {
        val ticks = (degrees * Constants.DEGRESS_TO_TICKS).roundToInt()
        moveTicks(ticks, speed, -ticks, 1.0)
    }

    fun land() {
        liftLock.position = 0.0
        liftPower = 1.0
        sleep = 800
        liftPower = 0.0
    }

    fun lowerLift() {
        liftPower = -1.0
        sleep = 700
        liftPower = 0.0
    }

    fun align(): Boolean {
        resetEncoders()
        var count = 0
        while (running && !(leftColor.onTape() && rightColor.onTape())) {
            leftPower = 0.2
            rightPower = 0.2
            while (running && currentPosition <= 15 && !(leftColor.onTape() || rightColor.onTape()));
            stop()
            leftPower = -0.2
            rightPower = -0.2
            while (running && currentPosition <= -15 && !(leftColor.onTape() || rightColor.onTape()));
            stop()

            // Only go back and forward twice
            if (!leftColor.onTape() && !rightColor.onTape())
                if (count < 2)
                    continue
                else return false

            if (!leftColor.onTape()) {
                while (running && leftPosition >= -6 && !leftColor.onTape()) {
                    leftPower = -0.2
                    rightPower = 0.1
                }
                while (running && leftPosition <= 6 && !leftColor.onTape()) {
                    leftPower = 0.2
                    rightPower = -0.1
                }
            } else if (!rightColor.onTape()) {
                while (running && rightPosition >= -6 && !rightColor.onTape()) {
                    leftPower = -0.2
                    rightPower = 0.1
                }
                while (running && rightPosition <= 6 && !rightColor.onTape()) {
                    leftPower = 0.2
                    rightPower = -0.1
                }
            } else return rightColor.onTape() && leftColor.onTape()
        }
        return true
    }
}