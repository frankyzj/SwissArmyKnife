package com.example.swissarmyknife.api.zxing;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.swissarmyknife.R;
import com.example.swissarmyknife.api.zxing.CameraConfig.CameraManager;

import java.util.ArrayList;
import java.util.List;

public class QrCodeFinderView extends View {

    private static final int MAX_RESULT_POINT = 20;
    private static final long ANIMATION_DELAY = 80L;
    private static final int[] SCANNER_ALPHA = {0, 64, 128, 192, 255, 192, 128, 64};
    private static final int POINT_SIZE = 6;
    private static final int CURRENT_POINT_OPACITY = 0xA0;

    private CameraManager cameraManager;
    private final Paint paint;

    private Bitmap resultBitmap;
    private final int maskColor;
    private final int resultColor;
    private final int laserColor;
    private final int resultPointColor;

    private int scannerAlpha;
    private List<ResultPoint> possibleResultPoints;
    private List<ResultPoint> lastPossibleResultPoints;

    public QrCodeFinderView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.QrCodeFinderView);

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Resources resources = getResources();
        maskColor = typedArray.getColor(R.styleable.QrCodeFinderView_mask_color,
                resources.getColor(R.color.viewfinder_mask));
        resultColor = typedArray.getColor(R.styleable.QrCodeFinderView_result_color,
                resources.getColor(R.color.result_view));
        laserColor = typedArray.getColor(R.styleable.QrCodeFinderView_laser_color,
                resources.getColor(R.color.viewfinder_laser));
        resultPointColor = typedArray.getColor(R.styleable.QrCodeFinderView_result_point_color,
                resources.getColor(R.color.possible_result_points));

        typedArray.recycle();
        scannerAlpha = 0;
        possibleResultPoints = new ArrayList<>(5);
        lastPossibleResultPoints = null;
    }

    public void setCameraManager(CameraManager cameraManager) {
        this.cameraManager = cameraManager;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (cameraManager == null) {
            return;
        }
        // 用户框住二维码的区域
        Rect frame = cameraManager.getFramingRect();
        Rect previewFrame = cameraManager.getFramingRectInPreview();

        if (frame == null || previewFrame == null) {
            return;
        }
        int width = canvas.getWidth();
        int height = canvas.getHeight();

        // 绘制预览区周边的遮罩,拍摄中用浅黑，获取的二维码时用深黑
        paint.setColor(resultBitmap != null ? resultColor : maskColor);
        canvas.drawRect(0, 0, width, frame.top, paint);
        canvas.drawRect(0, frame.top, frame.left, frame.bottom + 1, paint);
        canvas.drawRect(frame.right + 1, frame.top, width, frame.bottom + 1, paint);
        canvas.drawRect(0, frame.bottom + 1, width, height, paint);

        if (resultBitmap != null) {
            // 将结果位图绘制到扫描区内
            paint.setAlpha(CURRENT_POINT_OPACITY);
            canvas.drawBitmap(resultBitmap, null, frame, paint);
        } else {
            // 绘制红色扫描线，告诉用户解码正在进行
            paint.setColor(laserColor);
            paint.setAlpha(SCANNER_ALPHA[scannerAlpha]);
            scannerAlpha = (scannerAlpha + 1) % SCANNER_ALPHA.length;
            int middle = frame.height() / 2 + frame.top;
            canvas.drawRect(frame.left + 2, middle - 1,
                    frame.right - 1, middle + 2, paint);

            float scaleX = frame.width() / (float) previewFrame.width();
            float scaleY = frame.height() / (float) previewFrame.height();

            List<ResultPoint> currentPossible = possibleResultPoints;
            List<ResultPoint> currentLast = lastPossibleResultPoints;
            int frameLeft = frame.left;
            int frameTop = frame.top;

            // 识别到点时，会调用 addPossibleResultPoint 方法往 possibleResultPoints 中添加
            if (currentPossible.isEmpty()) {
                lastPossibleResultPoints = null;
            } else {
                possibleResultPoints = new ArrayList<>(5);
                lastPossibleResultPoints = currentPossible;
                paint.setAlpha(CURRENT_POINT_OPACITY);
                paint.setColor(resultPointColor);
                synchronized (currentPossible) {
                    for (ResultPoint point : currentPossible) {
                        canvas.drawCircle(frameLeft + (int) (point.getX() * scaleX),
                                frameTop + (int) (point.getY() * scaleY),
                                POINT_SIZE, paint);
                    }
                }
            }
            if (currentLast != null) {
                paint.setAlpha(CURRENT_POINT_OPACITY / 2);
                paint.setColor(resultPointColor);
                synchronized (currentLast) {
                    float radius = POINT_SIZE / 2.0F;
                    for (ResultPoint point : currentLast) {
                        canvas.drawCircle(frameLeft + (int) (point.getX() * scaleX),
                                frameTop + (int) (point.getY() * scaleY),
                                radius, paint);
                    }
                }
            }

            // 重绘红色扫描线
            postInvalidateDelayed(ANIMATION_DELAY,
                    frame.left - POINT_SIZE,
                    frame.top - POINT_SIZE,
                    frame.right + POINT_SIZE,
                    frame.bottom + POINT_SIZE);
        }
    }

    /**
     * 回收 Bitmap 资源，并重绘，会再次调用到onDraw方法
     */
    public void drawViewfinder() {
        Bitmap resultBitmap = this.resultBitmap;
        this.resultBitmap = null;
        if (resultBitmap != null) {
            resultBitmap.recycle();
        }
        invalidate();
    }

    /**
     * 拿到结果后绘制二维码
     *
     * @param barcode 二维码照片
     */
    public void drawResultBitmap(Bitmap barcode) {
        resultBitmap = barcode;
        invalidate();
    }

    public void addPossibleResultPoint(ResultPoint point) {
        List<ResultPoint> points = possibleResultPoints;
        synchronized (point) {
            points.add(point);
            int size = points.size();
            if (size > MAX_RESULT_POINT) {
                points.subList(0, size - MAX_RESULT_POINT / 2).clear();
            }
        }
    }

}
