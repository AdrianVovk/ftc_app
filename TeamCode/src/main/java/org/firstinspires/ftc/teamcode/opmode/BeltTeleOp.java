package org.firstinspires.ftc.teamcode.opmode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.BeltDriveHardware;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

@TeleOp(name = "Belt Tele Op")
@Disabled
public class BeltTeleOp extends LinearOpMode {

	public void runOpMode() throws InterruptedException {
		
		BeltDriveHardware roobot = new BeltDriveHardware(this);
	   
		roobot.reset(); 
		waitForStart();
		
		while(opModeIsActive()){
			
			roobot.setPower(-gamepad1.left_stick_y, -gamepad1.right_stick_y);

			roobot.setVertSlidesPower(-gamepad2.left_stick_y);
			//roobot.setHorizSlidesPower(gamepad2.right_stick_y);
			
			//roobot.intakeRotLeft.setPosition(gamepad1.left_stick_y);
			//roobot.intake.setPower(-gamepad1.right_trigger * 0.8f * (gamepad1.right_bumper ? -1f : 1f));
		
			
			if(gamepad1.a){
				
				roobot.resetEncoders();
			} else if (gamepad1.b) {
				roobot.deposit();
			}
			
			//roobot.intakeRotLeft.setPosition();
			//roobot.intakeRotLeft.setPosition(0);
				telemetry.addData("Servo Pos", roobot.intakeRotLeft.getPosition());
				telemetry.addData("Other servo pos", roobot.ratchet.getPosition());
				//telemetry.update();
	
			
			/*
			roobot.lowerIntake(gamepad2.left_bumper);
			roobot.setPaddle(gamepad2.right_bumper);
			if(gamepad2.left_trigger==0)
				roobot.setIntakePower(-gamepad2.right_trigger);
			else
				roobot.setIntakePower(gamepad2.left_trigger);*/
			
			telemetry.addData("FR Position", roobot.driveRF.getCurrentPosition());
			telemetry.addData("FL Position", roobot.driveLF.getCurrentPosition());
			telemetry.addData("BR Position", roobot.driveRB.getCurrentPosition());
			telemetry.addData("BL Position", roobot.driveLB.getCurrentPosition());
			telemetry.update();
		}
		stop();
	}
	
}