package com.example.barcodereader;

import android.content.Context;
import android.widget.Toast;

public class Module {

    public void showToast(Context context, String message)
    {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public void showSnackbar()
    {
    }

}
