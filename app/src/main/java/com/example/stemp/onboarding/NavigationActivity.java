package com.example.stemp.onboarding;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Html;
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
import androidx.viewpager.widget.ViewPager;

import com.example.stemp.MainActivity;
import com.example.stemp.R;

public class NavigationActivity extends AppCompatActivity {
    // swiper no swiping

    // perms cst
    private static final String PERMISSION_CALL_LOG = Manifest.permission.READ_CALL_LOG;
    private static final int PERMISSION_REQ_CODE = 100;

    // instantiate all necessary items
    ViewPager slideViewPager;
    LinearLayout dotIndicator;
    Button backButton, nextButton;
    TextView[] dots;
    ViewPagerAdapter viewPagerAdapter;

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
        SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);

        // get rid of asystem bars ie make fullscreen
        WindowInsetsControllerCompat windowInsetsController =
                WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars());

        setContentView(R.layout.activity_navigation);
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
                if (getItem(0) == 1)
                    requestRuntimePermission();
                else if (getItem(0) < 2)
                    slideViewPager.setCurrentItem(getItem(1), true);
                else {
                    Intent i = new Intent(NavigationActivity.this, MainActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        });


        slideViewPager = findViewById(R.id.slideViewPager);

        // disable swiping
        slideViewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        // ease-in , ease-out



        dotIndicator = (LinearLayout) findViewById(R.id.dotIndicator);
        viewPagerAdapter = new ViewPagerAdapter(this);
        slideViewPager.setAdapter(viewPagerAdapter);
        setDotIndicator(0); // coded here
        slideViewPager.addOnPageChangeListener(viewPagerListener);
    }
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
        dots[position].setTextColor(getResources().getColor(R.color.lavender, getApplicationContext().getTheme()));
    }
    private int getItem(int i) {
        return slideViewPager.getCurrentItem() + i;
    }

    // perms functions
    private void requestRuntimePermission(){
        if (ActivityCompat.checkSelfPermission(this, PERMISSION_CALL_LOG) == PackageManager.PERMISSION_GRANTED){
            slideViewPager.setCurrentItem(getItem(1), true);
        }
        else{
            ActivityCompat.requestPermissions(this, new String[]{PERMISSION_CALL_LOG}, PERMISSION_REQ_CODE);
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQ_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                slideViewPager.setCurrentItem(getItem(1), true);
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