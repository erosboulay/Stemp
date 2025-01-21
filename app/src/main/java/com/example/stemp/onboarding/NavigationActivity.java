package com.example.stemp.onboarding;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Html;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.splashscreen.SplashScreen;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.room.Room;
import androidx.viewpager.widget.ViewPager;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;
import androidx.work.WorkerParameters;

import com.example.stemp.MainActivity;
import com.example.stemp.R;
import com.example.stemp.db.Contact;
import com.example.stemp.db.ContactNumber;
import com.example.stemp.db.PhonecallDatabase;
import com.example.stemp.db.WorkerAdapter;

public class NavigationActivity extends AppCompatActivity {
    // swiper no swiping

    // perms cst
    private static final String PERMISSION_CALL_LOG = Manifest.permission.READ_CALL_LOG;
    private static final String PERMISSION_CONTACTS = Manifest.permission.READ_CONTACTS;
    private static final int PERMISSION_REQ_CODE = 100;

    // instantiate all necessary items
    ViewPager slideViewPager;
    LinearLayout dotIndicator;
    Button backButton, nextButton;
    TextView[] dots;
    ViewPagerAdapter viewPagerAdapter;

    // worker
    WorkRequest populateWorkRequest;

    // Update UI when you change a page
    ViewPager.OnPageChangeListener viewPagerListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            setDotIndicator(position);

            if (position > 0) {
                backButton.setVisibility(View.VISIBLE);
            } else {
                backButton.setVisibility(View.INVISIBLE);
            }
            if (position == 2){
                nextButton.setText("Get Started");
            } else if (position == 1){
                nextButton.setText("Allow");
            } else {
                nextButton.setText("Next");
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // show splashscreen
        SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);

        // check if onboarding has already been shown
        SharedPreferences sharedPref = getSharedPreferences("onboarding", Context.MODE_PRIVATE);
        boolean val = false;
        boolean defaultValue = sharedPref.getBoolean("done", val);
        if (defaultValue){
            Intent i = new Intent(NavigationActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        }

        // create database during onboarding process
        Log.d("DatabaseDebug", "Creating database");
        PhonecallDatabase db = PhonecallDatabase.getInstance(getApplicationContext());
        Log.d("DatabaseDebug", "Database is created");

        // Database shows up only when a query is made and a query cannot be done in main thread
        // for testing purpose
        //new Thread(() -> {
        //    db.phonecallDao().getAll();
        //}).start();

        // make first click interact
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        // get rid of system bars ie make fullscreen
        WindowInsetsControllerCompat windowInsetsController =
                WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars());

        setContentView(R.layout.activity_navigation);

        // set first page
        backButton = findViewById(R.id.backButton);
        nextButton = findViewById(R.id.nextButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getItem(0) > 0) {
                    slideViewPager.setCurrentItem(getItem(-1), true);
                }
            }
        });
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("NavigationActivity", "Next button clicked");
                int index = slideViewPager.getCurrentItem();
                Log.d("NavigationActivity", "Current Page: " + index);

                if (index == 1)
                    requestRuntimePermission();
                else if (index < 2)
                    slideViewPager.setCurrentItem(getItem(1), true);
                else {
                    // TODO: make a function for that maybe
                    // edit shared preferences to stop onboarding from showing when it is done
                    SharedPreferences sharedPref = getSharedPreferences("onboarding", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putBoolean("done", true);
                    editor.apply();

                    Intent i = new Intent(NavigationActivity.this, MainActivity.class);

                    startActivity(i);
                    finish();
                }
            }
        });

        slideViewPager = findViewById(R.id.slideViewPager);
        slideViewPager.setCurrentItem(0);

        // disable swiping
        slideViewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        dotIndicator = (LinearLayout) findViewById(R.id.dotIndicator);
        viewPagerAdapter = new ViewPagerAdapter(this);
        slideViewPager.setAdapter(viewPagerAdapter);
        setDotIndicator(0); // coded here
        slideViewPager.addOnPageChangeListener(viewPagerListener);
    }

    //TODO: solve dot indicator size on smaller devices, avoid overlapping
    public void setDotIndicator(int position) {
        dots = new TextView[3]; // make 3 dots
        dotIndicator.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226", Html.FROM_HTML_MODE_LEGACY));
            dots[i].setTextSize(100);
            dots[i].setTextColor(getResources().getColor(R.color.grey, getApplicationContext().getTheme()));
            dotIndicator.addView(dots[i]);
        }
        dots[position].setTextColor(getResources().getColor(R.color.blush, getApplicationContext().getTheme()));
    }
    private int getItem(int i) {
        return slideViewPager.getCurrentItem() + i;
    }

    // perms functions
    private void requestRuntimePermission(){
        if (checkPermissions()){
            slideViewPager.setCurrentItem(getItem(1), true);
            //TODO: begin background task to populate database

            // Create worker
            populateWorkRequest = new OneTimeWorkRequest.Builder(WorkerAdapter.class).build();
            WorkManager.getInstance(this.getApplicationContext()).enqueue(populateWorkRequest);

        }
        else{
            ActivityCompat.requestPermissions(this, new String[]{PERMISSION_CALL_LOG, PERMISSION_CONTACTS}, PERMISSION_REQ_CODE);
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQ_CODE){
            if(grantResults.length > 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                slideViewPager.setCurrentItem(getItem(1), true);
                //TODO: begin background task to populate database

                // Create worker
                populateWorkRequest = new OneTimeWorkRequest.Builder(WorkerAdapter.class).build();
                WorkManager.getInstance(this.getApplicationContext()).enqueue(populateWorkRequest);
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
    public boolean checkPermissions(){
        return ActivityCompat.checkSelfPermission(this, PERMISSION_CALL_LOG) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, PERMISSION_CONTACTS) == PackageManager.PERMISSION_GRANTED;
    }
}