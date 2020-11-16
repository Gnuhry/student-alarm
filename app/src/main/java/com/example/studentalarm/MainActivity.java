package com.example.studentalarm;

import android.content.IntentFilter;
import android.os.Bundle;

import com.example.studentalarm.Fragments.SettingsFragment;
import com.example.studentalarm.Receiver.NetworkReceiver;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;

public class MainActivity extends AppCompatActivity {
    public static BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.registerReceiver(new NetworkReceiver(), new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
        this.registerReceiver(new NetworkReceiver(), new IntentFilter("android.net.wifi.WIFI_STATE_CHANGED"));

        bottomNav = findViewById(R.id.bottomNav);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_);
        if (navHostFragment != null)
            NavigationUI.setupWithNavController(bottomNav, navHostFragment.getNavController());

        String lan = PreferenceManager.getDefaultSharedPreferences(this).getString(PreferenceKeys.LANGUAGE, null);
        if (lan == null)
            PreferenceKeys.Default(this);
        else if (!lan.equals(PreferenceKeys.DEFAULT_LANGUAGE(this)))
            SettingsFragment.ChangeLanguage(lan, this);
    }
}