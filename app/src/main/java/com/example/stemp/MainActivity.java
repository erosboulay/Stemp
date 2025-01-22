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
import androidx.work.Data;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.example.stemp.databinding.ActivityMainBinding;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);

        // add loading bar
        setContentView(R.layout.progress_bar);

        //TODO: initialize progress bar to 0 percent

        // gotta make array otherwise it can't be changed and stuff
        final int[] local_progress = {0};
        boolean caught_up = false;

        // TODO: FINISH IT!!!
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
                                            // UPDATE UI SOME KINDA WAY
                                            if (caught_up){
                                                local_progress[0] = value;
                                            }
                                            else{
                                                local_progress[0] += 1;
                                            }
                                            break;
                                        case SUCCEEDED:
                                            // UPDATE UI ANOTHER KINDA WAY
                                            if (caught_up){
                                                local_progress[0] = 100;
                                            }
                                            else{
                                                local_progress[0] += 1;
                                            }
                                            //
                                            setContentView(R.layout.activity_main);
                                            replaceFragment(new HomeFragment());
                                            break;
                                        case FAILED:
                                            // MAYBE HAVE A RETRY BUTTON
                                        case CANCELLED:
                                            // ALSO HAVE A RETRY BUTTON
                                    }
                                }
                            }
                        });

        //TODO: get worker and change layout if worker is done


        // setContentView(R.layout.activity_main);
        //replaceFragment(new HomeFragment()); // initialize app at home layout


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