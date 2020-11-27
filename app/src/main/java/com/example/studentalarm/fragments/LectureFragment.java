package com.example.studentalarm.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.studentalarm.R;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;


public class LectureFragment extends Fragment {

    @NonNull
    private final static String TAG = "LECTURE_FRAGMENT";

    private static Thread animate;
    private static boolean animate_bool = true;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_lecture, container, false);
        if (getActivity() == null) return view;

        Toolbar toolbar = getActivity().findViewById(R.id.my_toolbar);
        toolbar.getMenu().getItem(0).setVisible(true);
        toolbar.getMenu().getItem(1).setVisible(true);

        ((TabLayout) view.findViewById(R.id.tLLecture)).addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(@NonNull TabLayout.Tab tab) {
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
    public void openFragment(@NonNull Fragment fragment) {
        if (getActivity() != null)
            getActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fLLecture, fragment, TAG)
                    .addToBackStack(null)
                    .commit();
    }

    /**
     * Remove menu item if fragment changed
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (getActivity() == null) return;
        Toolbar toolbar = this.getActivity().findViewById(R.id.my_toolbar);
        if (toolbar != null) {
            toolbar.getMenu().getItem(0).setVisible(false);
            toolbar.getMenu().getItem(1).setVisible(false);
        }
    }


    /**
     * Start the reload animation
     *
     * @param activity activity of app
     */
    public static void AnimateReload(@NonNull Activity activity) {
        activity.findViewById(R.id.my_toolbar).post(() -> ((Toolbar) activity.findViewById(R.id.my_toolbar)).getMenu().getItem(1).setEnabled(false));
        animate_bool = true;
        MenuItem item = ((Toolbar) activity.findViewById(R.id.my_toolbar)).getMenu().getItem(1);
        animate = new Thread(() -> {
            setAnimation(activity, item, R.drawable.hourglass_bottom);
            activity.findViewById(R.id.my_toolbar).post(() -> item.setIcon(R.drawable.replay));
        });
        animate.start();
    }

    private static void setAnimation(Activity activity, MenuItem item, int res) {
        activity.findViewById(R.id.my_toolbar).post(() -> item.setIcon(res));
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (animate_bool)
            setAnimation(activity, item, res == R.drawable.hourglass_bottom ? R.drawable.hourglass_top: R.drawable.hourglass_bottom);
    }

    /**
     * Stop the reload animation
     *
     * @param activity activity of app
     */
    public static void StopAnimateReload(@NonNull Activity activity) {
        activity.findViewById(R.id.my_toolbar).post(() -> ((Toolbar) activity.findViewById(R.id.my_toolbar)).getMenu().getItem(1).setEnabled(true));
        animate_bool = false;
        if (animate != null) {
            try {
                animate.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}