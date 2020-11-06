package com.example.studentalarm;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity {

    private Fragment oldFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);

        openFragment(new AlarmFragment());


        bottomNav.setOnNavigationItemSelectedListener(item -> {

            if (oldFragment instanceof SettingsFragment && ((SettingsFragment) oldFragment).IsLinkIncorrect()) {
                new MaterialAlertDialogBuilder(this)
                        .setTitle(getString(R.string.discard_changes))
                        .setMessage(getString(R.string.do_you_want_to_discard_the_link_changes))
                        .setPositiveButton(getString(R.string.discard), (dialogInterface, i) -> {
                            String value = ((SettingsFragment) oldFragment).getOldLink();
                            SharedPreferences.Editor editor = getSharedPreferences("SETTINGS", Context.MODE_PRIVATE).edit();
                            editor.putString("Link", value).apply();
                            if (value == null) {
                                editor.putInt("Mode", ((SettingsFragment) oldFragment).getOldMode()).apply();
                                editor.putBoolean("Import_Auto", ((SettingsFragment) oldFragment).isOldAutoImport()).apply();
                            }
                            changeFragment(item);
                            bottomNav.setSelectedItemId(item.getItemId());
                        })
                        .setNegativeButton(getString(R.string.cancel), null)
                        .setCancelable(true)
                        .show();
            } else return changeFragment(item);
            return false;
        });

    }

    private boolean changeFragment(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.alarm) {
            openFragment(new AlarmFragment());
            return true;
        } else if (itemId == R.id.lecture) {
            openFragment(new LectureFragment());
            return true;
        } else if (itemId == R.id.school) {
            openFragment(new SchoolFragment());
            return true;
        } else if (itemId == R.id.setting) {
            openFragment(new SettingsFragment());
            return true;
        }
        return false;
    }

    void openFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        oldFragment = fragment;
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }
}