package com.example.studentalarm.ui.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEntity;
import com.example.studentalarm.R;
import com.example.studentalarm.RegularLectureSchedule;
import com.example.studentalarm.save.SaveRegularLectureSchedule;
import com.example.studentalarm.ui.adapter.RegularLectureAdapter;

import org.jetbrains.annotations.NotNull;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class RegularLectureFragment extends Fragment {
    RegularLectureAdapter regularLectureAdapter;
    Adapter adapter;
    RegularLectureSchedule regularLectureSchedule;
    WeekView weekView;
    private final List<RegularLectureSchedule.RegularLecture.RegularLectureTime> lectures;

    public RegularLectureFragment() {
        regularLectureSchedule = new RegularLectureSchedule(4, 10);
        lectures = new ArrayList<>();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_regular_lecture, container, false);
        if (getContext() == null || getActivity() == null) return view;

        InitAppBar();

//        save();

//        initTest();

        weekView = view.findViewById(R.id.regularWeekView);
        load();
        initWeekView();
        loadRecyclerView(view);
        loadDataWeekView();

        return view;
    }

    private void InitAppBar() {
        if(getActivity()==null) return;
        Toolbar toolbar = getActivity().findViewById(R.id.my_toolbar);
        toolbar.getMenu().getItem(2).setVisible(true);
        toolbar.getMenu().getItem(2).setOnMenuItemClickListener(menuItem -> {
            save();
            return true;
        });
        toolbar.getMenu().getItem(3).setVisible(true);
        toolbar.getMenu().getItem(3).setOnMenuItemClickListener(menuItem -> {
            //TODO settings regular lecture schedule
            return false;
        });
    }


    /**
     * Remove menu item
     */
    public static void removeRegularLectureMenu(@NonNull Activity activity) {
        Toolbar toolbar = activity.findViewById(R.id.my_toolbar);
        if (toolbar != null) {
            toolbar.getMenu().getItem(2).setVisible(false);
            toolbar.getMenu().getItem(3).setVisible(false);
        }
    }

    private void addTime(int day, int hour, @NonNull RegularLectureSchedule.RegularLecture lecture) {
        if (day >= regularLectureSchedule.getDays() || hour >= regularLectureSchedule.getHours())
            return;
        removeTime(day, hour);
        lectures.add(new RegularLectureSchedule.RegularLecture.RegularLectureTime(day, hour, lecture.getActiveRoomId(), lecture));
    }

    private void removeTime(int day, int hour) {
        if (day >= regularLectureSchedule.getDays() || hour >= regularLectureSchedule.getHours())
            return;
        for (Iterator<RegularLectureSchedule.RegularLecture.RegularLectureTime> iterator = lectures.iterator(); iterator.hasNext(); ) {
            RegularLectureSchedule.RegularLecture.RegularLectureTime time = iterator.next();
            if (time.day == day && time.hour == hour)
                iterator.remove();
        }
    }

    private void initTest() {
        regularLectureSchedule.addLecture(new RegularLectureSchedule.RegularLecture("Test").setDocent("Mr. aoshgoa").addRoom("A123"));
        regularLectureSchedule.addLecture(new RegularLectureSchedule.RegularLecture("Test2").setDocent("Mr. 123").addRoom("HUHN"));
        regularLectureSchedule.addLecture(new RegularLectureSchedule.RegularLecture("Test3").setDocent("Mr. 23").addRoom("A123"));
    }

    private void initWeekView() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2020, 5, 1, 0, 0, 0);
        weekView.scrollToDate(calendar);
        adapter = new Adapter();
        weekView.setAdapter(adapter);

        SimpleDateFormat format = new SimpleDateFormat("E", getResources().getConfiguration().locale);
        weekView.setTimeFormatter(hour -> hour + 1 + " Stunde");
        weekView.setMinHour(0);
        weekView.setMaxHour(regularLectureSchedule.getHours());
        weekView.setDateFormatter(date -> format.format(date.getTime()));
        weekView.setNumberOfVisibleDays(regularLectureSchedule.getDays() - 1);
    }

    private void loadRecyclerView(View view) {
        RecyclerView rv = view.findViewById(R.id.rVRegularLecture);
        regularLectureAdapter = new RegularLectureAdapter(regularLectureSchedule.getLectures());
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rv.setAdapter(regularLectureAdapter);
    }

    private void loadDataWeekView() {
        Log.d("erg", lectures.size() + "");
        adapter.submitList(lectures);
    }

    //----------------------------------Save---------------------------------
    private void save() {
        if (getContext() == null) return;
        FileOutputStream fos;
        try {
            fos = getContext().openFileOutput("REGULAR_LECTURE", Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(createSave());
            oos.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private SaveRegularLectureSchedule createSave() {
        SaveRegularLectureSchedule erg = new SaveRegularLectureSchedule();
        erg.day = regularLectureSchedule.getDays();
        erg.hour = regularLectureSchedule.getHours();
        SaveRegularLectureSchedule.SaveRegularLecture[] saveRegularLecture = new SaveRegularLectureSchedule.SaveRegularLecture[regularLectureSchedule.getLectures().size()];
        for (int f = 0; f < saveRegularLecture.length; f++) {
            saveRegularLecture[f] = new SaveRegularLectureSchedule.SaveRegularLecture();
            RegularLectureSchedule.RegularLecture l = regularLectureSchedule.getLectures().get(f);
            saveRegularLecture[f].id = l.getId();
            saveRegularLecture[f].activeRoomId = l.getActiveRoomId();
            saveRegularLecture[f].color = l.getColor();
            saveRegularLecture[f].docent = l.getDocent();
            saveRegularLecture[f].name = l.getName();
            saveRegularLecture[f].rooms = l.getRooms().toArray(new String[0]);
            Log.d("Check_ID_Save", l.getId() + "");
        }
        erg.schedule = saveRegularLecture;
        SaveRegularLectureSchedule.SaveTime[] times = new SaveRegularLectureSchedule.SaveTime[this.lectures.size()];
        for (int f = 0; f < times.length; f++) {
            times[f] = new SaveRegularLectureSchedule.SaveTime();
            RegularLectureSchedule.RegularLecture.RegularLectureTime t = lectures.get(f);
            times[f].day = t.day;
            times[f].hour = t.hour;
            times[f].room_id = t.room_id;

            times[f].saveRegularLecture = new SaveRegularLectureSchedule.SaveRegularLecture();
            times[f].saveRegularLecture.id = t.lecture.getId();
//            times[f].saveRegularLecture.activeRoomId = t.lecture.getActiveRoomId();
//            times[f].saveRegularLecture.color = t.lecture.getColor();
//            times[f].saveRegularLecture.docent = t.lecture.getDocent();
//            times[f].saveRegularLecture.name = t.lecture.getName();
//            times[f].saveRegularLecture.rooms = t.lecture.getRooms().toArray(new String[0]);
        }
        erg.times = times;
        return erg;
    }

    private void load() {
        if (getContext() == null) return;
        try {
            FileInputStream fis = getContext().openFileInput("REGULAR_LECTURE");
            ObjectInputStream ois = new ObjectInputStream(fis);
            ConvertSave((SaveRegularLectureSchedule) ois.readObject());
            fis.close();
            ois.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void ConvertSave(SaveRegularLectureSchedule readObject) {
        this.regularLectureSchedule.setDays(readObject.day);
        this.regularLectureSchedule.setHours(readObject.hour);
        int id = 0;
        if (readObject.schedule != null)
            for (SaveRegularLectureSchedule.SaveRegularLecture lecture : readObject.schedule) {
                RegularLectureSchedule.RegularLecture help = new RegularLectureSchedule.RegularLecture(lecture.name, lecture.id)
                        .setActiveRoomId(lecture.activeRoomId)
                        .setColor(lecture.color)
                        .setDocent(lecture.docent);
                if (lecture.id > id)
                    id = lecture.id;
                Log.d("Check_ID", lecture.id + "");
                for (String room : lecture.rooms)
                    help.addRoom(room);
                regularLectureSchedule.addLecture(help);
            }
        RegularLectureSchedule.RegularLecture.setCounter(id);
        List<RegularLectureSchedule.RegularLecture> help = regularLectureSchedule.getLectures();
        if (readObject.times != null)
            for (SaveRegularLectureSchedule.SaveTime time : readObject.times)
                for (RegularLectureSchedule.RegularLecture l : help)
                    if (l.getId() == time.saveRegularLecture.id)
                        lectures.add(new RegularLectureSchedule.RegularLecture.RegularLectureTime(time.day, time.hour, time.room_id, l));
    }

    class Adapter extends WeekView.SimpleAdapter<RegularLectureSchedule.RegularLecture.RegularLectureTime> {

        public int counter = 0;

        @Override
        public void onEventClick(@NonNull RegularLectureSchedule.RegularLecture.RegularLectureTime data) {
            super.onEventClick(data);
            RegularLectureSchedule.RegularLecture selected = regularLectureAdapter.getSelected();
            if (selected == null) return;
            if (selected.equals(data.lecture))
                removeTime(data.day, data.hour);
            else
                addTime(data.day, data.hour, selected);
            loadDataWeekView();
        }

        @NonNull
        @Override
        public WeekViewEntity onCreateEntity(@NonNull RegularLectureSchedule.RegularLecture.RegularLectureTime item) {
            WeekViewEntity.Event.Builder<RegularLectureSchedule.RegularLecture.RegularLectureTime> erg = new WeekViewEntity.Event.Builder<>(item);
            StringBuilder sb = new StringBuilder(item.lecture.getName());
            if (item.lecture.getDocent() != null)
                sb.append(" - ").append(item.lecture.getDocent());
            erg.setTitle(sb.toString());
            Calendar calendar = Calendar.getInstance(), calendar1 = Calendar.getInstance();
            calendar.set(2020, 5, item.day, item.hour, 0, 0);
            erg.setStartTime(calendar);
            calendar1.set(2020, 5, item.day, item.hour, 59, 59);
            erg.setEndTime(calendar1);
            if (item.lecture.getActiveRoom() != null)
                erg.setSubtitle(item.lecture.getActiveRoom());
            erg.setStyle(new WeekViewEntity.Style.Builder().setBackgroundColor(item.lecture.getColor()).build());
            erg.setId(counter++);
            return erg.build();
        }

        @Override
        public void onEmptyViewClick(@NotNull Calendar time) {
            super.onEmptyViewClick(time);
            RegularLectureSchedule.RegularLecture selected = regularLectureAdapter.getSelected();
            if (selected == null) return;
            addTime(time.get(Calendar.DAY_OF_MONTH), time.get(Calendar.HOUR), selected);
            loadDataWeekView();
        }
    }

}