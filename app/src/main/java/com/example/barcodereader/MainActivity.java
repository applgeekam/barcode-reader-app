package com.example.barcodereader;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.mlkit.vision.barcode.Barcode;


public class MainActivity extends AppCompatActivity {

    Button btnStart;
    final static int CODE = 1;
    private RadioGroup radioformatGroup;
    private static final int CAMERA_PERMISSION_CODE = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnStart = (Button) findViewById(R.id.btnStart);
        radioformatGroup = (RadioGroup) findViewById(R.id.radioFormatGroup);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CapturerPage.class);

                switch (radioformatGroup.getCheckedRadioButtonId())
                {
                    case R.id.radioQR:
                        intent.putExtra("format", Barcode.FORMAT_QR_CODE);
                        break;
                    case R.id.radioCB:
                        intent.putExtra("format", Barcode.FORMAT_CODABAR);
                        break;
                    case R.id.radioAll:
                        intent.putExtra("format", Barcode.FORMAT_ALL_FORMATS);
                        break;
                }
                startActivityForResult(intent, CODE);


            }
        });

//        Get permission to access camera
        checkPermission();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CODE)
        {
            try {
                Log.i("result", data.getExtras().getString("result"));
            }catch (Exception e){

                if (data == null)
                {
                    Log.i("Activity", "onActivityResult: No intent send by back activity");
                }
                Log.i("Error", "onActivityResult: " + e.toString());

            }
        }
    }

    // Function to check and request permission
    public void checkPermission()
    {
        // Checking if permission is not granted
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(
            MainActivity.this,
            new String[] { Manifest.permission.CAMERA },
            CAMERA_PERMISSION_CODE);
        }
    }

    // This function is called when user accept or decline the permission.
    // Request Code is used to check which permission called this function.
    // This request code is provided when user is prompt for permission.

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_PERMISSION_CODE) {

            // Checking whether user granted the permission or not.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                // Showing the toast message
                Toast.makeText(MainActivity.this, "Camera Permission Granted", Toast.LENGTH_SHORT).show();
            }
            else {
                btnStart.setEnabled(false);
            }
        }
    }


}