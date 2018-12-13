package org.firstinspires.ftc.teamcode.vision;

import org.firstinspires.ftc.robotcore.external.hardware.camera.CameraCaptureRequest;
import org.firstinspires.ftc.robotcore.external.hardware.camera.CameraCaptureSequenceId;
import org.firstinspires.ftc.robotcore.external.hardware.camera.CameraFrame;
import org.opencv.core.Size;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.Log;

//import com.us.cvlib.CVHelpers;
//import com.us.cvlib.OpenCVPipeline;
//import com.us.cvlib.ViewDisplay;
import com.disnodeteam.dogecv.ViewDisplay;
import com.disnodeteam.dogecv.OpenCVPipeline;
import com.disnodeteam.dogecv.detectors.DogeCVDetector;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.vuforia.Vuforia;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.function.Continuation;
import org.firstinspires.ftc.robotcore.external.hardware.camera.Camera;
import org.firstinspires.ftc.robotcore.external.hardware.camera.CameraCaptureSession;
import org.firstinspires.ftc.robotcore.external.hardware.camera.CameraCharacteristics;
import org.firstinspires.ftc.robotcore.external.hardware.camera.CameraException;
import org.firstinspires.ftc.robotcore.external.hardware.camera.CameraName;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener;
import org.firstinspires.ftc.robotcore.internal.system.Deadline;
import org.opencv.core.Core;
import org.opencv.core.KeyPoint;
import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.features2d.FeatureDetector;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class FirstPythonVisionTranslation extends DogeCVDetector {//OpenCVPipeline {

    /**
     * Whether it should use a mask to mask out the crater and run any other crater/depot only algorithms
     */
    public boolean depotMode;

    /**
     * Many of the internal variables are reused here for memory reasons
     */
    private Mat rgbaDownscaled = new Mat();
    private Mat grayDownscaled = new Mat();

    private Mat yuv = new Mat();
    private Mat yuvThresholded = new Mat();
    private Mat grayYuvThresholded = new Mat();
    private Mat denoisedYuvThresholded = new Mat();

    private Mat blurred = new Mat();
    private Mat errodedBlur = new Mat();

    private Mat dewaffleified = new Mat();

    private Mat blobs = new Mat();

    private Mat returnMat = new Mat();

    private Mat erode = new Mat();
    private Mat dilated = new Mat();
    private Mat thresholded = new Mat();

    private Mat hls = new Mat();
    private Mat hlsThresholdedWhite = new Mat();
    private Mat hlsThresholdedWhiteDenoised = new Mat();

    private long lastTime = 0;

    public DetectionConstants CONSTANTS = new DetectionConstants();

    public FirstPythonVisionTranslation(boolean depotMode){
        this.depotMode = depotMode;

    }

    /*
    TODO:
    Figure out depot and crater masks (probably just use static masks)
    Re-tune blur radius and erosion iterations
    Figure out Feature2D.drawKeypoints
    Tune blob params
    FPS counter
    Error handling
    Confidence calculations (distance between different detection types, error states, etc)
    Check performance and memory usage
    Speed modes
    Look into refactoring this to be easier to use/more generalizable
    Figure out the system of averaging the contour points for gold exactly
    Figure out white denoising
    Finalize the final left, right, center calculation
    Vuforia localization
    */
    @Override
    public void useDefaults() {

    }

    @Override
    public Mat process(Mat rgba) {

        // Rescale the image
        Imgproc.resize(rgba, rgbaDownscaled, new Size(rgba.width() * CONSTANTS.DOWNSCALE_FACTOR, rgba.height() * CONSTANTS.DOWNSCALE_FACTOR));

        rgbaDownscaled.copyTo(returnMat);

        List<String> errors = new ArrayList<>();

        // YUV color thresholding for gold
        Imgproc.cvtColor(rgbaDownscaled, yuv, Imgproc.COLOR_RGB2YUV);
        Core.inRange(yuv, CONSTANTS.YELLOW_MIN_YUV, CONSTANTS.YELLOW_MAX_YUV, yuvThresholded);
        Log.e("LOOKFORME", Integer.toString(yuvThresholded.channels()));
        // Imgproc.cvtColor(yuvThresholded, grayYuvThresholded, Imgproc.COLOR_RGB2GRAY);
        denoisedYuvThresholded = denoise(yuvThresholded);

        // Detect and draw contours
        List<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(denoisedYuvThresholded, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
        Imgproc.drawContours(returnMat, contours, -1, new Scalar(255, 0, 0), 2);


        if(contours.size() == 0){

            errors.add("No contours detected [Gold]");

        }

        List<MatOfPoint> temp = new ArrayList<>();
        List<Point> filteredContourPoints = new ArrayList<>();
        List<Point> contourCenters = new ArrayList<>();

        // Filter out contours that are smaller than a certain area and then calculate the center of all the other contours
        for(MatOfPoint contour : contours){

            double area = Imgproc.contourArea(contour);

            if(area < CONSTANTS.MIN_CONTOUR_CUTOFF_AREA_GOLD){

                continue;

            }

            // Add the filtered contours to a temporary list
            temp.add(contour);

            // Calculate and record the centers of each
            Point center = CVHelpers.calculateCenterPoint(contour);
            contourCenters.add(center);

            List<Point> points = contour.toList();
            filteredContourPoints.addAll(points);

        }

        // Redraw the filtered contours in a new color
        contours = temp;
        Imgproc.drawContours(returnMat, contours, -1, new Scalar(0, 255, 0), 2);

        Point averageContourPoint = null;

        if(filteredContourPoints.size() == 0){

            errors.add("No filtered contours [Gold]");

        }

        else {

            // Calculate the average point and draw it to the image
            averageContourPoint = CVHelpers.calculateAveragePoint(filteredContourPoints);
            Imgproc.circle(returnMat, averageContourPoint, 5, new Scalar(0, 255, 0), 2);

            // Find the contour closest to the average point and check its circularity
            int closestIdx = 0;
            double closestDistance = 1000000;

            for(int i = 0; i < contourCenters.size(); i++){

                Point p = contourCenters.get(i);
                double distance = Math.hypot(p.x - averageContourPoint.x, p.y - averageContourPoint.y);

                if(distance < closestDistance){

                    closestIdx = i;
                    closestDistance = distance;

                }

            }

            // Do the circularity calculation
            MatOfPoint closestContour = contours.get(closestIdx);
            double arcLen = Imgproc.arcLength(new MatOfPoint2f(closestContour.toArray()), true);
            double area = Imgproc.contourArea(closestContour);
            double circularity = 4 * Math.PI * area / (arcLen * arcLen);

            if(Math.abs(0.785 - circularity) > CONSTANTS.MAX_CIRCULARITY_ABNORMALITY_GOLD){

                errors.add("Abnormal circularity of nearest contour point [Gold]");

            }

        }

        Point averageBlobPoint = null;

        if(CONSTANTS.blobDetector != null){

            // Get rid of the waffle pattern
            dewaffleified = dewaffleify(denoisedYuvThresholded);

            // Detect and draw the blobs
            MatOfKeyPoint detectedBlobs = new MatOfKeyPoint();
            CONSTANTS.blobDetector.detect(dewaffleified, detectedBlobs);
            // returnMat.copyTo(blobs);
            // Features2d.drawKeypoints(blobs, detectedBlobs, returnMat, new Scalar(255, 0, 0));

            List<KeyPoint> keypoints = detectedBlobs.toList();
            List<Point> keypointPts = new ArrayList<>();
            List<Float> keypointAreaSquareds = new ArrayList<>();

            for(int i = 0; i < keypoints.size(); i++){

                KeyPoint k = keypoints.get(i);
                keypointPts.add(k.pt);
                keypointAreaSquareds.add(k.size * k.size);

            }

            if(keypointPts.size() != 0){

                // Calculate the average blob point, using area ^ 2 as a weighting
                averageBlobPoint = CVHelpers.calculateAveragePoint(keypointPts, keypointAreaSquareds);
                Imgproc.circle(returnMat, averageBlobPoint, 5, new Scalar(0, 0, 255), 2);

            }

            else {

                errors.add("No blobs detected [Gold]");

            }

        }

        else {

            errors.add("Blob detector is null [Gold]");

        }

        if(averageBlobPoint != null && averageContourPoint != null){

            double distance = Math.hypot(averageBlobPoint.x - averageContourPoint.x, averageBlobPoint.y - averageContourPoint.y);

        }

        // HLS white detection
        Imgproc.cvtColor(rgbaDownscaled, hls, Imgproc.COLOR_RGB2HLS);

        // Threshold and denoise
        Core.inRange(hls, CONSTANTS.WHITE_HLS_MIN, CONSTANTS.WHITE_HLS_MAX, hlsThresholdedWhite);
        hlsThresholdedWhite.copyTo(hlsThresholdedWhiteDenoised); // For now, no denoising

        contours = new ArrayList<>();

        Imgproc.findContours(hlsThresholdedWhiteDenoised, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
        Imgproc.drawContours(returnMat, contours, -1, new Scalar(0, 0, 255), 2);

        if(contours.size() == 0){

            errors.add("No contours detected [Silver]");

        }

        temp.clear();
        contourCenters.clear();

        // errors.add("" + contours.size());

        // Filter out non circular and small contours and record the centers of all the rest
        for(MatOfPoint mOp : contours){

            double arcLen = Imgproc.arcLength(new MatOfPoint2f(mOp.toArray()), true);
            double area = Imgproc.contourArea(mOp);

            // Quick check for fake contours
            if(arcLen < 0.03 || area < 0.03){

                // errors.add("Fake contour removed");

                continue;

            }

            double circularity = 4 * Math.PI * area / (arcLen * arcLen);

            if(circularity > CONSTANTS.MIN_SILVER_CIRCULARITY && area > CONSTANTS.MIN_SILVER_AREA){

                temp.add(mOp);
                contourCenters.add(CVHelpers.calculateCenterPoint(mOp));
                // errors.add("Added one to temp");

            }

            else if(circularity < CONSTANTS.MIN_SILVER_CIRCULARITY){

                // errors.add("Circularity: " + Double.toString(circularity));

            }

            else if(area < CONSTANTS.MIN_SILVER_CIRCULARITY){

                // errors.add("Area: " + Double.toString(area));

            }

        }

        contours = temp;
        Imgproc.drawContours(returnMat, contours, -1, new Scalar(0, 255, 0), 2);

        if(contours.size() == 0){

            errors.add("No filtered contours [Silver]");

        }

        else {

            Point averageSilverContourPoint = CVHelpers.calculateAveragePoint(contourCenters);
            Imgproc.circle(returnMat, averageSilverContourPoint, 5, new Scalar(255, 0, 0), 2);

        }

        // Do the final calculations to determine left, right, or center

        int font = Core.FONT_HERSHEY_COMPLEX;
        int thickness = 2;
        float fontScale = 0.8f;
        Scalar color = new Scalar(255, 0, 255);
        Size fontSize = Imgproc.getTextSize("A", font, fontScale, thickness, null);
        Imgproc.putText(returnMat, "[Position]", new Point(10, 10 + fontSize.height), font, fontScale, color, thickness);

        int i = 0;
        for(; i < errors.size(); i++){
            String e = errors.get(i);
            Imgproc.putText(returnMat, e, new Point(10, 10 + (fontSize.height + 10) * (i + 2)), font, fontScale, color, thickness);
        }

        String fps = Float.toString(1000f / (System.currentTimeMillis() - lastTime));

        Imgproc.putText(returnMat, fps, new Point(10, 10 + (fontSize.height + 10) * (i + 2)), font, fontScale, color, thickness);

        lastTime = System.currentTimeMillis();

        // Imgproc.cvtColor(hlsThresholdedWhite, returnMat, Imgproc.COLOR_GRAY2RGB);

        return returnMat;

    }

    public Mat denoise(Mat mat, int blurRadius, int erosionIterations){

        Imgproc.blur(mat, blurred, new Size(blurRadius, blurRadius));
        Imgproc.erode(blurred, errodedBlur, CONSTANTS.ERODE_KERNEL, new Point(0, 0), erosionIterations);


        return errodedBlur;

    }

    public Mat dewaffleify(Mat mat){

        Point p = new Point(0, 0);

        Imgproc.erode(mat, erode, CONSTANTS.ERODE_KERNEL, p, 15);
        Imgproc.dilate(erode, dilated, CONSTANTS.ERODE_KERNEL, p, 15);

        Imgproc.threshold(dilated, thresholded, 80, 255, Imgproc.THRESH_BINARY);

        return thresholded;

    }

    public Mat denoise(Mat mat, int erosionIterations){

        return denoise(mat, CONSTANTS.BLUR_RADIUS, erosionIterations);

    }

    public Mat denoise(Mat mat){

        return denoise(mat, CONSTANTS.BLUR_RADIUS, CONSTANTS.EROSION_ITERATIONS);

    }

}

class DetectionConstants {

    /**
     * Downscaling factor applied to all images before processing them
     */
    public float DOWNSCALE_FACTOR = 1f;

    /**
     * Default blur radius applied in the denoising function
     */
    public int BLUR_RADIUS = (int) (36f * DOWNSCALE_FACTOR);

    /**
     * Default number of erosions applied in the denoising function
     */
    public int EROSION_ITERATIONS = 2;

    /**
     * The kernel used for the erode
     */
    public Mat ERODE_KERNEL = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new  Size(2 * 5 + 1, 2 * 5 + 1));

    /**
     * Minimum area of contours to use when detecting gold
     */
    public float MIN_CONTOUR_CUTOFF_AREA_GOLD = 2700 * DOWNSCALE_FACTOR * DOWNSCALE_FACTOR;

    /**
     * Maximum difference from the expected square circularity that the detected gold contour
     * can be without reporting a warning
     */
    public float MAX_CIRCULARITY_ABNORMALITY_GOLD = 0.115f;

    /**
     * These are the blob detection parameters.
     */
    public boolean FILTER_BY_COLOR = true;
    public int BLOB_COLOR = 255;
    public int MIN_BLOB_THRESHOLD = 10;
    public int MAX_BLOB_THRESHOLD = 260;
    public boolean FILTER_BY_AREA = true;
    public int MIN_AREA = 100;
    public boolean FILTER_BY_CIRCULARITY = false;
    public int MIN_BLOB_CIRCULARITY = 1;
    public int MAX_BLOB_CIRCULARITY = 1;
    public boolean FILTER_BY_INTERTIA = true;
    public int MIN_INERTIAL_AREA = 0;
    public int MAX_INERTIAL_AREA = 1;

    /**
     * The blob detector for secondary gold detection
     */
    public FeatureDetector blobDetector = FeatureDetector.create(FeatureDetector.SIMPLEBLOB);

    /**
     * The min and max YUV thresholds for gold thresholding
     */
    public Scalar YELLOW_MIN_YUV = new Scalar(100, 0, 100);
    public Scalar YELLOW_MAX_YUV = new Scalar(255, 100, 200);

    /**
     * The min and max HLS thresholds for black (crater rim) detection
     */
    public Scalar BLACK_HLS_MIN = new Scalar(0, 0, 0);
    public Scalar BLACK_HLS_MAX = new Scalar(255, 50, 255);

    /**
     * The min and max HLS thresholds for white (silver) detection
     */
    public Scalar WHITE_HLS_MIN = new Scalar(0, 215, 0);
    public Scalar WHITE_HLS_MAX = new Scalar(255, 255, 255);

    /**
     * Minimum circularity to still detect a contour as circular for silver detection
     */
    public float MIN_SILVER_CIRCULARITY = 0.33f;

    /**
     * Minimum area to still detect a contour for silver detection. Since area scales with
     * width ^ 2, we multiply by DOWNSCALE_FACTOR ^ 2 (100 at 0.1 downscale)
     */
    public float MIN_SILVER_AREA = 12000 * DOWNSCALE_FACTOR * DOWNSCALE_FACTOR;

    private Context appContext;

//    /**
//     * Creates a set of constants with the default values
//     * @param appContext App context to use for file loading
//     */
//    public DetectionConstants(Context appContext) {
//
//        //this.appContext = appContext;
//        //this.loadBlobParams();
//
//    }

    /*public void writeParams(){

        try {

            FileOutputStream fos = appContext.openFileOutput("DetectionConstants.dat", Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(this);
            os.close();
            fos.close();

        } catch(IOException e) {

            e.printStackTrace();

        }

    }

    /**
     * Loads in the previously saved appContext
     * @param appContext The app context to use.
     * @return Constants from the previous run or the hardcoded defaults if there is an error.
     *
    public static DetectionConstants loadConstants(Context appContext){

        try {

            FileInputStream fis = appContext.openFileInput("DetectionConstants.dat");
            ObjectInputStream is = new ObjectInputStream(fis);

            DetectionConstants self = (DetectionConstants) is.readObject();
            self.appContext = appContext;

            // Make sure that the blob detector is set properly
            self.loadBlobParams();

            is.close();
            fis.close();

            return self;

        } catch (ClassNotFoundException e) {

            e.printStackTrace();

        } catch (IOException e) {

            e.printStackTrace();

        }

        return new DetectionConstants(appContext);

    }

    /**
     * This method writes all of the blob detection parameters to a config file and loads them
     * in to the blob detector.
     *
    public void loadBlobParams(){

        StringBuilder config = new StringBuilder();

        config.append("<?xml version=\"1.0\"?>\n");
        config.append("<opencv_storage>\n");

        config.append("<thresholdStep>10.</thresholdStep>\n");

        config.append("<minThreshold>");
        config.append(MIN_BLOB_THRESHOLD);
        config.append("</minThreshold>\n");

        config.append("<maxThreshold>");
        config.append(MAX_BLOB_THRESHOLD);
        config.append("</maxThreshold>\n");

        config.append("<minRepeatability>2</minRepeatability>\n");

        config.append("<minDistBetweenBlobs>10.</minDistBetweenBlobs>\n");

        config.append("<filterByColor>");
        config.append(FILTER_BY_COLOR ? 1 : 0);
        config.append("</filterByColor>\n");

        config.append("<blobColor>");
        config.append(BLOB_COLOR);
        config.append("</blobColor>\n");

        config.append("<filterByArea>");
        config.append(FILTER_BY_AREA ? 1 : 0);
        config.append("</filterByArea>\n");

        config.append("<minArea>");
        config.append(MIN_AREA);
        config.append("</minArea>\n");

        config.append("<maxArea>");
        config.append(Integer.MAX_VALUE);
        config.append("</maxArea>\n");

        config.append("<filterByCircularity>");
        config.append(FILTER_BY_CIRCULARITY ? 1 : 0);
        config.append("</filterByCircularity>\n");

        config.append("<minCircularity>");
        config.append(MIN_BLOB_CIRCULARITY);
        config.append("</minCircularity>\n");

        config.append("<maxCircularity>");
        config.append(MAX_BLOB_CIRCULARITY);
        config.append("</maxCircularity>\n");

        config.append("<filterByInertia>");
        config.append(FILTER_BY_INTERTIA ? 1 : 0);
        config.append("</filterByInertia>\n");

        config.append("<minInertiaRatio>");
        config.append(MIN_INERTIAL_AREA);
        config.append("</minInertiaRatio>\n");

        config.append("<maxInertiaRatio>");
        config.append(MAX_INERTIAL_AREA);
        config.append("</maxInertiaRatio>\n");

        config.append("<filterByConvexity>1</filterByConvexity>\n");
        config.append("<minConvexity>0.95</minConvexity>\n");
        config.append("<maxConvexity>" + Integer.MAX_VALUE + "</maxConvexity>\n");
        config.append("</opencv_storage>\n");

        try{

            File file = File.createTempFile("blobConfig.xml", null, appContext.getCacheDir());
            FileOutputStream outputStream = new FileOutputStream(file);
            outputStream.write(config.toString().getBytes());
            outputStream.close();

            blobDetector.read(file.getPath()); // figure out if this works

        } catch(Exception e){

            e.printStackTrace();

        }

    }*/

}
