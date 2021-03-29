package com.example.swissarmyknife.customview;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.swissarmyknife.R;

public class WindowUtils {
    WindowManager wm;
    Context mContext;
    FrameLayout frameLayout;

    public WindowUtils(Context context) {
        mContext = context;
        wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    }

    public void showView() {
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        // 设置屏幕尺寸
        layoutParams.height = 600;
        layoutParams.width = 800;
        //设置背景
        layoutParams.format = PixelFormat.TRANSPARENT;
        layoutParams.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND; // 不修改原值，增加背景变暗属性
        layoutParams.dimAmount = 0.6F;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }

        TextView textView = new TextView(mContext);
        textView.setText("Window System");
        textView.setBackgroundResource(R.color.teal_200);

        FrameLayout.LayoutParams params = new FrameLayout
                .LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        textView.setLayoutParams(params);
        textView.setOnClickListener(v -> {
            clearView();
        });

        frameLayout = new FrameLayout(mContext);
        frameLayout.setBackgroundColor(Color.BLUE);
        frameLayout.addView(textView);

        wm.addView(frameLayout, layoutParams);
    }

    public void clearView() {
        wm.removeView(frameLayout);
    }
}
