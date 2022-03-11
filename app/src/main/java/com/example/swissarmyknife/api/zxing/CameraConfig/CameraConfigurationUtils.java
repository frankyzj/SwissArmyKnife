package com.example.swissarmyknife.api.zxing.CameraConfig;

import android.graphics.Point;
import android.hardware.Camera;
import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CameraConfigurationUtils {
    private static final String TAG = "CameraConfigurationUtil";

    private static final int MIN_PREVIEW_PIXELS = 480 * 320;
    private static final double MAX_ASPECT_DISTORTION = 0.15;

    public static Point findBestPreviewSizeValue(Camera.Parameters parameters, Point screenResolution) {
        List<Camera.Size> rawSupportedSizes = parameters.getSupportedPreviewSizes();
        if (rawSupportedSizes == null) {
            Log.w(TAG, "Device returned no supported preview sizes; using default");
            return getDefaultPreviewSize(parameters);
        }

        StringBuilder previewSizesString = new StringBuilder();
        for (Camera.Size size : rawSupportedSizes) {
            previewSizesString.append(size.width).append('x').append(size.height).append(' ');
        }
        Log.i(TAG, "Supported preview sizes: " + previewSizesString);

        double screenAspectRatio = screenResolution.x / (double)screenResolution.y;

        int maxResolution = 0;
        Camera.Size maxResPreviewSize = null;
        // 选出很屏幕宽高相同的一组数据
        for (Camera.Size size : rawSupportedSizes) {
            int realWidth = size.width;
            int realHeight = size.height;
            int resolution = realWidth * realHeight;
            if (resolution < MIN_PREVIEW_PIXELS) {
                continue;
            }

            boolean isCandidatePortrait = realWidth < realHeight;
            // 处理屏幕横向的情况
            int maybeFlippedWidth = isCandidatePortrait ? realHeight : realWidth;
            int maybeFlippedHeight = isCandidatePortrait ? realWidth : realHeight;

            double aspectRatio = maybeFlippedWidth / (double)maybeFlippedHeight;
            double distortion = Math.abs(aspectRatio - screenAspectRatio);
            // 图像和屏幕比例的差异
            if (distortion > MAX_ASPECT_DISTORTION) {
                continue;
            }

            // 选出很屏幕宽高相同的一组数据
            if (maybeFlippedWidth == screenResolution.x
            && maybeFlippedHeight == screenResolution.y) {
                Point exactPoint = new Point(realWidth, realHeight);
                Log.i(TAG, "Found preview size exactly matching screen size: " + exactPoint);
                return exactPoint;
            }

            if (resolution > maxResolution) {
                maxResolution = resolution;
                maxResPreviewSize = size;
            }
        }

        // 如果没有完全匹配的，使用最大的预览尺寸
        if (maxResPreviewSize != null) {
            Point largestSize = new Point(maxResPreviewSize.width, maxResPreviewSize.height);
            Log.i(TAG, "Using largest suitable preview size: " + largestSize);
            return largestSize;
        }

        // 如果既没有匹配的也没有最大值，就使用当前值
        Camera.Size defaultPreview = parameters.getPreviewSize();
        if (defaultPreview == null) {
            throw new IllegalStateException("Parameters contained no preview size!");
        }

        Point defaultSize = new Point(defaultPreview.width, defaultPreview.height);
        Log.i(TAG, "No suitable preview sizes, using default: " + defaultSize);
        return defaultSize;
    }

    @NotNull
    private static Point getDefaultPreviewSize(Camera.Parameters parameters) {
        Camera.Size defaultSize = parameters.getPreviewSize();
        if (defaultSize == null) {
            throw new IllegalStateException("Parameters contained no preview size!");
        }
        return new Point(defaultSize.width, defaultSize.height);
    }
}
