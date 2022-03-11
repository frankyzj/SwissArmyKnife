package com.example.swissarmyknife.ui.home;

import android.view.View;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.swissarmyknife.api.zxing.ZxingActivity;

import java.util.ArrayList;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<ArrayList<FeatureItem>> mItems;

    public HomeViewModel() {
        mItems = new MutableLiveData<>();
        ArrayList<FeatureItem> items = new ArrayList<>();
        FeatureItem zxingItem = new FeatureItem("Zxing", ZxingActivity.class);
        items.add(zxingItem);
        mItems.setValue(items);
    }

    public LiveData<ArrayList<FeatureItem>> getItems() {
        return mItems;
    }
}