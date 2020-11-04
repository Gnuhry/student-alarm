package com.example.studentalarm;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alamkanak.weekview.WeekView;

import java.text.SimpleDateFormat;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LectureFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LectureFragment extends Fragment {
    WeekView.SimpleAdapter<Lecture_Schedule.Lecture> adapter;
    SimpleDateFormat format = new SimpleDateFormat("EEE dd.MM");

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public LectureFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LectureFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LectureFragment newInstance(String param1, String param2) {
        LectureFragment fragment = new LectureFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_lecture, container, false);
        // Inflate the layout for this fragment
        WeekView weekview = view.findViewById(R.id.weekView);
        adapter = new WeekView.SimpleAdapter<>();
        weekview.setAdapter(adapter);
        weekview.setTimeFormatter(hour -> {
            if (hour < 10)
                return "0" + hour + " h";
            return "" + hour + " h";
        });
        weekview.setDateFormatter(date -> format.format(date.getTime()));
        adapter.submit(Lecture_Schedule.Load(this.getContext()).getAllLecture());
        Thread x = new Thread(() -> {
            Lecture_Schedule l = Import.ICSImport("http://vorlesungsplan.dhbw-mannheim.de/ical.php?uid=7758001", this.getContext());
            adapter.submit(l.getAllLecture());
        });
        x.start();
        return view;
    }
}