package org.firstinspires.ftc.teamcode.opmode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.BeltDriveHardware;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

@TeleOp(name = "Belt Tele Op (POV mode)", group="Penn")
public class BeltTeleOpPOV extends LinearOpMode{

	public void runOpMode() throws InterruptedException{
		
		BeltDriveHardware roobot = new BeltDriveHardware(this);
	   
		int direction = 1;
		roobot.reset(); 
		waitForStart();
		
		while(opModeIsActive()){
			double drive = -gamepad1.right_stick_y, turn = gamepad1.right_stick_x;
			double leftPower = drive + turn;
			double rightPower = drive - turn;
			roobot.setPower(leftPower * direction, rightPower * direction);
			roobot.setVertSlidesPower(-gamepad1.left_stick_y);
			
			if(gamepad1.b)
				roobot.intakeRotLeft.setPosition(0);
			else
				roobot.intakeRotLeft.setPosition(1);
				
			if (gamepad1.a)
				roobot.resetEncoders();
				
			if (gamepad1.right_bumper)
				direction = -1;
			else
				direction = 1;
				
			telemetry.addData("FR Position", roobot.driveRF.getCurrentPosition());
			telemetry.addData("FL Position", roobot.driveLF.getCurrentPosition());
			telemetry.addData("BR Position", roobot.driveRB.getCurrentPosition());
			telemetry.addData("BL Position", roobot.driveLB.getCurrentPosition());
			telemetry.update();
		}
		
	}
	
}