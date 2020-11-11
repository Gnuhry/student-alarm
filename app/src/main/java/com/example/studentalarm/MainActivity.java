package com.example.studentalarm;

import android.os.Bundle;

import com.example.studentalarm.Fragments.AlarmFragment;
import com.example.studentalarm.Fragments.LectureFragment;
import com.example.studentalarm.Fragments.SchoolFragment;
import com.example.studentalarm.Fragments.SettingsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        openFragment(new AlarmFragment());

        bottomNav.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.alarm)
                openFragment(new AlarmFragment());
            else if (itemId == R.id.lecture)
                openFragment(new LectureFragment());
            else if (itemId == R.id.school)
                openFragment(new SchoolFragment());
            else if (itemId == R.id.setting)
                openFragment(new SettingsFragment());
            else
                return false;
            return true;
        });

    }

    public void openFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }
}