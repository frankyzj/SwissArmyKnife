package com.example.swissarmyknife.api.zxing;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import com.example.swissarmyknife.R;

public class ZxingActivity extends AppCompatActivity {
    private static final String TAG = "ZxingActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zxing);
        findViewById(R.id.zxing_btn).setOnClickListener(v -> {

        });
    }
}