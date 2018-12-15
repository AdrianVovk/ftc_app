package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;

public class BeltDriveHardware {
    public static final double TICKS_IN_ROT = 537.6;
    public static final double INCHES_TO_TICKS = TICKS_IN_ROT / 17.0;
    public static final double DEGREES_TO_TICKS = TICKS_IN_ROT / 60.0;

    public DcMotor driveLF, driveLB, driveRF, driveRB, intakeSpin, horizSlides, rightLift, leftLift;
    public Servo intakePaddle, intakeRotLeft, intakeRotRight, depositorRot, ratchet;
    public CRServo intake;
    public ColorSensor leftCol, rightCol;
    public WebcamName webcam;
    private LinearOpMode opMode = null;

    //////////
    // Init //
    //////////

    public BeltDriveHardware(LinearOpMode opMode) throws InterruptedException {
        this(opMode.hardwareMap);
        this.opMode = opMode;
    }

    public BeltDriveHardware(HardwareMap map) throws InterruptedException {
        // Drive motors
        this.driveLF = configureMotor(map, "dLF", true);
        this.driveLB = configureMotor(map, "dLB", false);
        this.driveRF = configureMotor(map, "dRF", false);
        this.driveRB = configureMotor(map, "dRB", true);

        // The alignment color sensors
        this.leftCol = map.colorSensor.get("leftColor");
        this.rightCol = map.colorSensor.get("rightColor");

        // Intake and lifts
        //this.intakeSpin = configureMotor(map, "intakeSpin", false);
        this.leftLift = configureMotor(map, "leftLift", false);
        this.rightLift = configureMotor(map, "rightLift", false);
        this.ratchet = map.servo.get("ratchet");
        //this.intakePaddle = map.servo.get("intakePaddle");
        this.intakeRotLeft = map.servo.get("intakeRotLeft");
        //this.intakeRotRight = map.servo.get("intakeRotRight");
        //this.intakeRotRight.setDirection(Servo.Direction.REVERSE);
        //this.depositorRot =

        //int cameraMonitorViewId = hardware.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardware.appContext.getPackageName());

        reset();
    }

    private DcMotor configureMotor(HardwareMap map, String name, boolean reverse) {
        DcMotor m = map.dcMotor.get(name);
        m.setDirection(reverse ? DcMotor.Direction.REVERSE : DcMotor.Direction.FORWARD);
        m.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        resetEncoder(m);
        m.setPower(0);
        return m;
    }

    public void reset() throws InterruptedException {
        stop(); // Stop moving
        resetEncoders(); // We do those
        intakeRotLeft.setPosition(1);
        //setIntakePower(0); // Stop the intake from spinning
        //lowerIntake(false); // Lift up the intake
    }

    public void resetEncoders() throws InterruptedException {
        stop();
        resetEncoder(driveRF);
        resetEncoder(driveRB);
        resetEncoder(driveLF);
        resetEncoder(driveLB);
        stop();
    }

