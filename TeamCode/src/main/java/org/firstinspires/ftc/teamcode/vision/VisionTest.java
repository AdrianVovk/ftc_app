package org.firstinspires.ftc.teamcode.vision;

import android.annotation.SuppressLint;

import com.disnodeteam.dogecv.CameraViewDisplay;
import com.disnodeteam.dogecv.Dogeforia;
import com.disnodeteam.dogecv.filters.DogeCVColorFilter;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

//import com.us.cvlib.CameraViewDisplay;

import org.firstinspires.ftc.robotcore.external.hardware.camera.CameraName;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.teamcode.vision.FirstPythonVisionTranslation;

@TeleOp(name="Rover Ruckus Vision")
public class VisionTest extends OpMode {

    private FirstPythonVisionTranslation vision;

    private int currentLineIdx = 0;
    private int currentRowIdx = 0;

    private ElapsedTime time = new ElapsedTime();

    private boolean downPressedLastTime = false;
    private boolean upPressedLastTime = false;
    private boolean leftPressedLastTime = false;
    private boolean rightPressedLastTime = false;

    @Override
    public void init() {
        VuforiaLocalizer.Parameters params = new VuforiaLocalizer.Parameters();
        params.vuforiaLicenseKey = "AcyPCov/////AAABmSDbwrb0ZEiDttYV7rcRVBJZck+hOLvbd6tGd1xaHSf4b1UiFh1OQagZxNLol03/mVebiaI/2jo5YvXKrDdHTraBV9CUfHDePhGd4ASNQ56gA7RNGc+v7GaZbVxvc3mPlMkzP/lLrxEvSIc6l3b43B1IyQGPNwjh8Xky3ClKkVVA/GoYjMZCxXyba8cQliDhuHVZ1AB9lBd4fUjtLOy86tbL6EbnAu9+NJeOnhOPN8HOYruqBu6UvL39kKqrSBCZPxiwrxUuSvZSL8hiPV92Ad3r74el5TvvhO/OZVgfQw9dReHK3Ef+KNzZWRv3jcrel2BTYRCD9nugBmF7GnkHymeZkmbIchW/OcWgxMUbf1vF";
        params.cameraName = hardwareMap.get(CameraName.class, "Webcam 1");
        params.fillCameraMonitorViewParent = true;

        vision = new FirstPythonVisionTranslation(true);
        vision.init(hardwareMap.appContext, CameraViewDisplay.getInstance(), 0, true);
        Dogeforia vuforia = new Dogeforia(params);
        vuforia.enableConvertFrameToBitmap();
        vuforia.enableDogeCV();
        vuforia.setDogeCVDetector(vision);
        vuforia.start();

        time.reset();

    }

    @Override
    public void init_loop() {
        //constantEditor();

    }

    @Override
    public void loop() {

        //constantEditor();

    }

