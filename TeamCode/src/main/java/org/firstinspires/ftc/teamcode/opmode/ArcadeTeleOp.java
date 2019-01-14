package org.firstinspires.ftc.teamcode.opmode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Constants;
import org.firstinspires.ftc.teamcode.RobotHardware;

@TeleOp(name = "Arcade TeleOp", group = "Cleveland")
public class ArcadeTeleOp extends LinearOpMode {

    public void runOpMode() {
        RobotHardware robot = new RobotHardware(this);

        boolean rbPressedLast = false;

        int direction = 1;
        double powerScale = 1.0;
        robot.reset();
        waitForStart();

        while (opModeIsActive()) {
            // Invert the drive direction if necessary
            if (gamepad1.right_bumper && !rbPressedLast) direction *= -1;
            rbPressedLast = gamepad1.right_bumper;

            double drive = -gamepad1.right_stick_y * direction;
            double turn = gamepad1.right_stick_x;

            robot.setLeftPower((drive + turn) * powerScale);
            robot.setRightPower((drive - turn) * powerScale);
            robot.setLiftPower(-gamepad1.left_stick_y);

            //robot.setSlidesPower(gamepad1.left_stick_x);
            //robot.setIntakePower(gamepad1.left_trigger * (gamepad1.left_bumper ? -1 : 1));
            //robot.setIntakeDown(gamepad1.a);

            robot.setDeposit(gamepad1.a);
            if (gamepad1.x) robot.unlock();

            powerScale = gamepad1.left_bumper ? 0.5 : 1.0;

            if (gamepad1.b)
                robot.resetEncoders();

            telemetry.addData("Right pos", robot.getRightPosition());
            telemetry.addData("Left pos", robot.getLeftPosition());
            telemetry.update();
        }
    }

}