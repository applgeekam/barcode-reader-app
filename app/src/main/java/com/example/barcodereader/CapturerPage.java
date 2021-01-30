package com.example.barcodereader;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.Barcode;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.common.InputImage;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;


public class CapturerPage extends AppCompatActivity {

    final static int CODE = 2;
    Executor executor;
    int barcodeFormat;
    boolean isDone;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capturer_page);

        barcodeFormat = this.getIntent().getExtras().getInt("format");
        isDone = false;

        initCamera();
    }


    public void initCamera()
    {
        PreviewView previewView = findViewById(R.id.previewView);

        ListenableFuture cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        executor = ContextCompat.getMainExecutor(this);

        cameraProviderFuture.addListener(() -> {
            try {
                // Camera provider is now guaranteed to be available
                ProcessCameraProvider cameraProvider = (ProcessCameraProvider) cameraProviderFuture.get();

                // Set up the view finder use case to display camera preview
                Preview preview = new Preview.Builder().build();

                // Choose the camera by requiring a lens facing
                CameraSelector cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                        .build();


                // Attach use cases to the camera with the same lifecycle owner
                Camera camera = cameraProvider.bindToLifecycle(
                        ((LifecycleOwner) this),
                        cameraSelector,
                        getImageAnalyser(),
                        preview
                        );

                // Connect the preview use case to the previewView
                preview.setSurfaceProvider(
                        previewView.getSurfaceProvider());

            } catch (InterruptedException | ExecutionException e) {
                // Currently no exceptions thrown. cameraProviderFuture.get()
                // shouldn't block since the listener is being called, so no need to
                // handle InterruptedException.
                Toast.makeText(getApplicationContext(), "Error occur when add listener", Toast.LENGTH_LONG).show();
            }
        }, executor );
    }

    public ImageAnalysis getImageAnalyser()
    {
        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                        .setTargetResolution(new Size(1280, 720))
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();

        imageAnalysis.setAnalyzer(executor, new ImageAnalysis.Analyzer() {
            @Override
            public void analyze(@NonNull ImageProxy image) {
                onCaptureCallback(image);
            }
        });

        return imageAnalysis;
    }


    public void onCaptureCallback(ImageProxy imageProxy)
    {
        @SuppressLint("UnsafeExperimentalUsageError") Image mediaImage = imageProxy.getImage();
        if (mediaImage != null) {
            InputImage image = InputImage.fromMediaImage(mediaImage, imageProxy.getImageInfo().getRotationDegrees());

            BarcodeScannerOptions options = new BarcodeScannerOptions.Builder().setBarcodeFormats(barcodeFormat).build();

            BarcodeScanner scanner = BarcodeScanning.getClient(options);

            Task<List<Barcode>> result = scanner.process((InputImage) image)
                    .addOnSuccessListener((OnSuccessListener<List<Barcode>>) barcodes -> {
                        if (barcodes.size() > 0 && !isDone)
                        {
                            isDone = true;
                            close(formatBarcodeToRead(barcodes));
                        }
                        imageProxy.close();
                    })
                    .addOnFailureListener((OnFailureListener) e -> {
                        Toast.makeText(getApplicationContext(), R.string.get_barcode_info_failed, Toast.LENGTH_LONG).show();
                        imageProxy.close();
                    });

        }


    }


    public void close(ArrayList result)
    {

        Intent intent = new Intent(getApplicationContext(), ShowInfo.class);
        intent.putExtra("list", result);
        startActivityForResult(intent, CODE);

    }

    public ArrayList<HashMap<String, String>> formatBarcodeToRead(List<Barcode> barcodes)
    {
        ArrayList<HashMap<String, String>> defaultList = new ArrayList<HashMap<String, String>>();
        for (Barcode barcode: barcodes) {
            int valueType = barcode.getValueType();
            HashMap<String, String> item= new HashMap<String, String>();
            switch (valueType) {
                case Barcode.TYPE_WIFI:
                    item.put("type", "WIFI");
                    item.put("ssid", barcode.getWifi().getSsid());
                    item.put("password", barcode.getWifi().getPassword());
                    item.put("encryption", String.valueOf(barcode.getWifi().getEncryptionType()));
                    break;
                case Barcode.TYPE_URL:
                    item.put("type", "URL");
                    item.put("title", barcode.getUrl().getTitle());
                    item.put("url", barcode.getUrl().getUrl());
                    break;
                default:
//                    Todo : show the correct value type
                    item.put("type", "DEFAULT");
                    item.put("value", barcode.getDisplayValue());
                    item.put("encoded", barcode.getRawValue());
                    break;
            }
            defaultList.add(item);
        }

        return defaultList;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODE)
        {
            isDone = false;
            Log.i("Activity", "onActivityResult: Back to camera view");
        }
    }


}