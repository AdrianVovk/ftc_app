package org.firstinspires.ftc.teamcode.opmode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.BeltDriveHardware;
import org.firstinspires.ftc.teamcode.RobotHardware;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

@TeleOp(name = "Belt Tele Op (POV mode)", group="Penn")
public class BeltTeleOpPOV extends LinearOpMode{

	public void runOpMode() throws InterruptedException {

        RobotHardware robot = new RobotHardware(this);

		int direction = 1;
		robot.reset();

		waitForStart();

		while(opModeIsActive()){
			double drive = -gamepad1.right_stick_y, turn = gamepad1.right_stick_x;

			robot.setLeftPower((drive * direction) + turn);
			robot.setRightPower((drive * direction) - turn);
			robot.setLiftPower(-gamepad1.left_stick_y);
			
			/*
			if(gamepad1.b)
				roobot.intakeRotLeft.setPosition(0);
			else
				roobot.intakeRotLeft.setPosition(1);
            */

			if (gamepad1.a)
				robot.resetEncoders();
				
			if (gamepad1.right_bumper)
				direction = -1;
			else
				direction = 1;

			telemetry.addData("Right pos", robot.getRightPosition());
			telemetry.addData("Left pos", robot.getLeftPosition());
			telemetry.update();
		}
		
	}
	
}