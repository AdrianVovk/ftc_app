package org.firstinspires.ftc.teamcode.opmode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.BeltDriveHardware;
import org.firstinspires.ftc.teamcode.vision.Vision;

@Autonomous(name="Crater", group="Penn")
public class CraterAuto extends LinearOpMode{

	public void runOpMode() throws InterruptedException{
		
		BeltDriveHardware roobot = new BeltDriveHardware(this);
		boolean placeMarker = true;
		boolean align = true;
		boolean otherCrater = false;
		boolean land = true;
		boolean avoidMinerals = false;
		boolean aligned = false;
		boolean wait7 = false;
		//try{
		while(!isStopRequested() && !opModeIsActive()){
			
			if(gamepad1.a)
				align = !align;
			if(gamepad1.b)
				placeMarker = !placeMarker;
			if(gamepad1.x)
				otherCrater = !otherCrater;
			//if(gamepad1.)
			//	avoidMinerals = !avoidMinerals;
			if(gamepad1.y)
				land = !land;
			if(gamepad1.right_bumper)
				wait7 = !wait7;
			
			telemetry.addData("Align (a)", align);
			telemetry.addData("Go to depot and place marker (b)", placeMarker);
			telemetry.addData("Go to opposite color crater (x) (sorta sketch)", otherCrater);
			telemetry.addData("Land (y)", land);
			telemetry.addData("Wait 7 (right bumper)", wait7);
			//telemetry.addData("Avoid Minerals (y)", avoidMinerals);
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
		
		if(!placeMarker){
			roobot.driveForwardAtSpeed(45, 0.5);
			return;
		}
		
		if(avoidMinerals) {// && !otherCrater){
			roobot.driveForward(7);
		} else {
	
			roobot.driveForward(20);
			roobot.resetEncoders();
	
			roobot.turn(88);
			roobot.resetEncoders();
	
			roobot.driveForward(-30);
			roobot.driveForwardAtSpeed(-44, 0.5);
			roobot.resetEncoders();
	
			roobot.turn(-43);
			roobot.resetEncoders();
	
			roobot.driveForward(-51);
			roobot.resetEncoders();
			
			roobot.deposit();
			
			if(otherCrater){
			
				roobot.turn(92);
				roobot.resetEncoders();
				
				roobot.alignToLander();
				roobot.resetEncoders();
			
				roobot.driveForward(70);
				roobot.driveForwardAtSpeed(95, 0.5);
				roobot.resetEncoders();
			}
			else {
				roobot.driveForward(70); 
				roobot.driveForwardAtSpeed(95, 0.5);
				roobot.resetEncoders();
			}
		} 
	}
	
	
}
