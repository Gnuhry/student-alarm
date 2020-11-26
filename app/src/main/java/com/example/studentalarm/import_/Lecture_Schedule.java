package com.example.studentalarm.import_;

import android.content.Context;
import android.graphics.Color;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class Lecture_Schedule implements Serializable {
    @NonNull
    private final List<Lecture> lecture, import_lecture;

    /**
     * Create an empty lecture schedule
     */
    public Lecture_Schedule() {
        lecture = new ArrayList<>();
        import_lecture = new ArrayList<>();
    }

    /**
     * import ics file in lecture schedule
     *
     * @param calendar the ics file object
     */
    @NonNull
    public Lecture_Schedule ImportICS(@NonNull ICS calendar) {
        import_lecture.clear();
        if (calendar.getVEventList() != null)
            for (ICS.vEvent ev : calendar.getVEventList()) {
                try {
                    if (ev.SUMMARY != null && ev.LOCATION != null && ev.DTStart != null && ev.DTend != null) {
                        Date start = ICS.stringToDate(ev.DTStart), end = ICS.stringToDate(ev.DTend);
                        if (start != null && end != null)
                            import_lecture.add(new Lecture(true).setName(ev.SUMMARY).setLocation(ev.LOCATION).setStart(start).setEnd(end));
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        return this;
    }

    /**
     * get all lectures from Lecture_Schedule
     *
     * @return all Lectures
     */
    @NonNull
    public List<Lecture> getAllLecture() {
        List<Lecture> all = new ArrayList<>();
        all.addAll(lecture);
        all.addAll(import_lecture);
        Collections.sort(all);
        return all;
    }

    /**
     * delete all import events
     */
    public void clearImportEvents() {
        this.import_lecture.clear();
    }

    /**
     * delete all not import events
     */
    public void clearNormalEvents() {
        this.lecture.clear();
    }

    /**
     * delete all events. import and not import
     */
    public void clearEvents() {
        this.import_lecture.clear();
        this.lecture.clear();
    }

    public void addLecture(@NonNull Lecture lecture) {
        this.lecture.add(lecture);
    }

    public void removeLecture(@NonNull Lecture data) {
        int id1 = lecture.indexOf(data), id2 = import_lecture.indexOf(data);
        if (id1 >= 0) lecture.remove(id1);
        if (id2 >= 0) import_lecture.remove(id2);
    }

    /**
     * get the next lecture, at least starting tomorrow 00:00:00:00
     *
     * @return next lecture
     */
    @Nullable
    public Lecture getNextFirstDayLecture() {
        boolean first = true;
        Lecture help = new Lecture(false).setStart(getDayAddDay(1)), help2 = new Lecture(false).setStart(getDayAddDay(0));
        for (Lecture l : getAllLecture())
            if (l.compareTo(help2) >= 0 && first) {
                first = false;
                if (l.start.after(Calendar.getInstance().getTime()))
                    return l;
            } else if (l.compareTo(help) >= 0)
                return l;
        return null;
    }

    /**
     * get the next day at 00:00:00:00
     *
     * @return next day as date
     */
    @NonNull
    private static Date getDayAddDay(int addDay) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, addDay);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }


    //----------------------------------------------------------------SAVE_LOAD---------------------------------------------------------

    /**
     * Save the Lecture Schedule in the internal storage of the application
     *
     * @param context context of the application
     */
    public void Save(@NonNull Context context) {
        FileOutputStream fos;
        try {
            fos = context.openFileOutput("LECTURE", Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(this);
            oos.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Load the Lecture Schedule from the internal storage of the application
     *
     * @param context context of the application
     */
    @NonNull
    public static Lecture_Schedule Load(@NonNull Context context) {
        try {
            FileInputStream fis = context.openFileInput("LECTURE");
            ObjectInputStream ois = new ObjectInputStream(fis);
            Lecture_Schedule erg = (Lecture_Schedule) ois.readObject();
            fis.close();
            ois.close();
            if (erg != null)
                return erg;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return new Lecture_Schedule();
    }

    /**
     * inner class to represent the lecture information
     */
    public static class Lecture implements Serializable, Comparable<Lecture> {
        private String docent, location, name;
        private Date start, end;
        private static int counter = 1;
        private int color = Color.RED;
        private final int id;
        private final boolean isImport;

        public Lecture(boolean isImport) {
            this.id = counter++;
            this.isImport = isImport;
        }

        public String getDocent() {
            return docent;
        }

        public String getLocation() {
            return location;
        }

        public String getName() {
            return name;
        }

        @NonNull
        public Date getStart() {
            return new Date(start.getTime() + TimeZone.getDefault().getOffset(Calendar.ZONE_OFFSET));
        }

        @NonNull
        public Date getEnd() {
            return new Date(end.getTime() + TimeZone.getDefault().getOffset(Calendar.ZONE_OFFSET));
        }

        public int getId() {
            return id;
        }

        public int getColor() {
            return color;
        }

        public boolean isImport() {
            return isImport;
        }

        @NonNull
        public Lecture setDocent(@NonNull String docent) {
            this.docent = docent;
            return this;
        }

        @NonNull
        public Lecture setLocation(@NonNull String location) {
            this.location = location;
            return this;
        }

        @NonNull
        public Lecture setName(@NonNull String name) {
            this.name = name;
            return this;
        }

        @NonNull
        public Lecture setStart(@NonNull Date start) {
            this.start = start;
            return this;
        }

        @NonNull
        public Lecture setEnd(@NonNull Date end) {
            this.end = end;
            return this;
        }

        @NonNull
        public Lecture setColor(int color) {
            this.color = color;
            return this;
        }

        @Override
        public boolean equals(@Nullable Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Lecture lecture = (Lecture) o;
            return id == lecture.id;
        }

        @Override
        public int compareTo(@NonNull Lecture lecture) {
            return start.compareTo(lecture.start);
        }
    }
}
