package com.idreamspace.skindetection.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.idreamspace.skindetection.R;
import com.idreamspace.skindetection.databinding.FragmentFirstBinding;
import com.idreamspace.skindetection.lifecycle.AppVIewModel;

public class FirstFragment extends Fragment {

    public final static String TAG = "FirstFragment.";

//    private AppVIewModel model;

    private FragmentFirstBinding binding;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    private void nextFragment() {
        NavHostFragment.findNavController(FirstFragment.this)
                .navigate(R.id.action_FirstFragment_to_SecondFragment);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirstFragment.this.nextFragment();
            }
        });

        AppVIewModel model = new ViewModelProvider(requireActivity()).get(AppVIewModel.class);
        model.getQrcode().observe(getViewLifecycleOwner(), qrcode -> {
            Log.d(TAG, "push qrcode: " + qrcode);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}