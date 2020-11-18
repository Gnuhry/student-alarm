package com.example.studentalarm;

import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;

import com.example.studentalarm.Fragments.SettingsFragment;
import com.example.studentalarm.Receiver.NetworkReceiver;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
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
    }
}