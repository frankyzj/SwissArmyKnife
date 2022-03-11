package com.example.swissarmyknife.api.zxing.CameraConfig;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;

public class CameraManager {
    private static final String TAG = "CameraManager";

    private static final int MIN_FRAME_WIDTH = 240;
    private static final int MIN_FRAME_HEIGHT = 240;
    private static final int MAX_FRAME_WIDTH = 1200; // = 5/8 * 1920
    private static final int MAX_FRAME_HEIGHT = 675; // = 5/8 * 1080

    private Rect framingRect;
    private Rect framingRectInPreview;

    private final Context context;
    private final CameraConfigurationManager configManager;
    private OpenCamera camera;

    public CameraManager(Context context) {
        this.context = context;
        this.configManager = new CameraConfigurationManager(context);
    }

    /**
     * 在手机屏幕上绘制用来框住二维码区域，确保用户在合适的距离将相机对准二维码。
     *
     * @return window 坐标下的矩形框
     */
    public synchronized Rect getFramingRect() {
        if (framingRect == null) {
            if (camera == null) {
                return null;
            }
            Point screenResolution = configManager.getScreenResolution();
            if (screenResolution == null) {
                // 调用太早，初始化还没有完成
                return null;
            }

            int width = findDesiredDimensionInRange(screenResolution.x, MIN_FRAME_WIDTH, MAX_FRAME_WIDTH);
            int height = findDesiredDimensionInRange(screenResolution.y, MIN_FRAME_HEIGHT, MAX_FRAME_HEIGHT);

            int leftOffset = (screenResolution.x - width) / 2;
            int topOffset = (screenResolution.y - height) /2;
            framingRect = new Rect(leftOffset, topOffset, leftOffset + width, topOffset + height);
        }
        return framingRect;
    }

    private static int findDesiredDimensionInRange(int resolution, int hardMin, int hardMax) {
        int dim = 5 * resolution / 8; // 取屏幕宽度的 5/8
        if (dim < hardMin) {
            return hardMin;
        }
        return Math.min(dim, hardMax);
    }

    /**
     * 预览帧的区域，而非 UI 或屏幕
     *
     * @return 预览帧的区域
     */
    public synchronized Rect getFramingRectInPreview() {
        if (framingRectInPreview == null) {
            Rect framingRect = getFramingRect();
            if (framingRect == null) {
                return null;
            }
            Rect rect = new Rect(framingRect);
            Point resolutionScreen = configManager.getScreenResolution();
            Point resolutionCamera = configManager.getCameraResolution();
            if (resolutionCamera == null || resolutionScreen == null) {
                return null;
            }
            // 为什么要这么算？
            rect.left = rect.left * resolutionCamera.x / resolutionScreen.x;
            rect.right = rect.right * resolutionCamera.x / resolutionScreen.x;
            rect.top = rect.top * resolutionCamera.y / resolutionScreen.y;
            rect.bottom = rect.bottom * resolutionCamera.y / resolutionScreen.y;
            framingRectInPreview = rect;
        }
        return framingRectInPreview;
    }
}
