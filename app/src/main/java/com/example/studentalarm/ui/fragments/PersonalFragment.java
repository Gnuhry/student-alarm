package com.example.studentalarm.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.studentalarm.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class PersonalFragment extends Fragment {

    private static final String LOG = "PersonalFragment";
    @Nullable
    private RegularLectureFragment fragment;

    public PersonalFragment() {
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (getContext() != null)
            checkSave(getContext());
    }

    /**
     * open a fragment
     *
     * @param fragment the fragment to open
     */
    public void openFragment(@NonNull Fragment fragment) {
        if (getActivity() != null) {
            Log.i(LOG, "open Fragment: " + fragment.getClass().toString());
            if (fragment != this.fragment && getContext() != null)
                checkSave(getContext());
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
    @NonNull
    public RegularLectureFragment getRegularFragment() {
        fragment = new RegularLectureFragment(this);
        return fragment;
    }

    /**
     * check if save dialog should displayed
     */
    private void checkSave(@NonNull Context context) {
        if (fragment != null && fragment.hasChanges() && getContext() != null) {
            new MaterialAlertDialogBuilder(getContext())
                    .setTitle(R.string.dismiss)
                    .setMessage(R.string.do_you_want_to_save_your_changes)
                    .setPositiveButton(R.string.save, (dialogInterface, i) -> {
                        fragment.getRegularLectureSchedule().save(context);
                        fragment = null;
                    })
                    .setNegativeButton(R.string.dismiss, (dialogInterface, i) -> fragment = null)
                    .setCancelable(false)
                    .show();
        }
    }
}