    private void resetEncoder(DcMotor motor) {
        motor.setPower(0.0);
        motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    /////////////////////
    // Simple controls //
    /////////////////////

    public void land() throws InterruptedException {
        ratchet.setPosition(0);
        setVertSlidesPower(1.0);
        Thread.sleep(800);
        setVertSlidesPower(0.0);
    }

    public void lowerLift() throws InterruptedException {
        setVertSlidesPower(-1.0);
        Thread.sleep(700);
        setVertSlidesPower(0);
    }

    public void deposit() throws InterruptedException {
        intakeRotLeft.setPosition(0);
        Thread.sleep(1500);
    }

    public void setPower(Double left, Double right) {
        if (left != null) {
            driveLF.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            driveLF.setPower(left);
            driveLB.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            driveLB.setPower(left);
        }

        if (right != null) {
            driveRF.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            driveRF.setPower(right);
            driveRB.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            driveRB.setPower(right);
        }
    }

    // So this is easy to use with the gamepad
    public void setPower(float left, float right) {
        setPower((double) left, (double) right);
    }

    public void stop() throws InterruptedException {
        setPower(0.0, 0.0);
        if (!active()) return;
        Thread.sleep(100);
    }

    public void setIntakePower(double power) {
        intakeSpin.setPower(Math.max(power, power < 0 ? -0.5 : 0.5));
    }

    public void lowerIntake(boolean lowered) {
        double position = lowered ? 1 : -1;
        intakeRotLeft.setPosition(position);
        intakeRotRight.setPosition(position);
    }

    public void setPaddle(boolean paddle) {
        intakePaddle.setPosition(paddle ? 1.0 : 0.5);
    }

    public void setHorizSlidesPower(double power) {
        horizSlides.setPower(power);
    }

    public void setVertSlidesPower(double power) {

        leftLift.setPower(1.0 * power);
        rightLift.setPower(1.0 * power);
    }

    /////////////////
    // Dumb Drive  //
    /////////////////


    public void driveForward(double inLeft, double speedL, double inRight, double speedR) throws InterruptedException {
        driveForwardTicks((int) (inLeft * INCHES_TO_TICKS), speedL, (int) (inRight * INCHES_TO_TICKS), speedR);
    }

    private void driveForwardTicks(int lTicks, double lSpeed, int rTicks, double rSpeed) throws InterruptedException {
        stop();

        driveLF.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        driveRF.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        driveLB.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        driveRB.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        driveLF.setTargetPosition(lTicks);
        driveRF.setTargetPosition(rTicks);
        driveLB.setTargetPosition(lTicks);
        driveRB.setTargetPosition(rTicks);

        driveLF.setPower(lSpeed);
        driveRF.setPower(rSpeed);
        driveLB.setPower(lSpeed);
        driveRB.setPower(rSpeed);

        while (active() && ((driveRF.isBusy() && driveRB.isBusy()) || (driveLF.isBusy() && driveLB.isBusy())))
            ;
        stop();
    }

    public void driveForward(double in) throws InterruptedException {
        driveForwardAtSpeed(in, 1.0);
    }

    public void driveForward(double inLeft, double inRight) throws InterruptedException {
        driveForward(inLeft, 1.0, inRight, 1.0);
    }

    public void driveForwardAtSpeed(double in, double speed) throws InterruptedException {
        driveForward(in, speed, in, speed);
    }

    public void turn(int degrees) throws InterruptedException {
        int ticks = (int) (degrees * DEGREES_TO_TICKS);
        driveForwardTicks(ticks, 1.0, -ticks, 1.0);
    }

    public double getCurrentPosition() {
        return (getLeftPosition() + getRightPosition()) / 2.0;
    }

    public double getLeftPosition() {
        int position = driveLF.getCurrentPosition();
        position += driveLB.getCurrentPosition();
        position /= 2.0;
        return position / INCHES_TO_TICKS;
    }

    public double getRightPosition() {
        int position = driveRF.getCurrentPosition();
        position += driveRB.getCurrentPosition();
        position /= 2.0;
        return position / INCHES_TO_TICKS;
    }

    //////////////////
    // Color Sensor //
    //////////////////

    private boolean colorSensorOnTape(ColorSensor s) {
        boolean onBlue = s.blue() - s.red() >= 10;
        boolean onRed = s.red() - s.green() >= 10;
        return onBlue || onRed;
    }

    public boolean leftOnTape() {
        return colorSensorOnTape(leftCol);
    }

    public boolean rightOnTape() {
        return colorSensorOnTape(rightCol);
    }

    public boolean alignToLander() throws InterruptedException {
        resetEncoders();
        int count = 0;
        while (active() && !(leftOnTape() && rightOnTape())) {
            // Go Forward & Back
            setPower(0.2, 0.2);
            while (active() && getCurrentPosition() <= 15 && !(leftOnTape() || rightOnTape())) ;
            stop();
            setPower(-0.2, -0.2);
            while (active() && getCurrentPosition() >= -15 && !(leftOnTape() || rightOnTape())) ;
            stop();

            // Only go F&B twice
            count++;
            if (!leftOnTape() && !rightOnTape())
                if (count < 2)
                    continue;
                else return false;

            if (!leftOnTape()) {
                while (active() && getLeftPosition() >= -6 && !leftOnTape()) setPower(-0.2, 0.1);
                while (active() && getLeftPosition() <= 6 && !leftOnTape()) setPower(0.2, -0.1);
            } else if (!rightOnTape()) {
                while (active() && getRightPosition() >= -6 && !rightOnTape()) setPower(0.1, -0.2);
                while (active() && getRightPosition() <= 6 && !rightOnTape()) setPower(-0.1, 0.2);
            } else return rightOnTape() && leftOnTape();
        }
        return true;
    }

    ///////////////////
    // Util Function //
    ///////////////////

    private boolean active() {
        return opMode != null && !opMode.isStopRequested() && opMode.opModeIsActive();
    }
}