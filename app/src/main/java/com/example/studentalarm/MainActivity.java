package com.example.studentalarm;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;

import com.example.studentalarm.receiver.NetworkReceiver;
import com.example.studentalarm.save.PreferenceKeys;
import com.example.studentalarm.ui.dialog.ImportDialog;
import com.example.studentalarm.ui.fragments.SettingsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("Main","started");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.registerReceiver(new NetworkReceiver(), new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
        this.registerReceiver(new NetworkReceiver(), new IntentFilter("android.net.wifi.WIFI_STATE_CHANGED"));

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_);
        if (navHostFragment != null)
            NavigationUI.setupWithNavController((BottomNavigationView) findViewById(R.id.bottomNav), navHostFragment.getNavController());
        PreferenceKeys.setDefault(this);

        String lan = PreferenceManager.getDefaultSharedPreferences(this).getString(PreferenceKeys.LANGUAGE, null);
        if (lan != null && !lan.equals(PreferenceKeys.defaultLanguage(this)))
            new SettingsFragment().changeLanguage(lan, this, this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == ImportDialog.REQUEST_CODE && resultCode == Activity.RESULT_OK)
            ImportDialog.setResultIntent(data, this);

        super.onActivityResult(requestCode, resultCode, data);
    }
}
