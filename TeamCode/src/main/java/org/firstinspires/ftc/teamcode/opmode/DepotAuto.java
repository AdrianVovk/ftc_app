package org.firstinspires.ftc.teamcode.opmode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.BeltDriveHardware;

@Autonomous(name="Depot", group="Penn")
public class DepotAuto extends LinearOpMode{

	public void runOpMode() throws InterruptedException {
		
		BeltDriveHardware roobot = new BeltDriveHardware(this);
		boolean align = true;
		boolean otherCrater = false;
		boolean land = true;
		boolean avoidMinerals = false;
		boolean wait7 = false;

		while(!isStopRequested() && !isStarted()) {
			if(gamepad1.a)
				align = !align;
			if(gamepad1.x)
				otherCrater = !otherCrater;
			/*if(gamepad1.b)
				avoidMinerals = !avoidMinerals;*/
			if(gamepad1.y)
				land = !land;
			if(gamepad1.right_bumper)
				wait7 = !wait7;
			
			telemetry.addData("Align (a)", align);
			telemetry.addData("Same color crater (x)", otherCrater);
			//telemetry.addData("Avoid Minerals + Avoid Depot (b)", avoidMinerals);
			telemetry.addData("Land (y)", land);
			telemetry.addData("Wait 7 (right bumper)", wait7);
			telemetry.update();
		}
		
		roobot.reset(); 
		waitForStart();
		if (isStopRequested()) return;
	
		if(land)
			roobot.land();
		
		if (align && !roobot.alignToLander()) {
			telemetry.addLine("Failed to align");
			telemetry.update();
			return;
		}
		
		if(wait7)
			Thread.sleep(7000);
			
		roobot.resetEncoders();
			
		if (land)
			roobot.lowerLift();
			
		if(avoidMinerals) {
			roobot.driveForward(14);
			roobot.resetEncoders();
			
			if(!otherCrater) {
				roobot.turn(-89);
				roobot.resetEncoders();
			}
		} else {
			roobot.driveForwardAtSpeed(40, .5);
			roobot.resetEncoders();
			
			roobot.driveForwardAtSpeed(23, 0.3);
			roobot.resetEncoders();
			/*long currentTime = System.currentTimeMillis();
	
			while(System.currentTimeMillis()-currentTime<500)
				roobot.setIntakePower(-1);*/
   
			//roobot.setIntakePower(0);
			
			//roobot.turn(185);
			
			
			if(otherCrater) {
				roobot.turn(120);
				roobot.resetEncoders();
				
				roobot.driveForward(18);
				roobot.resetEncoders();
				
				roobot.turn(12);
				
				roobot.resetEncoders();
				
				roobot.deposit();
				
				//roobot.driveForward(-10);
				//roobot.resetEncoders();
				
				roobot.driveForwardAtSpeed(78, .5);
				roobot.resetEncoders();
			
			} else {
				
				roobot.turn(-130);
				roobot.resetEncoders();
				
				roobot.deposit();
				
				roobot.driveForward(70);
				roobot.resetEncoders();
				
				roobot.driveForwardAtSpeed(25, 0.5);
				roobot.resetEncoders();
			}
		}
	}
}
