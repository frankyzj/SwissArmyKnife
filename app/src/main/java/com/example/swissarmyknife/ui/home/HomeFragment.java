package com.example.swissarmyknife.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.swissarmyknife.R;

public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";

    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        root.setOnClickListener(v -> Log.i(TAG, "onCreateView: "));

        final RecyclerView recyclerView = root.findViewById(R.id.features_items);
        FeatureItemAdapter adapter = new FeatureItemAdapter(getActivity());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        homeViewModel.getItems().observe(getViewLifecycleOwner(), s -> {
            adapter.setItems(s);
            adapter.notifyDataSetChanged();
        });
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}