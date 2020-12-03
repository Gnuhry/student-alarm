package com.example.studentalarm.ui.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEntity;
import com.example.studentalarm.R;
import com.example.studentalarm.RegularLectureSchedule;
import com.example.studentalarm.save.SaveRegularLectureSchedule;
import com.example.studentalarm.ui.adapter.RegularLectureAdapter;

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
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class RegularLectureFragment extends Fragment {
    private RegularLectureAdapter regularLectureAdapter;
    private Adapter adapter;
    private final RegularLectureSchedule regularLectureSchedule;
    private WeekView weekView;
    private final List<RegularLectureSchedule.RegularLecture.RegularLectureTime> lectures;
    private RecyclerView rv;
    private static final String LOG = "RegularLectureFragment";

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

        initAppBar();
        weekView = view.findViewById(R.id.regularWeekView);
        rv = view.findViewById(R.id.rVRegularLecture);
        load();
        initWeekView();
        loadRecyclerView();
        loadDataWeekView();

        return view;
    }

    /**
     * fragment has no changes
     *
     * @return {true} if no changes {false} if changes occurs
     */
    public boolean hasNoChanges() {
        //TODO return false if changes true if not
        return false;
    }

    /**
     * init the appbar
     */
    private void initAppBar() {
        Log.i(LOG, "initAppBar");
        if (getActivity() == null) return;
        Toolbar toolbar = getActivity().findViewById(R.id.my_toolbar);
        toolbar.getMenu().getItem(2).setVisible(true);
        toolbar.getMenu().getItem(2).setOnMenuItemClickListener(menuItem -> {
            save();
            return true;
        });
        toolbar.getMenu().getItem(3).setVisible(true);
        toolbar.getMenu().getItem(3).setOnMenuItemClickListener(menuItem -> {
            //TODO settings regular lecture settings
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

    /**
     * add a lecture to the timetable
     *
     * @param day     day to add
     * @param hour    hour to add
     * @param lecture lecture to add
     */
    private void addTime(int day, int hour, @NonNull RegularLectureSchedule.RegularLecture lecture) {
        if (day >= regularLectureSchedule.getDays() || hour >= regularLectureSchedule.getHours())
            return;
        removeTime(day, hour);
        lectures.add(new RegularLectureSchedule.RegularLecture.RegularLectureTime(day, hour, lecture.getActiveRoomId(), lecture));
    }

    /**
     * remove a lecture from the timetable
     *
     * @param day  day to remove
     * @param hour hour to remove
     */
    private void removeTime(int day, int hour) {
        if (day >= regularLectureSchedule.getDays() || hour >= regularLectureSchedule.getHours())
            return;
        for (Iterator<RegularLectureSchedule.RegularLecture.RegularLectureTime> iterator = lectures.iterator(); iterator.hasNext(); ) {
            RegularLectureSchedule.RegularLecture.RegularLectureTime time = iterator.next();
            if (time.day == day && time.hour == hour)
                iterator.remove();
        }
    }

    /**
     * init the weekView
     */
    private void initWeekView() {
        Log.i(LOG, "init weekView");
        Calendar calendar = Calendar.getInstance();
        calendar.set(2020, 5, 1, 0, 0, 0);
        weekView.scrollToDate(calendar);
        adapter = new Adapter();
        weekView.setAdapter(adapter);

        SimpleDateFormat format = new SimpleDateFormat("E", getResources().getConfiguration().locale);
        weekView.setTimeFormatter(hour -> hour + 1 + getString(R.string.hour));
        weekView.setMinHour(0);
        weekView.setMaxHour(regularLectureSchedule.getHours());
        weekView.setDateFormatter(date -> format.format(date.getTime()));
        weekView.setNumberOfVisibleDays(regularLectureSchedule.getDays() - 1);
    }

    /**
     * loading the recycler view elements
     */
    public void loadRecyclerView() {
        Log.i(LOG, "load recyclerView");
        regularLectureAdapter = new RegularLectureAdapter(regularLectureSchedule, this);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rv.setAdapter(regularLectureAdapter);
        loadDataWeekView();
    }

    /**
     * loading the week view elements
     */
    private void loadDataWeekView() {
        Log.i(LOG, "load weekView");
        Log.d("erg", lectures.size() + "");
        for (Iterator<RegularLectureSchedule.RegularLecture.RegularLectureTime> iterator = lectures.iterator(); iterator.hasNext(); )
            if (!regularLectureSchedule.getLectures().contains(iterator.next().lecture))
                iterator.remove();
        adapter.submitList(lectures);
    }

    //----------------------------------Save---------------------------------

    /**
     * clear data from file
     *
     * @param context context of app
     */
    public static void clearSave(@NonNull Context context) {
        Log.i(LOG, "clear save");
        saving(null, context);
    }

    /**
     * save data to file
     */
    public void save() {
        Log.i(LOG, "save");
        if (getContext() != null) {
            saving(createSave(), getContext());
            Toast.makeText(getContext(), R.string.save, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * saving the date to the file
     *
     * @param schedule data to save
     * @param context  context of app
     */
    private static void saving(@Nullable SaveRegularLectureSchedule schedule, @NonNull Context context) {
        FileOutputStream fos;
        try {
            fos = context.openFileOutput("REGULAR_LECTURE", Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(schedule);
            oos.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * create the save object
     *
     * @return save object
     */
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
        }
        erg.times = times;
        return erg;
    }

    /**
     * load data from file
     */
    private void load() {
        Log.i(LOG, "load");
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

    /**
     * convert the save object to the normal object
     *
     * @param readObject save object to convert
     */
    private void ConvertSave(@NonNull SaveRegularLectureSchedule readObject) {
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
                        lectures.add(new RegularLectureSchedule.RegularLecture.RegularLectureTime(time.day, time.hour, time.room_id < 0 ? (l.getRooms().size() > 0 ? l.getActiveRoomId() : -1) : time.room_id, l));
    }

    class Adapter extends WeekView.SimpleAdapter<RegularLectureSchedule.RegularLecture.RegularLectureTime> {

        public int counter = 0;

        @Override
        public void onEventClick(@NonNull RegularLectureSchedule.RegularLecture.RegularLectureTime data) {
            super.onEventClick(data);
            Log.i(LOG, "adapter-eventClick");
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
                erg.setSubtitle(item.lecture.getRooms().get(item.room_id));
            erg.setStyle(new WeekViewEntity.Style.Builder().setBackgroundColor(item.lecture.getColor()).build());
            erg.setId(counter++);
            return erg.build();
        }

        @Override
        public void onEmptyViewClick(@NonNull Calendar time) {
            super.onEmptyViewClick(time);
            Log.i(LOG, "adapter-emptyClick");
            RegularLectureSchedule.RegularLecture selected = regularLectureAdapter.getSelected();
            if (selected == null) return;
            addTime(time.get(Calendar.DAY_OF_MONTH), time.get(Calendar.HOUR), selected);
            loadDataWeekView();
        }
    }

}