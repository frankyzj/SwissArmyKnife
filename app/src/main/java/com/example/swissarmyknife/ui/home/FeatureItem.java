package com.example.swissarmyknife.ui.home;

import android.app.Activity;

public class FeatureItem {
    private String title;
    private Class<? extends Activity> feature;

    public FeatureItem(String title, Class<? extends Activity> feature) {
        this.title = title;
        this.feature = feature;
    }

    public String getTitle() {
        return title;
    }

    public Class<? extends Activity> getFeature() {
        return feature;
    }
}
