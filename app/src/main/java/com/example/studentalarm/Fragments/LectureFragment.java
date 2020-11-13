package com.example.studentalarm.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.studentalarm.R;
import com.google.android.material.tabs.TabLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;


public class LectureFragment extends Fragment {

    private final static String TAG = "LECTURE_FRAGMENT";

    public LectureFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lecture, container, false);
        TabLayout layout=(TabLayout) view.findViewById(R.id.tLLecture);
        layout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        openFragment(new WeeklyFragment());
                        break;
                    case 1:
                        openFragment(new MonthlyFragment());
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        openFragment(new WeeklyFragment());
        return view;
    }

    /**
     * open a fragment
     *
     * @param fragment the fragment to open
     */
    public void openFragment(Fragment fragment) {
        if (getActivity() == null) return;
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fLLecture, fragment, TAG);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}