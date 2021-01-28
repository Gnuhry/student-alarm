package com.example.studentalarm.ui.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
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
    private static final String TAG = "LECTURE_FRAGMENT";
    private static final String LOG = "LectureFragment";

    private static Thread animate;
    private static boolean animateBool = true;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(LOG, "open");
        View view = inflater.inflate(R.layout.fragment_lecture, container, false);
        if (getActivity() == null) return view;

        RegularLectureFragment.removeRegularLectureMenu(getActivity());
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
        if (getActivity() != null) {
            Log.i(LOG, "open Fragment: " + fragment.getClass().toString());
            getActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fLLecture, fragment, TAG)
                    .addToBackStack(null)
                    .commit();
        }
    }

    /**
     * Remove menu item
     */
    public static void removeLectureMenu(@NonNull Activity activity) {
        Toolbar toolbar = activity.findViewById(R.id.my_toolbar);
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
    public static void animateReload(@NonNull Activity activity) {
        Log.d(LOG, "Start animate reload");
        activity.findViewById(R.id.my_toolbar).post(() -> ((Toolbar) activity.findViewById(R.id.my_toolbar)).getMenu().getItem(1).setEnabled(false));
        animateBool = true;
        MenuItem item = ((Toolbar) activity.findViewById(R.id.my_toolbar)).getMenu().getItem(1);
        animate = new Thread(() -> {
            setAnimation(activity, item, R.drawable.hourglass_bottom);
            activity.findViewById(R.id.my_toolbar).post(() -> item.setIcon(R.drawable.replay));
        });
        animate.start();
    }

    /**
     * Stop the reload animation
     *
     * @param activity activity of app
     */
    public static void stopAnimateReload(@NonNull Activity activity) {
        Log.d(LOG, "stop animation");
        activity.findViewById(R.id.my_toolbar).post(() -> ((Toolbar) activity.findViewById(R.id.my_toolbar)).getMenu().getItem(1).setEnabled(true));
        animateBool = false;
        if (animate != null) {
            try {
                animate.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Playing the animation
     *
     * @param activity activity of app
     * @param item     item to display animation
     * @param res      res to display
     */
    private static void setAnimation(@NonNull Activity activity, @NonNull MenuItem item, int res) {
        if (activity == null || !(res == R.drawable.hourglass_bottom || res == R.drawable.hourglass_top))
            return;
        activity.findViewById(R.id.my_toolbar).post(() -> item.setIcon(res));
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (animateBool)
            setAnimation(activity, item, res == R.drawable.hourglass_bottom ? R.drawable.hourglass_top : R.drawable.hourglass_bottom);
    }

}