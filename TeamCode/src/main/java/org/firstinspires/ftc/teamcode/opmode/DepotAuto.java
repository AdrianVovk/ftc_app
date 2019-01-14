package org.firstinspires.ftc.teamcode.opmode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.RobotHardware;
import org.firstinspires.ftc.teamcode.vision.Vision;

@Autonomous(name = "Depot", group = "Cleveland")
public class DepotAuto extends LinearOpMode {

    public void runOpMode() {
        RobotHardware roobot = new RobotHardware(this);
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

        roobot.reset();
        waitForStart();
        vision.cleanup();
        if (isStopRequested()) return;

        if (land) roobot.land();

        if (align && !roobot.align()) {
            telemetry.addLine("Failed to align");
            telemetry.update();
            return;
        }
        roobot.drive(2);

        if (wait7) roobot.setSleep(7000);
        roobot.resetEncoders();

        if (land) roobot.lowerLift();

        roobot.setSlidesPower(1);
        roobot.setSleep(4000);
        roobot.setSlidesPower(0);

        roobot.setIntakeDown(true);
        roobot.setIntakePower(-1);
        roobot.setSleep(2000);
        roobot.setIntakePower(0);

        roobot.setIntakeDown(false);

        roobot.setSlidesPower(-1);
        roobot.setSleep(3500);
        roobot.setSlidesPower(0);

        switch (cubePos) {
            case LEFT:
                roobot.turn(-30);
                roobot.resetEncoders();
                /*roobot.drive(40, 0.5);
                roobot.resetEncoders();
                roobot.turn(50);
                roobot.resetEncoders();
                roobot.drive(31, 0.5);
                roobot.resetEncoders();
                roobot.turn(-20);*/
                break;
            case RIGHT:
                roobot.turn(30);
                roobot.resetEncoders();
                /*roobot.drive(40, 0.5);
                roobot.resetEncoders();
                roobot.turn(-55);
                roobot.resetEncoders();
                roobot.drive(35, 0.5);
                roobot.resetEncoders();
                roobot.turn(-12);
                roobot.resetEncoders();
                roobot.drive(5);
                roobot.resetEncoders();
                roobot.turn(30);*/
                break;
            case CENTER:
            case UNKNOWN:
                /*roobot.drive(40, 0.5);
                roobot.drive(40 + 30, 0.3);*/
                break;
        }
        roobot.resetEncoders();

        roobot.setIntakeDown(true);

        roobot.setSlidesPower(1);
        roobot.setSleep(3000);
        roobot.setSlidesPower(0);

        roobot.setIntakePower(1);
        roobot.setSleep(1500);
        roobot.setIntakePower(0);

        roobot.setSlidesPower(-1);
        roobot.setSleep(2500);
        roobot.setSlidesPower(0);

        switch (cubePos) {
            case LEFT:
                roobot.turn(30);
                roobot.resetEncoders();
                break;
            case RIGHT:
                roobot.turn(-30);
                roobot.resetEncoders();
                break;
            case CENTER:
            case UNKNOWN:

                break;
        }

        if (otherCrater) {

            roobot.drive(10,.7);
            roobot.turn(90);
            roobot.drive(35, .7);
            /*roobot.turn(120);
            roobot.resetEncoders();

            roobot.drive(18);
            roobot.resetEncoders();

            roobot.turn(12);
            roobot.resetEncoders();

            roobot.deposit();

            roobot.drive(78, 0.5);
            roobot.resetEncoders();*/

        } else {

            roobot.drive(10,.7);
            roobot.turn(-90);
            roobot.drive(35, .7);
            /*roobot.turn(-130);
            roobot.resetEncoders();

            roobot.deposit();

            roobot.drive(70);
            roobot.resetEncoders();

            roobot.drive(25, 0.5);
            roobot.resetEncoders();*/
        }
    }

}
