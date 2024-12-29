package com.example.stamp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.splashscreen.SplashScreen;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private static final String PERMISSION_CALL_LOG = Manifest.permission.READ_CALL_LOG;
    private static final int PERMISSION_REQ_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        requestRuntimePermission();

    }

    private void requestRuntimePermission(){
        if (ActivityCompat.checkSelfPermission(this, PERMISSION_CALL_LOG) == PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this, "Permission Granted.", Toast.LENGTH_LONG).show();
        }
        else{
            ActivityCompat.requestPermissions(this, new String[]{PERMISSION_CALL_LOG}, PERMISSION_REQ_CODE);
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQ_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permission Granted. You can use the API now", Toast.LENGTH_LONG).show();
            }
            else{
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("This app needs those permissions. If you want to experience the app, please give permissions. You may need to go to phone settings.")
                        .setTitle("Permission Required")
                        .setCancelable(false)
                        .setNegativeButton("Ok", ((dialog, which)-> dialog.dismiss()));
                builder.show();
            }
        }
    }
}