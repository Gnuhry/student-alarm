package com.example.studentalarm;

import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;

import com.example.studentalarm.Fragments.AlarmFragment;
import com.example.studentalarm.Fragments.LectureFragment;
import com.example.studentalarm.Fragments.SchoolFragment;
import com.example.studentalarm.Fragments.SettingsFragment;
import com.example.studentalarm.Receiver.NetworkReceiver;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;

public class MainActivity extends AppCompatActivity {
    private final static String TAG = "FRAGMENT";
    int lastId, beforeLastId;
    public static BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.registerReceiver(new NetworkReceiver(), new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
        this.registerReceiver(new NetworkReceiver(), new IntentFilter("android.net.wifi.WIFI_STATE_CHANGED"));

        bottomNav = findViewById(R.id.bottomNav);
        String s = PreferenceManager.getDefaultSharedPreferences(this).getString(PreferenceKeys.LANGUAGE, null);
        if (s == null)
            PreferenceManager.getDefaultSharedPreferences(this).edit().putString(PreferenceKeys.LANGUAGE, "EN").apply();
        else if (!s.equals("EN")) {
            Resources resources = getResources();
            DisplayMetrics dm = resources.getDisplayMetrics();
            Configuration config = resources.getConfiguration();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                config.setLocale(new Locale(s.toLowerCase()));
            } else {
                config.locale = new Locale(s.toLowerCase());
            }
            resources.updateConfiguration(config, dm);
            bottomNav.getMenu().clear();
            bottomNav.inflateMenu(R.menu.bottom_nav_menu);
        }
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

    /**
     * open a fragment
     * @param fragment the fragment to open
     */
    public void openFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment, TAG);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}