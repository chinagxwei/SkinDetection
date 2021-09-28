package com.idreamspace.skindetection.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.idreamspace.skindetection.R;
import com.idreamspace.skindetection.databinding.FragmentThreeBinding;

public class ThreeFragment extends Fragment {

    private FragmentThreeBinding binding;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentThreeBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    private void nextFragment() {
        NavHostFragment.findNavController(ThreeFragment.this)
                .navigate(R.id.action_to_SecondFragment);
    }

    private void toFirstFragment() {
        NavHostFragment.findNavController(ThreeFragment.this)
                .navigate(R.id.action_to_FirstFragment);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ThreeFragment.this.nextFragment();
            }
        });

        binding.buttonThree2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ThreeFragment.this.toFirstFragment();
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}