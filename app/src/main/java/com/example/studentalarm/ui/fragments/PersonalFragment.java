package com.example.studentalarm.ui.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.studentalarm.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class PersonalFragment extends Fragment {

    private static final String LOG = "PersonalFragment";
    private RegularLectureFragment fragment;

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
            if (fragment != this.fragment)
                checkSave();
            getActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frLPersonal, fragment, "TAG")
                    .addToBackStack(null)
                    .commit();
        }
    }

    /**
     * get the regularFragment
     *
     * @return regular fragment
     */
    public RegularLectureFragment getRegularFragment() {
        fragment = new RegularLectureFragment();
        return fragment;
    }

    /**
     * check if save dialog should displayed
     */
    private void checkSave() {
        if (fragment != null && !fragment.hasNoChanges()) {
            new MaterialAlertDialogBuilder(getContext())
                    .setTitle(R.string.dismiss)
                    .setMessage(R.string.do_you_want_to_save_your_changes)
                    .setPositiveButton(R.string.save, (dialogInterface, i) -> {
                        fragment.save();
                        fragment = null;
                    })
                    .setNegativeButton(R.string.dismiss, (dialogInterface, i) -> fragment = null)
                    .setCancelable(false)
                    .show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        checkSave();
    }
}