package com.idreamspace.skindetection.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.navigation.fragment.NavHostFragment;

import com.google.common.util.concurrent.ListenableFuture;
import com.idreamspace.skindetection.R;
import com.idreamspace.skindetection.app.AppEntity;
import com.idreamspace.skindetection.config.UriConfig;
import com.idreamspace.skindetection.databinding.FragmentSecondBinding;
import com.idreamspace.skindetection.net.HttpUpload;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class SecondFragment extends Fragment {
    private final String TAG = "Camera.";

    private final String[] permissions = new String[]{Manifest.permission.CAMERA};

    private CameraSelector lensFacing = CameraSelector.DEFAULT_FRONT_CAMERA;

    private final Integer REQUEST_CODE_PERMISSIONS = 10;

    private ImageCapture imageCapture = null;

    private File outputDirectory = null;

    private FragmentSecondBinding binding;


    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentSecondBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    private void nextFragment() {
        NavHostFragment.findNavController(SecondFragment.this)
                .navigate(R.id.action_SecondFragment_to_FirstFragment);
    }

    private void toFirstFragment() {
        NavHostFragment.findNavController(SecondFragment.this)
                .navigate(R.id.action_SecondFragment_to_next_Fragment);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.outputDirectory = this.getOutputDirectory();
        if (this.allPermissionsGranted()) {
            this.startCamera();
        } else {
            ActivityCompat.requestPermissions(this.getActivity(), this.permissions, this.REQUEST_CODE_PERMISSIONS);
        }
        binding.buttonSecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SecondFragment.this.nextFragment();
            }
        });

        binding.buttonSecond3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                SecondFragment.this.toFirstFragment();
                takePhoto();
            }
        });
    }

    private void startCamera() {
        PreviewView previewView = binding.viewFinder;

        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this.getActivity());

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());
                CameraSelector cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA;
                imageCapture = new ImageCapture.Builder()
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .build();
                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(
                        ((LifecycleOwner) SecondFragment.this.getActivity()),
                        cameraSelector,
                        preview,
                        imageCapture
                );
            } catch (ExecutionException | InterruptedException e) {
//                    e.printStackTrace();
                Log.e(TAG, "Use case binding failed", e);
            }
        }, ContextCompat.getMainExecutor(this.getActivity()));
    }

    private void takePhoto() {
        if (null == this.imageCapture) return;

        String FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS";
        File photoFile = new File(
                this.outputDirectory,
                new SimpleDateFormat(FILENAME_FORMAT, Locale.CHINA).format(System.currentTimeMillis()) + ".jpg"
        );

        ImageCapture.OutputFileOptions outputOptions = new ImageCapture.OutputFileOptions.Builder(photoFile).build();

        this.imageCapture.takePicture(
                outputOptions,
                ContextCompat.getMainExecutor(this.getActivity()),
                new ImageCapture.OnImageSavedCallback() {

                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                        Uri saveUri = Uri.fromFile(photoFile);
                        String msg = "Photo capture succeeded: " + saveUri;
//                        Toast.makeText(SecondFragment.this.getActivity(), msg, Toast.LENGTH_SHORT).show();
                        Log.d(TAG, msg);
                        Log.d(TAG, "name: " + photoFile.getName());
                        Log.d(TAG, "size: " + photoFile.length());
                        Log.d(TAG, "path: " + photoFile.getPath());
                        AppEntity app = (AppEntity) SecondFragment.this.getActivity().getApplication();
                        HttpUpload upload = app.getComponent(HttpUpload.class);
//                        upload.byFile(photoFile, "http://10.0.2.2:8080/upload.php");
                        upload.byFile(photoFile, UriConfig.UPLOAD_URL);
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        Log.e(TAG, "Photo capture failed: " + exception.getMessage(), exception);
                    }
                }
        );
    }

    private boolean allPermissionsGranted() {
        return ContextCompat.checkSelfPermission(this.getActivity().getBaseContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private File getOutputDirectory() {
        Log.i(TAG, "pkg name: " + this.getActivity().getPackageName());
        return Arrays.stream(this.getActivity().getExternalMediaDirs())
                .findFirst()
                .orElse(
                        new File(this.getActivity().getPackageName(), getResources().getString(R.string.app_name))
                );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}