package com.example.studentalarm.regular;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.studentalarm.R;
import com.example.studentalarm.save.SaveKeys;
import com.example.studentalarm.save.SaveRegularLectureSchedule;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RegularLectureSchedule {

    private static final String LOG = "RegularLectureSchedule";

    @NonNull
    private final List<RegularLecture> lectures;
    @NonNull
    private final List<RegularLectureSchedule.RegularLecture.RegularLectureTime> regularLectures;
    private int days, hours;

    public RegularLectureSchedule() {
        lectures = new ArrayList<>();
        regularLectures = new ArrayList<>();
        days = 5;
        hours = 6;
    }

    public int getDays() {
        return days;
    }

    public int getHours() {
        return hours;
    }

    @NonNull
    public List<RegularLecture> getLectures() {
        return lectures;
    }

    @NonNull
    public List<RegularLecture.RegularLectureTime> getRegularLectures() {
        return regularLectures;
    }

    public void setDays(int days) {
        this.days = days;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    public void addLecture(RegularLecture lecture) {
        lectures.add(lecture);
    }

    /**
     * add a lecture to the timetable
     *
     * @param day     day to add
     * @param hour    hour to add
     * @param lecture lecture to add
     */
    public void addTime(int day, int hour, @NonNull RegularLectureSchedule.RegularLecture lecture) {
        if (day > this.days || hour >= this.hours)
            return;
        removeTime(day, hour);
        regularLectures.add(new RegularLectureSchedule.RegularLecture.RegularLectureTime(day, hour, lecture.getActiveRoom(), lecture));
    }

    /**
     * remove a lecture from the timetable
     *
     * @param day  day to remove
     * @param hour hour to remove
     */
    public void removeTime(int day, int hour) {
        if (day > this.days || hour >= this.hours)
            return;
        for (Iterator<RegularLecture.RegularLectureTime> iterator = regularLectures.iterator(); iterator.hasNext(); ) {
            RegularLectureSchedule.RegularLecture.RegularLectureTime time = iterator.next();
            if (time.day == day && time.hour == hour)
                iterator.remove();
        }
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
    public void save(@NonNull Context context) {
        Log.i(LOG, "save");
        saving(createSave(), context);
        Toast.makeText(context, R.string.save, Toast.LENGTH_SHORT).show();
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
            fos = context.openFileOutput(SaveKeys.REGULAR_LECTURE, Context.MODE_PRIVATE);
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
    @NonNull
    private SaveRegularLectureSchedule createSave() {
        SaveRegularLectureSchedule erg = new SaveRegularLectureSchedule();
        erg.day = this.days;
        erg.hour = this.hours;
        SaveRegularLectureSchedule.SaveRegularLecture[] saveRegularLecture = new SaveRegularLectureSchedule.SaveRegularLecture[lectures.size()];
        for (int f = 0; f < saveRegularLecture.length; f++) {
            saveRegularLecture[f] = new SaveRegularLectureSchedule.SaveRegularLecture();
            RegularLectureSchedule.RegularLecture l = lectures.get(f);
            saveRegularLecture[f].id = l.getId();
            saveRegularLecture[f].activeRoomId = l.getActiveRoomId();
            saveRegularLecture[f].color = l.getColor();
            saveRegularLecture[f].docent = l.getDocent();
            saveRegularLecture[f].name = l.getName();
            saveRegularLecture[f].rooms = l.getRooms().toArray(new String[0]);
        }
        erg.schedule = saveRegularLecture;
        SaveRegularLectureSchedule.SaveTime[] times = new SaveRegularLectureSchedule.SaveTime[regularLectures.size()];
        for (int f = 0; f < times.length; f++) {
            times[f] = new SaveRegularLectureSchedule.SaveTime();
            RegularLectureSchedule.RegularLecture.RegularLectureTime t = regularLectures.get(f);
            times[f].day = t.day;
            times[f].hour = t.hour;
            times[f].room = t.room;

            times[f].saveRegularLecture = new SaveRegularLectureSchedule.SaveRegularLecture();
            times[f].saveRegularLecture.id = t.lecture.getId();
        }
        erg.times = times;
        return erg;
    }

    /**
     * load data from file
     */
    @NonNull
    public static RegularLectureSchedule load(@Nullable Context context) {
        Log.i(LOG, "load");
        if (context == null) return new RegularLectureSchedule();
        try {
            FileInputStream fis = context.openFileInput(SaveKeys.REGULAR_LECTURE);
            ObjectInputStream ois = new ObjectInputStream(fis);
            RegularLectureSchedule help = convertSave((SaveRegularLectureSchedule) ois.readObject());
            fis.close();
            ois.close();
            return help;
        } catch (IOException | ClassNotFoundException e) {
            Log.d("RegularLectureSchedule", "can't load");
        }
        return new RegularLectureSchedule();
    }

    /**
     * convert the save object to the normal object
     *
     * @param readObject save object to convert
     */
    @NonNull
    private static RegularLectureSchedule convertSave(@NonNull SaveRegularLectureSchedule readObject) {
        RegularLectureSchedule schedule = new RegularLectureSchedule();
        if (readObject == null)
            return schedule;
        schedule.days = readObject.day;
        schedule.hours = readObject.hour;
        int id = 0;
        if (readObject.schedule != null)
            for (SaveRegularLectureSchedule.SaveRegularLecture lecture : readObject.schedule) {
                RegularLectureSchedule.RegularLecture help = new RegularLectureSchedule.RegularLecture(lecture.name, lecture.id)
                        .setActiveRoomId(lecture.activeRoomId)
                        .setColor(lecture.color)
                        .setDocent(lecture.docent);
                if (lecture.id > id)
                    id = lecture.id;
                for (String room : lecture.rooms)
                    help.addRoom(room);
                schedule.addLecture(help);
            }
        RegularLectureSchedule.RegularLecture.setCounter(id);
        List<RegularLectureSchedule.RegularLecture> help = schedule.getLectures();
        if (readObject.times != null)
            for (SaveRegularLectureSchedule.SaveTime time : readObject.times)
                for (RegularLectureSchedule.RegularLecture l : help)
                    if (l.getId() == time.saveRegularLecture.id)
                        schedule.regularLectures.add(new RegularLectureSchedule.RegularLecture.RegularLectureTime(time.day, time.hour, time.room, l));
        return schedule;
    }


    public static class RegularLecture {
        private static int counter = 0;
        private final int id;
        private String name, docent;
        @NonNull
        private List<String> rooms;
        private int color = Color.RED, activeRoomId;

        public RegularLecture(String name) {
            id = counter++;
            rooms = new ArrayList<>();
            activeRoomId = -1;
            this.name = name;
        }

        public RegularLecture(String name, int id) {
            this.id = id;
            rooms = new ArrayList<>();
            activeRoomId = -1;
            this.name = name;
        }

        @NonNull
        public List<String> getRooms() {
            return rooms;
        }

        public String getName() {
            return name;
        }

        public String getDocent() {
            return docent;
        }

        public int getId() {
            return id;
        }

        public int getColor() {
            return color;
        }

        public int getActiveRoomId() {
            return activeRoomId;
        }

        @Nullable
        public String getActiveRoom() {
            if (activeRoomId == -1 || activeRoomId >= rooms.size())
                return null;
            return rooms.get(activeRoomId);
        }

        @NonNull
        public RegularLecture setAllRooms(@NonNull List<String> rooms2) {
            this.rooms = rooms2;
            if (rooms.size() > 0)
                activeRoomId = 0;
            return this;
        }

        @NonNull
        public RegularLecture setName(String name) {
            this.name = name;
            return this;
        }

        @NonNull
        public RegularLecture setDocent(String docent) {
            this.docent = docent;
            return this;
        }

        public static void setCounter(int new_counter) {
            counter = new_counter;
        }

        @NonNull
        public RegularLecture setColor(int color) {
            this.color = color;
            return this;
        }

        @NonNull
        public RegularLecture setActiveRoomId(int activeRoomId) {
            this.activeRoomId = activeRoomId;
            return this;
        }

        public void addRoom(String room) {
            if (activeRoomId == -1) activeRoomId = 0;
            rooms.add(room);
        }


        public static class RegularLectureTime {
            public final int day, hour;
            private final String room;
            public final RegularLecture lecture;

            public RegularLectureTime(int day, int hour, String room, RegularLecture lecture) {
                this.day = day;
                this.hour = hour;
                this.room = room;
                this.lecture = lecture;
            }

            @Nullable
            public String getActiveRoom() {
                if (lecture.rooms.contains(room))
                    return room;
                return lecture.getActiveRoom();
            }

            public int getCalendarDay() {
                return day == 7 ? 1 : day + 1;
            }
        }
    }

}
