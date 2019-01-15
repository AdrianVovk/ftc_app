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

        //drive for marker
        robot.drive(30,.5,20,.5);
        robot.resetEncoders();

        //slides and intake for Marker placement
        robot.setSlidesPower(1);
        robot.setSleep(3000);
        robot.setSlidesPower(0);

        robot.setIntakePaddle(1);

        robot.setIntakePower(-1);
        robot.setSleep(3000);
        robot.setIntakePower(0);


        robot.setSlidesPower(-1);
        robot.setSleep(3500);
        robot.setSlidesPower(0);

        //drive and realignment
        robot.drive(-30,.5, -20, .5);
        robot.resetEncoders();

        robot.align();
        robot.resetEncoders();

        //turn for sample
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

        //sample
        robot.setSlidesPower(1);
        robot.setSleep(3000);
        robot.setSlidesPower(0);

        robot.setIntakePower(1);
        robot.setSleep(3000);
        robot.setIntakePower(0);

        robot.setSlidesPower(-1);
        robot.setSleep(3500);
        robot.setSlidesPower(0);

        //deposit to Intake
        robot.setIntakePaddle(1);

        robot.setIntakeDown(true);

        robot.setLiftPower(1);
        robot.setSleep(3000);

        robot.setDeposit();
        robot.setSleep(2500);

        robot.setDeposit();
        robot.setSleep(2500);

        robot.drive(0);
        robot.resetEncoders();

        robot.setLiftPower(-1);
        robot.setSleep(3000);


        //deposit extra mineral
        robot.turn(90);
        robot.resetEncoders();

        robot.drive(-10);
        robot.resetEncoders();














        switch (cubePos) {
            case LEFT:
                robot.turn(30);
                robot.resetEncoders();
                break;

            case RIGHT:
                robot.turn(-30);
                robot.resetEncoders();
                break;

            case CENTER:
                break;

            case UNKNOWN:
                break;
        }


        robot.turn(-45);
        robot.resetEncoders();

        robot.drive(0);
        robot.resetEncoders();

        robot.turn(-10);
        robot.resetEncoders();

        //send slides out for marker claim
        robot.setSlidesPower(1);
        robot.setSleep(3000);

        robot.setSlidesPower(-1);
        robot.setSleep(3500);

        robot.setIntakePaddle(1);
        robot.wait(1000);

        robot.setIntakePaddle(-1);
        robot.wait(1000);

        if (otherCrater) {

            robot.turn(-92);
            robot.resetEncoders();

            robot.drive(0);
            robot.resetEncoders();

            robot.turn(-10);
            robot.resetEncoders();

            robot.drive();
            robot.resetEncoders();
        } else {

            robot.turn(55);
            robot.resetEncoders();

            //parking/grabbing extra minerals

            robot.drive(0);
            robot.resetEncoders();

        }

        robot.setSlidesPower(1);
        robot.setSleep(1000);

        robot.setIntakePower(1);
        robot.setSleep(3000);




        /*if (!placeMarker) {
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

             else {
                robot.drive(70);
                robot.drive(95, 0.5);
                robot.resetEncoders();
            }
        } */
    }

    /*public void runCenter() {

        if (!placeMarker) {
            robot.drive(45, 0.5);
            return;
        }*/

}


}
