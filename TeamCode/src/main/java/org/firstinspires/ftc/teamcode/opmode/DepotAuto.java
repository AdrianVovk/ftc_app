package org.firstinspires.ftc.teamcode.opmode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.RobotHardware;
import org.firstinspires.ftc.teamcode.vision.Vision;

@Autonomous(name = "Depot", group = "Cleveland")
public class DepotAuto extends LinearOpMode {

    public void runOpMode() {
        RobotHardware robot = new RobotHardware(this);
        Vision vision = new Vision(hardwareMap);

        Vision.CubePosition cubePos = Vision.CubePosition.UNKNOWN;
        vision.init();

        boolean align = true;
        boolean otherCrater = false;
        boolean land = true;
        boolean avoidMinerals = false;
        boolean wait7 = false;
        while (!isStopRequested() && !isStarted()) {
            if (gamepad1.a)
                align = !align;
            if (gamepad1.x)
                otherCrater = !otherCrater;
			if(gamepad1.b)
				avoidMinerals = !avoidMinerals;
            if (gamepad1.y)
                land = !land;
            if (gamepad1.right_bumper)
                wait7 = !wait7;
            telemetry.addData("Align (a)", align);
            telemetry.addData("Same color crater (x)", otherCrater);
            telemetry.addData("Avoid Minerals + Avoid Depot (b)", avoidMinerals);
            telemetry.addData("Land (y)", land);
            telemetry.addData("Wait 7 (right bumper)", wait7);
            telemetry.addLine();
            telemetry.addData("Cube Pos", (cubePos = vision.getCubePosition()));
            telemetry.update();

            vision.updateDashboard();
        }

        robot.reset();
        waitForStart();
        vision.cleanup();
        if (isStopRequested()) return;

        if (land) robot.land();

        if (align && !robot.align()) {
            telemetry.addLine("Failed to align");
            telemetry.update();
            return;
        }
        robot.drive(2);

        if (wait7) robot.setSleep(7000);
        robot.resetEncoders();

        if (land) robot.lowerLift();

        switch (cubePos) {
            case LEFT:
                robot.turn(-30);
                robot.resetEncoders();
                robot.drive(40, 0.5);
                robot.resetEncoders();
                robot.turn(50);
                robot.resetEncoders();
                robot.drive(31, 0.5);
                robot.resetEncoders();
                robot.turn(-20);
                break;
            /*case RIGHT:
                robot.turn(30);
                robot.resetEncoders();
                robot.drive(40, 0.5);
                robot.resetEncoders();
                robot.turn(-55);
                robot.resetEncoders();
                robot.drive(35, 0.5);
                robot.resetEncoders();
                robot.turn(-12);
                robot.resetEncoders();
                robot.drive(5);
                robot.resetEncoders();
                robot.turn(30);
                break;*/
            case RIGHT:
            case CENTER:
            case UNKNOWN:
                robot.drive(40, 0.5);
                robot.drive(40 + 30, 0.3);
                break;
        }
        robot.resetEncoders();

        if (otherCrater) {
            robot.turn(120);
            robot.resetEncoders();

            robot.drive(18);
            robot.resetEncoders();

            robot.turn(12);
            robot.resetEncoders();

            robot.deposit();

            robot.drive(78, 0.5);
            robot.resetEncoders();
        } else {
            robot.turn(-130);
            robot.resetEncoders();

            robot.deposit();

            robot.drive(70);
            robot.resetEncoders();

            robot.drive(25, 0.5);
            robot.resetEncoders();
        }
    }

}
