package com.example.stemp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.example.stemp.databinding.ActivityMainBinding;
import com.example.stemp.db.WorkerAdapter;
import com.example.stemp.onboarding.NavigationActivity;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    // worker
    OneTimeWorkRequest populateWorkRequest;
    // progress bar
    ProgressBar pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);

        SharedPreferences sharedPref = getSharedPreferences("populate_database", Context.MODE_PRIVATE);
        boolean val = true;
        boolean populate = sharedPref.getBoolean("populate", val);

        // populates only once
        if (populate){
            populate();
        }
        else{
            // set main activity view
            setContentView(R.layout.activity_main);
            replaceFragment(new HomeFragment());
        }


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

    private void populate(){
        // get progressbar
        setContentView(R.layout.progress_bar);
        pb = findViewById(R.id.progressBar);
        pb.setProgress(0, true);

        // begin background task to populate database
        populateWorkRequest = new OneTimeWorkRequest.Builder(WorkerAdapter.class).build(); // Create worker
        WorkManager.getInstance(this.getApplicationContext()).enqueueUniqueWork("populateWorkRequest", ExistingWorkPolicy.REPLACE, populateWorkRequest);

        // monitors worker
        WorkManager.getInstance(this)
                .getWorkInfosForUniqueWorkLiveData("populateWorkRequest")
                .observe(this, new Observer<List<WorkInfo>>() {
                    @Override
                    public void onChanged(List<WorkInfo> workInfos) {
                        if (workInfos != null){

                            int last = workInfos.size() - 1;
                            WorkInfo workinfo = workInfos.get(last);
                            WorkInfo.State state =  workinfo.getState();
                            Data progress = workinfo.getProgress();
                            int value = progress.getInt("PROGRESS", 0);

                            switch(state){
                                case ENQUEUED:
                                    // do nothing
                                    break;
                                case RUNNING:
                                    // UPDATE LOADING BAR
                                    pb.setProgress(value, true);
                                    break;
                                case SUCCEEDED:
                                    // UPDATE LOADING BAR AND SHOW MAIN SCREEN
                                    pb.setProgress(100, true);
                                    SharedPreferences sharedPref = getSharedPreferences("populate_database", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPref.edit();
                                    editor.putBoolean("populate", false);
                                    editor.apply();
                                    // set main activity view
                                    setContentView(R.layout.activity_main);
                                    replaceFragment(new HomeFragment());
                                    break;
                                case FAILED:
                                    // MAYBE HAVE A RETRY BUTTON
                                    break;
                                case CANCELLED:
                                    // ALSO HAVE A RETRY BUTTON
                                    break;
                            }
                        }
                    }
                });

    }


}