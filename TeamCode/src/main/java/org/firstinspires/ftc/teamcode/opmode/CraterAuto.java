package org.firstinspires.ftc.teamcode.opmode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.RobotHardware;
import org.firstinspires.ftc.teamcode.vision.Vision;

@Autonomous(name = "Crater", group = "Penn")
public class CraterAuto extends LinearOpMode {
    RobotHardware robot;

    boolean placeMarker = true;
    boolean align = true;
    boolean otherCrater = false;
    boolean land = true;
    boolean avoidMinerals = false;
    boolean aligned = false;
    boolean wait7 = false;

    public void runOpMode() throws InterruptedException {
        robot = new RobotHardware(this);
        Vision vision = new Vision(hardwareMap);
        Vision.CubePosition cubePos = Vision.CubePosition.UNKNOWN;
        vision.init();

        while (!isStopRequested() && !opModeIsActive()) {
            if (gamepad1.a)
                align = !align;
            if (gamepad1.b)
                placeMarker = !placeMarker;
            if (gamepad1.x)
                otherCrater = !otherCrater;
            //if(gamepad1.)
            //	avoidMinerals = !avoidMinerals;
            if (gamepad1.y)
                land = !land;
            if (gamepad1.right_bumper)
                wait7 = !wait7;

            telemetry.addData("Align (a)", align);
            telemetry.addData("Go to depot and place marker (b)", placeMarker);
            telemetry.addData("Go to opposite color crater (x) (sorta sketch)", otherCrater);
            telemetry.addData("Land (y)", land);
            telemetry.addData("Wait 7 (right bumper)", wait7);
            //telemetry.addData("Avoid Minerals (y)", avoidMinerals);
            telemetry.addLine();
            telemetry.addData("Cube Pos", (cubePos = vision.getCubePosition()));
            telemetry.update();
        }

        robot.reset();
        waitForStart();
        vision.cleanup();
        if (isStopRequested()) return;

        if (land)
            robot.lowerLift();

        if (align && !robot.align()) {
            telemetry.addLine("Failed to align");
            telemetry.update();
            return;
        }

        if (wait7)
            Thread.sleep(7000);

        robot.resetEncoders();
        if (land)
            robot.lowerLift();

        switch (cubePos) {
            case LEFT:
                robot.turn(-30);
                robot.resetEncoders();
                break;

            case RIGHT:
                robot.turn(30);
                robot.resetEncoders();
                break;
            case CENTER:
            case UNKNOWN:
                break;
        }

        if (!placeMarker) {
            robot.drive(45, 0.5);
            return;
        } else {

            robot.drive(20);
            robot.resetEncoders();

            robot.turn(88);
            robot.resetEncoders();

            robot.drive(-30);
            robot.drive(-44, 0.5);
            robot.resetEncoders();

            robot.turn(-43);
            robot.resetEncoders();

            robot.drive(-51);
            robot.resetEncoders();

            robot.deposit();

            if (otherCrater) {

                robot.turn(92);
                robot.resetEncoders();

                robot.align();
                robot.resetEncoders();

                robot.drive(70);
                robot.drive(95, 0.5);
                robot.resetEncoders();
            } else {
                robot.drive(70);
                robot.drive(95, 0.5);
                robot.resetEncoders();
            }
        }
    }

    public void runCenter() {

        if (!placeMarker) {
            robot.drive(45, 0.5);
            return;
        }

    }


}