    @SuppressLint("DefaultLocale")
    public void constantEditor(){

        /**
         * The variables to edit are:
         * DOWNSCALE_FACTOR
         * BLUR_RADIUS
         * EROSION_ITERATIONS
         * MIN_CONTOUR_CUTOFF_AREA_GOLD
         * MAX_CIRCULARITY_ABNORMALITY_GOLD
         * FILTER_BY_COLOR
         * BLOB_COLOR
         * MIN_BLOB_THRESHOLD
         * MAX_BLOB_THRESHOLD
         * FILTER_BY_AREA
         * MIN_AREA
         * FILTER_BY_CIRCULARITY
         * MIN_BLOB_CIRCULARITY
         * MAX_BLOB_CIRCULARITY
         * FILTER_BY_INTERTIA
         * MIN_INERTIAL_AREA
         * MAX_INERTIAL_AREA
         * YELLOW_MIN_YUV
         * 	Y
         * 	U
         * 	V
         * YELLOW_MAX_YUV
         * 	Y
         * 	U
         * 	V
         * BLACK_HLS_MIN
         * 	H
         * 	L
         * 	S
         * BLACK_HLS_MAX
         * 	H
         * 	L
         * 	S
         * WHITE_HLS_MIN
         * 	H
         * 	L
         * 	S
         * WHITE_HLS_MAX
         * 	H
         * 	L
         * 	S
         * MIN_SILVER_CIRCULARITY
         * MIN_SILVER_AREA
         */

        int[] rowLengths = {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 3, 3, 3, 3, 1, 1};
        StringBuilder[] lines = new StringBuilder[22];

        for(int i = 0; i < lines.length; i++){

            lines[i] = new StringBuilder();

        }

        lines[currentLineIdx].append((int) time.seconds() % 2 == 0 ? String.format("[%d] ", currentRowIdx + 1) : "[  ] ");

        lines[0].append(String.format("DOWNSCALE_FACTOR: %.2f", vision.CONSTANTS.DOWNSCALE_FACTOR));
        lines[1].append(String.format("BLUR_RADIUS: %d", vision.CONSTANTS.BLUR_RADIUS));
        lines[2].append(String.format("EROSION_ITERATIONS: %d", vision.CONSTANTS.EROSION_ITERATIONS));
        lines[3].append(String.format("MIN_CONTOUR_CUTOFF_AREA_GOLD: %.2f", vision.CONSTANTS.MIN_CONTOUR_CUTOFF_AREA_GOLD));
        lines[4].append(String.format("MAX_CIRCULARITY_ABNORMALITY_GOLD: %.2f", vision.CONSTANTS.MAX_CIRCULARITY_ABNORMALITY_GOLD));
        lines[5].append(String.format("FILTER_BY_COLOR: %b", vision.CONSTANTS.FILTER_BY_COLOR));
        lines[5].append(String.format("BLOB_COLOR: %d", vision.CONSTANTS.BLOB_COLOR));
        lines[6].append(String.format("MIN_BLOB_THRESHOLD: %d", vision.CONSTANTS.MIN_BLOB_THRESHOLD));
        lines[7].append(String.format("MAX_BLOB_THRESHOLD: %d", vision.CONSTANTS.MAX_BLOB_THRESHOLD));
        lines[8].append(String.format("FILTER_BY_AREA: %b", vision.CONSTANTS.FILTER_BY_AREA));
        lines[9].append(String.format("MIN_AREA: %b", vision.CONSTANTS.MIN_AREA));
        lines[10].append(String.format("FILTER_BY_CIRCULARITY: %b", vision.CONSTANTS.FILTER_BY_CIRCULARITY));
        lines[11].append(String.format("MIN_BLOB_CIRCULARITY: %d", vision.CONSTANTS.MIN_BLOB_CIRCULARITY));
        lines[12].append(String.format("MAX_BLOB_CIRCULARITY: %d", vision.CONSTANTS.MAX_BLOB_CIRCULARITY));
        lines[13].append(String.format("FILTER_BY_INTERTIA: %b", vision.CONSTANTS.FILTER_BY_INTERTIA));
        lines[14].append(String.format("MIN_INERTIAL_AREA: %b", vision.CONSTANTS.MIN_INERTIAL_AREA));
        lines[15].append(String.format("MAX_INERTIAL_AREA: %b", vision.CONSTANTS.MAX_INERTIAL_AREA));
        lines[16].append(String.format("YELLOW_MIN_YUV: %1$s %2$s %3$s", vision.CONSTANTS.YELLOW_MIN_YUV.val[0], vision.CONSTANTS.YELLOW_MIN_YUV.val[1], vision.CONSTANTS.YELLOW_MIN_YUV.val[2]));
        lines[17].append(String.format("YELLOW_MAX_YUV: %1$s %2$s %3$s", vision.CONSTANTS.YELLOW_MAX_YUV.val[0], vision.CONSTANTS.YELLOW_MAX_YUV.val[1], vision.CONSTANTS.YELLOW_MAX_YUV.val[2]));
        lines[18].append(String.format("WHITE_HLS_MIN: %1$s %2$s %3$s", vision.CONSTANTS.WHITE_HLS_MIN.val[0], vision.CONSTANTS.WHITE_HLS_MIN.val[1], vision.CONSTANTS.WHITE_HLS_MIN.val[2]));
        lines[19].append(String.format("WHITE_HLS_MAX: %1$s %2$s %3$s", vision.CONSTANTS.WHITE_HLS_MAX.val[0], vision.CONSTANTS.WHITE_HLS_MAX.val[1], vision.CONSTANTS.WHITE_HLS_MAX.val[2]));
        lines[20].append(String.format("MIN_SILVER_CIRCULARITY: %b", vision.CONSTANTS.MIN_SILVER_CIRCULARITY));
        lines[21].append(String.format("MIN_SILVER_AREA: %b", vision.CONSTANTS.MIN_SILVER_AREA));

        // Change the appropriate value
        float increase = gamepad1.right_trigger;
        increase = increase > 0.95f ? 1.01f : increase;
        float decrement = gamepad1.left_trigger;
        decrement = decrement > 0.95f ? 1.01f : decrement;
        float delta = increase - decrement;

        // no
        // also crashing
        vision.CONSTANTS.DOWNSCALE_FACTOR += delta * 0.005;
        vision.CONSTANTS.BLUR_RADIUS += delta;
        vision.CONSTANTS.EROSION_ITERATIONS += delta;
        vision.CONSTANTS.MIN_CONTOUR_CUTOFF_AREA_GOLD += delta;
        vision.CONSTANTS.MAX_CIRCULARITY_ABNORMALITY_GOLD += delta * 0.005;
        vision.CONSTANTS.FILTER_BY_COLOR = delta > 0;
        vision.CONSTANTS.BLOB_COLOR += delta;
        vision.CONSTANTS.MIN_BLOB_THRESHOLD += delta;
        vision.CONSTANTS.MAX_BLOB_THRESHOLD += delta;
        vision.CONSTANTS.FILTER_BY_AREA = delta > 0;
        vision.CONSTANTS.MIN_AREA += delta;
        vision.CONSTANTS.FILTER_BY_CIRCULARITY = delta > 0;
        vision.CONSTANTS.MIN_BLOB_CIRCULARITY += delta * 0.005;
        vision.CONSTANTS.MAX_BLOB_CIRCULARITY += delta * 0.005;

        boolean right = !rightPressedLastTime && gamepad1.dpad_right;
        boolean left = !leftPressedLastTime && gamepad1.dpad_left;
        boolean up = !upPressedLastTime && gamepad1.dpad_up;
        boolean down = !downPressedLastTime && gamepad1.dpad_down;


        rightPressedLastTime = gamepad1.dpad_right;
        leftPressedLastTime = gamepad1.dpad_left;
        upPressedLastTime = gamepad1.dpad_up;
        downPressedLastTime = gamepad1.dpad_down;

        // Move the * row marker and column marker
        if(down){

            currentLineIdx++;
            // currentLineIdx %= lines.length;
            currentRowIdx = 0;

        }

        if(up){

            currentLineIdx--;
            currentRowIdx = 0;

            if(currentLineIdx < 0){

                currentLineIdx = lines.length - 1;

            }

        }

        if(right){

            currentRowIdx++;
            currentRowIdx %= rowLengths[currentLineIdx];

        }

        if(left){

            currentRowIdx--;
            currentRowIdx = currentRowIdx < 0 ? rowLengths[currentLineIdx] : currentRowIdx;

        }

        for(StringBuilder line : lines){

            telemetry.addLine(line.toString());

        }

        telemetry.addLine(down ? "down" : "not down");
        telemetry.addLine("" + currentLineIdx);

    }

    @Override
    public void stop() {

        vision.disable();

    }
}
