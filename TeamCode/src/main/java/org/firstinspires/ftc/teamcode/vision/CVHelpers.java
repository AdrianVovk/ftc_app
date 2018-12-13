package org.firstinspires.ftc.teamcode.vision;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

import java.util.Collections;
import java.util.List;

public class CVHelpers {

    /**
     * Converts a grayscale image to an RGB one and releases the original.
     * @param gray Grayscale image.
     * @return The RGB version of this image.
     */
    public static Mat gray2rgb(Mat gray){

        Mat rgb = new Mat();
        Imgproc.cvtColor(gray, rgb, Imgproc.COLOR_GRAY2RGB);

        return rgb;

    }

    /**
     * Calculates the center of a contour with the given points using moments.
     * @param points Points of the contour.
     * @return The center of the contour.
     */
    public static Point calculateCenterPoint(MatOfPoint points){

        Moments moments = Imgproc.moments(points);

        return new Point(moments.m10 / moments.m00, moments.m01 / moments.m00);

    }

    /**
     * Calculates the weighted average of a list of points.
     * @param points Points to average.
     * @param weights Weights to use.
     * @return Weighted arithmetic mean.
     */
    public static Point calculateAveragePoint(List<Point> points, List<Float> weights){

        if(points.size() == 0){

            return null;

        }

        float sx = 0;
        float sy = 0;
        float ws = 0;

        for(int i = 0; i < points.size(); i++){

            Point p = points.get(i);
            float w = weights.get(i);

            sx += p.x * w;
            sy += p.y * w;

            ws += w;

        }

        return new Point(sx / ws, sy / ws);

    }

    /**
     * Unweighted arithmetic mean of a list of points.
     * @param points Points to average.
     * @return Their mean.
     */
    public static Point calculateAveragePoint(List<Point> points){

        return calculateAveragePoint(points, Collections.nCopies(points.size(), 1f));

    }

}
