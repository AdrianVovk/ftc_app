package org.firstinspires.ftc.teamcode.ext

import com.qualcomm.robotcore.hardware.Gamepad

/**
 * This class makes gamepads easier to deal with
 */
class GamepadHandler(val gamepad: Gamepad) {

    private var aPressedBefore = false
    private var bPressedBefore = false
    private var xPressedBefore = false
    private var yPressedBefore = false
    private var rbPressedBefore = false
    private var lbPressedBefore = false

    val a: Boolean
        get() {
            val gmpad = gamepad.a
            val ret = !aPressedBefore && gmpad
            aPressedBefore = gmpad
            return ret
        }

    val b: Boolean
        get() {
            val gmpad = gamepad.b
            val ret = !bPressedBefore && gmpad
            bPressedBefore = gmpad
            return ret
        }

    val x: Boolean
        get() {
            val gmpad = gamepad.x
            val ret = !xPressedBefore && gmpad
            xPressedBefore = gmpad
            return ret
        }

    val y: Boolean
        get() {
            val gmpad = gamepad.y
            val ret = !yPressedBefore && gmpad
            yPressedBefore = gmpad
            return ret
        }

    val rightBumper: Boolean
        get() {
            val gmpad = gamepad.right_bumper
            val ret = !rbPressedBefore && gmpad
            rbPressedBefore = gmpad
            return ret
        }

    val leftBumper: Boolean
        get() {
            val gmpad = gamepad.left_bumper
            val ret = !lbPressedBefore && gmpad
            lbPressedBefore = gmpad
            return ret
        }
}