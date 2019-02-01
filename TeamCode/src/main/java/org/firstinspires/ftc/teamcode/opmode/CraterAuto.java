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
    boolean wait7 = false;
    boolean depositMineral = true;

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
            if (gamepad1.left_bumper)
                depositMineral = !depositMineral;

            telemetry.addData("Align (a)", align);
            telemetry.addData("Go to depot and place marker (b)", placeMarker);
            telemetry.addData("Go to opposite color crater (x) (sorta sketch)", otherCrater);
            telemetry.addData("Land (y)", land);
            telemetry.addData("Wait 7 (right bumper)", wait7);
            telemetry.addData("Deposit Mineral (left bumper)", depositMineral);
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
        robot.drive(30, .5, 20, .5);
        robot.resetEncoders();

        //drive and outake for Marker placement
        robot.drive(15);
        robot.resetEncoders();

        robot.setIntakePower(-1);
        robot.setSleep(1500);
        robot.setIntakePower(0);

        //realign for vision
        robot.drive(-10);
        robot.resetEncoders();

        robot.drive(-30, .5, -20, .5);
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
        robot.drive(10);
        robot.resetEncoders();

        robot.setIntakePower(1);
        robot.setSleep(1000);
        robot.setIntakePower(0);

        //go to crater
        switch (cubePos) {
            case LEFT:
                robot.drive(10,1,5,1);
                robot.resetEncoders();
                break;

            case RIGHT:
                robot.drive(5,1,10,1);
                robot.resetEncoders();
                break;

            case CENTER:
                robot.drive(10);
                break;

            case UNKNOWN:
                break;
        }

        if (depositMineral) {
            //deposit to Intake
            //robot.setIntakePaddle(1);

            robot.setDeposit(true);

            //robot.setIntakeDown(true);

            //robot.setIntakePower(1);
            robot.setSleep(1500);
            //robot.setIntakePower(0);

            robot.setDeposit(false);

            //send lift up for deposit
            robot.setLiftPower(1);
            robot.setSleep(1000);
            robot.setLiftPower(0);

            //set up for deposit
            robot.turn(90);
            robot.resetEncoders();

            robot.drive(-10);
            robot.resetEncoders();

            robot.turn(90);
            robot.resetEncoders();

            robot.drive(-10, .5);
            robot.resetEncoders();

            //make deposit
            robot.setDeposit(true);

            robot.drive(0);
            robot.resetEncoders();

        }

            robot.setLiftPower(-1);
            robot.setSleep(1000);
            robot.setLiftPower(0);

            robot.turn(-45);
            robot.resetEncoders();

            robot.drive(0);
            robot.resetEncoders();

            robot.turn(-10);
            robot.resetEncoders();



        if (otherCrater) {

            robot.turn(-92);
            robot.resetEncoders();

            robot.drive(0);
            robot.resetEncoders();

            robot.turn(-10);
            robot.resetEncoders();

            robot.drive(0);
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

        //robot.setIntakePower(1);
        robot.setSleep(1000);




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



