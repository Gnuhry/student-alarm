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
    private final static String TAG = "FRAGMENT";
    int lastId, beforeLastId;

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
            beforeLastId = lastId;
            lastId = itemId;
            return true;
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(TAG);
        if (fragment instanceof AlarmFragment)
            bottomNav.setSelectedItemId(R.id.alarm);
        else if (fragment instanceof LectureFragment)
            bottomNav.setSelectedItemId(R.id.lecture);
        else if (fragment instanceof SchoolFragment)
            bottomNav.setSelectedItemId(R.id.school);
        else if (fragment instanceof SettingsFragment)
            bottomNav.setSelectedItemId(R.id.setting);
    }

    public void openFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment, TAG);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }
}