package com.example.swissarmyknife.ui.home;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class FeatureItemAdapter extends RecyclerView.Adapter<FeatureItemAdapter.FeaturesItemsViewHolder> {

    private ArrayList<FeatureItem> mItems;

    private Activity mActivity;

    public FeatureItemAdapter(Activity activity) {
        mActivity = activity;
    }

    @NonNull
    @Override
    public FeaturesItemsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        return new FeaturesItemsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeaturesItemsViewHolder holder, int position) {
        holder.textView.setText(mItems.get(position).getTitle());
        holder.textView.setOnClickListener(v -> {
            startFeatureActivity(mItems.get(position).getFeature());
        });
    }

    private void startFeatureActivity(Class<? extends Activity> feature) {
        Intent intent = new Intent();
        intent.setClass(mActivity, feature);
        mActivity.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public void setItems(ArrayList<FeatureItem> items) {
        mItems = items;
    }

    static class FeaturesItemsViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        public FeaturesItemsViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(android.R.id.text1);
        }
    }
}
