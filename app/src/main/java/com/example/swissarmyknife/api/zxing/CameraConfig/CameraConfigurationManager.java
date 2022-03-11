package com.example.swissarmyknife.api.zxing.CameraConfig;

import android.content.Context;
import android.graphics.Point;
import android.hardware.Camera;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

/**
 * 读取、解析、设置相机参数
 */
public class CameraConfigurationManager {
    private static final String TAG = "CameraConfigManager";
    private final Context mContext;

    private int cwNeededRotation;
    private int cwRotationFromDisplayToCamera;

    private Point screenResolution; // 用屏幕最右下角的点坐标表示屏幕分辨率
    private Point cameraResolution;
    private Point bestPreviewSize;
    private Point previewSizeOnScreen;

    CameraConfigurationManager(Context context) {
        mContext = context;
    }

    /**
     * 从相机读取应用需要的参数: 镜头与屏幕的夹角？
     * @see #cwRotationFromDisplayToCamera,
     * @see #cwNeededRotation
     * 屏幕大小
     * @see #screenResolution
     * @see #cameraResolution
     * 预览图的大小——用右下角的点坐标表示，等
     * @see #previewSizeOnScreen
     * @see #bestPreviewSize
     *
     * @param camera 相机参数
     */
    public void initValuesFromCamera(OpenCamera camera) {
        Camera.Parameters parameters = camera.getCamera().getParameters();
        WindowManager manager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();

        int cwRotationOfDisplay = getCwRotationOfDisplay(display); // 垂直顺时针旋转角度

        int cwRotationOfCamera = camera.getOrientation();
        Log.i(TAG, "initValuesFromCamera: cwRotationOfCamera = " + cwRotationOfCamera);

        if (camera.getFacing() == CameraFacing.FRONT) {
            cwRotationOfCamera = (360 - cwRotationOfCamera) % 360; // 前置摄像头的话需要特殊处理
            Log.i(TAG, "initValuesFromCamera: cwRotationOfCamera = " + cwRotationOfCamera);
        }

        cwRotationFromDisplayToCamera =
                (360 + cwRotationOfCamera - cwRotationOfDisplay) % 360; // 考虑手动设置相机横竖方向的情况
        Log.i(TAG, "initValuesFromCamera: cwRotationFromDisplayToCamera = "
                + cwRotationFromDisplayToCamera);

        if (camera.getFacing() == CameraFacing.FRONT) {
            Log.i(TAG, "前置摄像头的补偿旋角");
            cwNeededRotation = (360 - cwRotationFromDisplayToCamera) % 360;
        } else {
            cwNeededRotation = cwRotationFromDisplayToCamera;
        }
        Log.i(TAG, "ClockWise rotation from display to camera: " + cwNeededRotation);

        initPreview(parameters, display);
    }

    private void initPreview(Camera.Parameters parameters, Display display) {
        Point theScreenResolution = new Point();
        display.getSize(theScreenResolution); // 当前应用屏幕的大小

        screenResolution = theScreenResolution;
        Log.i(TAG, "Screen resolution in current orientation: " + screenResolution);
        // 计算合适的预览图大小
        cameraResolution = CameraConfigurationUtils.findBestPreviewSizeValue(parameters, screenResolution);
        Log.i(TAG, "Camera resolution: " + cameraResolution);
        bestPreviewSize = CameraConfigurationUtils.findBestPreviewSizeValue(parameters, screenResolution);
        Log.i(TAG, "Best available preview size: " + bestPreviewSize);

        boolean isScreenPortrait = screenResolution.x < screenResolution.y;
        boolean isPreviewSizePortrait = bestPreviewSize.x < bestPreviewSize.y;

        if (isScreenPortrait == isPreviewSizePortrait) {
            previewSizeOnScreen = bestPreviewSize;
        } else {
            previewSizeOnScreen = new Point(bestPreviewSize.y, bestPreviewSize.x);
        }
        Log.i(TAG, "Preview size on screen: " + previewSizeOnScreen);
    }

    private int getCwRotationOfDisplay(Display display) {
        int cwRotationOfDisplay;
        int displayRotation = display.getRotation();
        switch (displayRotation) {
            case Surface.ROTATION_0:
                cwRotationOfDisplay = 0;
                break;
            case Surface.ROTATION_90:
                cwRotationOfDisplay = 90;
                break;
            case Surface.ROTATION_180:
                cwRotationOfDisplay = 180;
                break;
            case Surface.ROTATION_270:
                cwRotationOfDisplay = 270;
                break;
            default:
                // 如果出现 -90 这样的值
                if (displayRotation % 90 == 0) {
                    cwRotationOfDisplay = (360 + displayRotation) % 360;
                } else {
                    throw new IllegalArgumentException("Bad rotation: " + displayRotation);
                }
        }
        Log.i(TAG, "getCwRotationOfDisplay: " + cwRotationOfDisplay);
        return cwRotationOfDisplay;
    }

    public Point getScreenResolution() {
        return screenResolution;
    }

    public Point getCameraResolution() {
        return cameraResolution;
    }
}
