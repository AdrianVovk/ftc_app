package org.firstinspires.ftc.teamcode

import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.dashboard.config.Config
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.ext.init
import org.firstinspires.ftc.teamcode.ext.onTape
import org.firstinspires.ftc.teamcode.ext.resetEncoder
import org.firstinspires.ftc.teamcode.ext.runToPosition
import kotlin.math.roundToInt

@Config("Drivetrain")
object Constants {
    @JvmField
    var TICKS_IN_ROT = 537.6
    @JvmField
    var INCHES_IN_ROT = 17.0
    @JvmField
    var DEGRESS_IN_ROT = 60.0

    @JvmField
    var TURN_TIME_LIMIT: Long = 1500
    @JvmField
    var DRIVE_TIME_LIMIT: Long = 2500

    val INCHES_TO_TICKS
        get() = TICKS_IN_ROT / INCHES_IN_ROT
    val DEGRESS_TO_TICKS
        get() = TICKS_IN_ROT / DEGRESS_IN_ROT

    @JvmField
    var DRIVE_POWER_SCALE = 1.0
    @JvmField
    var SLIDES_POWER_SCALE = 0.05
    @JvmField
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
    private val rightLift = map.dcMotor["rightLift"].init(true)
    private val liftLock = map.servo["ratchet"]
    private val intake = map.dcMotor["intake"].init()
    private val slides = map.dcMotor["slides"].init()
    private val intakeFlipLeft = map.servo["intakeRotLeft"]
    private val intakeFlipRight = map.servo["intakeRotRight"]
    private val paddle = map.servo["paddle"]
    private val depositor = map.servo["deposit"]
    private val leftColor = map.colorSensor["leftColor"]
    private val rightColor = map.colorSensor["rightColor"]

    // Access to the OpMode
    private var opMode: LinearOpMode? = null

    // This is the constructor that should probably be used
    constructor(opMode: LinearOpMode) : this(opMode.hardwareMap) {
        this.opMode = opMode
        opMode.telemetry = MultipleTelemetry(opMode.telemetry, FtcDashboard.getInstance()?.telemetry)
    }

    // Used to stop loops if necessary
    private val running: Boolean
        get() = opMode?.isStopRequested?.not() ?: true

    var sleep: Long = 0
        set(value) {
            field = value
            val startTime = System.currentTimeMillis()
            while (running && System.currentTimeMillis() - startTime <= value);
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


    var intakePower: Double
        get() = intake.power
        set(value) {
            intake.power = value
        }

    var slidesPower: Double
        get() = slides.power
        set(value) {
            slides.power = value * Constants.SLIDES_POWER_SCALE
        }

    private var oldIntakePos : Double = -1.0
    var intakePosition: Double
        get() = (intakeFlipLeft.position + intakeFlipRight.position)/2
        set(value) {
            if (value == 0.0) {
                intakePaddle = true
                if (oldIntakePos != value) sleep = 300
            } else intakePaddle = false
            oldIntakePos = value

            intakeFlipLeft.position = value
            intakeFlipRight.position = 1.0 - value
        }

    var intakePaddle: Boolean
        get() = paddle.position == 0.0
        set(value) {
            paddle.position = if (value) 1.0 else 0.0
        }


    var deposit: Boolean
        get() = depositor.position == 0.5
        set(value) {
            depositor.position = if (value) 1.0 else 0.5

            //if true make deposit if false keep up
        }

    fun deposit() {
        deposit = true
        sleep = 1500
        deposit = false
    }

    val currentPosition: Double
        get() = (leftPosition + rightPosition) / 2.0

    val leftPosition: Double
        get() = (frontLeftPosition + backLeftPosition) / 2.0

    val rightPosition: Double
        get() = (frontRightPosition + backRightPosition) / 2.0

    val frontLeftPosition: Double
        get() = dLF.currentPosition / Constants.INCHES_TO_TICKS

    val frontRightPosition: Double
        get() = dRF.currentPosition / Constants.INCHES_TO_TICKS

    val backLeftPosition: Double
        get() = dLB.currentPosition / Constants.INCHES_TO_TICKS

    val backRightPosition: Double
        get() = dRB.currentPosition / Constants.INCHES_TO_TICKS

    fun stop() {
        leftPower = 0.0
        rightPower = 0.0
        sleep = 100
    }

    fun reset() {
        stop()
        resetEncoders()
        liftPower = 0.0
        slidesPower = 0.0
        intakePower = 0.0
        intakePosition = 0.0
        deposit = false

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

    private fun moveTicks(l: Int, lPow: Double, r: Int, rPow: Double, timeLim: Long = Long.MAX_VALUE) {
        val startTime = System.currentTimeMillis()

        stop() // Stop moving (for accuracy reasons)
        dLF.runToPosition(l, lPow)
        dLB.runToPosition(l, lPow)
        dRF.runToPosition(r, rPow)
        dRB.runToPosition(r, rPow)
        while (running && System.currentTimeMillis() - startTime <= timeLim &&
                ((dLF.isBusy && dLB.isBusy) || (dRF.isBusy || dRB.isBusy)));
        stop() // Stop the motors
    }

    @JvmOverloads
    fun drive(leftIn: Double, leftSpeed: Double = 1.0, rightIn: Double = leftIn, rightSpeed: Double = leftSpeed) {
        val lTicks = (leftIn * Constants.INCHES_TO_TICKS).roundToInt()
        val rTicks = (rightIn * Constants.INCHES_TO_TICKS).roundToInt()
        moveTicks(lTicks, leftSpeed, rTicks, rightSpeed, Constants.DRIVE_TIME_LIMIT)
    }

    @JvmOverloads
    fun turn(degrees: Int, speed: Double = 1.0) {
        val ticks = (degrees * Constants.DEGRESS_TO_TICKS).roundToInt()
        moveTicks(ticks, speed, -ticks, speed, Constants.TURN_TIME_LIMIT)
    }

    fun unlock() {
        liftLock.position = 0.0
    }

    fun land() {
        unlock()
        sleep = 100
        liftPower = 0.6
        sleep = 1300
        liftPower = 0.0
        sleep = 300
    }

    fun lowerLift() {
        liftPower = -.5
        sleep = 1400
        liftPower = 0.0
    }

    fun align(): Boolean {
        resetEncoders()
        var count = 0
        while (running && !(leftColor.onTape() && rightColor.onTape())) {
            leftPower = 0.3
            rightPower = 0.3
            while (running && currentPosition <= 15 && !(leftColor.onTape() || rightColor.onTape()));
            stop()
            leftPower = -0.2
            rightPower = -0.2
            while (running && currentPosition >= -15 && !(leftColor.onTape() || rightColor.onTape()));
            stop()

            // Only go back and forward twice
            count++
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