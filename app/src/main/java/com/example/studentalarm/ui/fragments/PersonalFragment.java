package com.example.studentalarm.ui.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;

import com.example.studentalarm.alarm.AlarmManager;
import com.example.studentalarm.save.PreferenceKeys;
import com.example.studentalarm.R;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

public class PersonalFragment extends Fragment {

    private static final String LOG = "PersonalFragment";

    public PersonalFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (getActivity() != null) {
            RegularLectureFragment.removeRegularLectureMenu(getActivity());
            LectureFragment.removeLectureMenu(getActivity());
        }
        Log.i(LOG, "open");
        openFragment(new AlarmSettingFragment(this));
        return inflater.inflate(R.layout.fragment_personal, container, false);
    }

    /**
     * open a fragment
     *
     * @param fragment the fragment to open
     */
    public void openFragment(@NonNull Fragment fragment) {
        if (getActivity() != null) {
            Log.i(LOG, "open Fragment: " + fragment.getClass().toString());
            getActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frLPersonal, fragment, "TAG")
                    .addToBackStack(null)
                    .commit();
        }
    }
}