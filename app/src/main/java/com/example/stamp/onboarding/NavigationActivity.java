package com.example.stamp.onboarding;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;
import androidx.viewpager.widget.ViewPager;

import com.example.stamp.MainActivity;
import com.example.stamp.R;

public class NavigationActivity extends AppCompatActivity {
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
            } else {
                nextButton.setText("Next");
            }
        }
        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SplashScreen.installSplashScreen(this);
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
                if (getItem(0) < 2)
                    slideViewPager.setCurrentItem(getItem(1), true);
                else {
                    Intent i = new Intent(NavigationActivity.this, MainActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        });
        slideViewPager = (ViewPager) findViewById(R.id.slideViewPager);
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
}