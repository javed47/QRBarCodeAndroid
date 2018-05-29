package com.example.javed.qrbarscanner;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Camera;
import android.net.sip.SipSession;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.Manifest.permission.CAMERA;

public class MainActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView scannerView;
    private Button scanButton;
    private static final int Request_Camera = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Instantiating the ZxingScannerView
        scannerView = new ZXingScannerView(this);
        scanButton = (Button) findViewById(R.id.scan_button);
        //On button click initialize the bar code scanning by camera on
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setContentView(scannerView);


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkPermission()) {
                        Toast.makeText(MainActivity.this, "Permission Granted",
                        Toast.LENGTH_LONG).show();
                    } else {
                        requestPermission();
                    }
                }
                //This method call start the camera
                scannerView.startCamera();
            }
        });

        //Here the result been taken from the Scanner View
        scannerView.setResultHandler(this);

    }

    //Check if the permission is already given
    private boolean checkPermission() {
        return (ContextCompat.checkSelfPermission(MainActivity.this,
                CAMERA) == PackageManager.PERMISSION_GRANTED);
    }

    //If no permission is not given ask for the permission
    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{CAMERA}, Request_Camera);
    }

    //This is a wrap up method for the appcompat class
    public void onRequestPermissionsResult(int requestCode, String permissions[], int grantResults[]) {
        switch (requestCode) {
            case Request_Camera:
                if (grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted) {
                        //if permission is granted show message
                        Toast.makeText(MainActivity.this, "Permission Granted",
                                Toast.LENGTH_LONG).show();
                    } else {
                        //if the permission is not granted show message
                        Toast.makeText(MainActivity.this, "Permission Denied",
                                Toast.LENGTH_LONG).show();

                        //check the version
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(CAMERA)) {
                                //Make a dialog box
                                displayAlertMessage(
                                        "You need to Allow Access for Camera and AutoFocus",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{CAMERA}, Request_Camera);
                                                }
                                            }
                                        });
                                return;
                            }
                        }
                    }
                }
                break;
        }
    }

    //Initialize the dialog box
    public void displayAlertMessage(String message, DialogInterface.OnClickListener listner) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", listner)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        scannerView.startCamera();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkPermission()) {
                if (scannerView == null){
                    scannerView = new ZXingScannerView(this);
                }
                scannerView.setResultHandler(this);
                scannerView.startCamera();
            }
            else {
                requestPermission();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        scannerView.stopCamera();
    }

    //This method is implemented from ZXingScannerView.ResultHandler
    // which will actually create the result

    @Override
    public void handleResult(Result result) {
        String resultCode = result.getText();
        Toast.makeText(MainActivity.this, resultCode,
                Toast.LENGTH_LONG).show();
    }
}
