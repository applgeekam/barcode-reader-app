package com.example.barcodereader;

import androidx.annotation.NonNull;
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
import android.graphics.Point;
import android.graphics.Rect;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
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

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;


public class CapturerPage extends AppCompatActivity {


    Executor executor;
    int barcodeFormat;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capturer_page);

        barcodeFormat = this.getIntent().getExtras().getInt("format");

        initCamera();
    }


    public void initCamera()
    {
        PreviewView previewView = findViewById(R.id.previewView);

        ListenableFuture cameraProviderFuture =
                ProcessCameraProvider.getInstance(this);

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
        ImageAnalysis imageAnalysis =
                new ImageAnalysis.Builder()
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
                        if (barcodes.size() > 0)
                        {
                            close(showBarcodeReader(barcodes));
                        }
                        imageProxy.close();
                    })
                    .addOnFailureListener((OnFailureListener) e -> {Toast.makeText(getApplicationContext(), R.string.get_barcode_info_failed, Toast.LENGTH_LONG).show(); imageProxy.close();});

        }


    }


    public void close(String message)
    {
        this.getIntent().putExtra("result", message);
        setResult(2, this.getIntent());
        finish();
    }

    public String showBarcodeReader(List<Barcode> barcodes)
    {
//        for (Barcode barcode: barcodes) {
//            Rect bounds = barcode.getBoundingBox();
//            Point[] corners = barcode.getCornerPoints();
//
//            String rawValue = barcode.getRawValue();
//
//            int valueType = barcode.getValueType();
//            // See API reference for complete list of supported types
//            switch (valueType) {
//                case Barcode.TYPE_WIFI:
//                    String ssid = barcode.getWifi().getSsid();
//                    String password = barcode.getWifi().getPassword();
//                    int type = barcode.getWifi().getEncryptionType();
//                    break;
//                case Barcode.TYPE_URL:
//                    String title = barcode.getUrl().getTitle();
//                    String url = barcode.getUrl().getUrl();
//                    break;
//            }
//        }
        return String.valueOf(barcodes.size());
    }


}