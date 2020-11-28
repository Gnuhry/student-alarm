package com.example.studentalarm;

import android.content.IntentFilter;
import android.os.Bundle;

import com.example.studentalarm.fragments.SettingsFragment;
import com.example.studentalarm.receiver.NetworkReceiver;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Calendar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.registerReceiver(new NetworkReceiver(), new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
        this.registerReceiver(new NetworkReceiver(), new IntentFilter("android.net.wifi.WIFI_STATE_CHANGED"));

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_);
        if (navHostFragment != null)
            NavigationUI.setupWithNavController((BottomNavigationView) findViewById(R.id.bottomNav), navHostFragment.getNavController());

        String lan = PreferenceManager.getDefaultSharedPreferences(this).getString(PreferenceKeys.LANGUAGE, null);
        if (lan == null)
            PreferenceKeys.Default(this);
        else if (!lan.equals(PreferenceKeys.DEFAULT_LANGUAGE(this)))
            new SettingsFragment().ChangeLanguage(lan, this, this);


//        Calendar calendar=Calendar.getInstance();
//        calendar.set(2020,11,28,10,00,00);
//        Alarm.setAlarm(calendar, this);
    }
}