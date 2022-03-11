package com.example.swissarmyknife.api.zxing.CameraConfig;

import android.hardware.Camera;

/**
 * 封装一个已开启的相机的参数
 */
public class OpenCamera {
    private final int index;
    private final Camera camera;
    private final CameraFacing facing;
    private final int orientation;

    public OpenCamera(int index, Camera camera, CameraFacing facing, int orientation) {
        this.index = index;
        this.camera = camera;
        this.facing = facing;
        this.orientation = orientation;
    }

    public Camera getCamera() {
        return camera;
    }

    public CameraFacing getFacing() {
        return facing;
    }

    public int getOrientation() {
        return orientation;
    }
}
