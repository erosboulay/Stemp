package com.example.stemp;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.example.stemp.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_main);
        replaceFragment(new HomeFragment()); // initialize app at home layout

        //TODO: add loading bar

        // listen to user switch fragments
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int clicked_id = item.getItemId();

            if (clicked_id == R.id.home) {
                replaceFragment(new HomeFragment());
            }
            else if (clicked_id == R.id.profile) {
                replaceFragment(new ProfileFragment());
            }
            else if (clicked_id == R.id.settings){
                    replaceFragment(new SettingsFragment());
            }
            return true;
        });

    }

    // for switching fragments
    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction =  fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }


}