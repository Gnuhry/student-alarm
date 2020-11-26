package com.example.studentalarm.import_;

import android.content.Context;
import android.graphics.Color;

import org.jetbrains.annotations.NotNull;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
    public void ImportICS(@NotNull ICS calendar) {
        import_lecture.clear();
        for (ICS.vEvent ev : calendar.getVEventList())
            import_lecture.add(new Lecture(ev.getSUMMARY(), null, ev.getLOCATION(), ev.getDTStart(), ev.getDTend(), true));
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
    public void deleteAllImportEvents() {
        this.import_lecture.clear();
    }

    /**
     * delete all not import events
     */
    public void deleteAllNotImportEvents() {
        this.lecture.clear();
    }

    /**
     * delete all events. import and not import
     */
    public void deleteAllEvents() {
        this.import_lecture.clear();
        this.lecture.clear();
    }

    public void addLecture(Lecture lecture) {
        this.lecture.add(lecture);
    }

    public void removeLecture(Lecture data) {
        int id1 = lecture.indexOf(data), id2 = import_lecture.indexOf(data);
        if (id1 >= 0) lecture.remove(id1);
        if (id2 >= 0) import_lecture.remove(id2);
    }

    /**
     * get the first lecture at day of date
     *
     * @param date day, where the lecture take place
     * @return first lecture of the day
     */
    @Nullable
    public Lecture getFirstLectureAtDate(@NotNull Date date) {
        Lecture erg = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.GERMAN);
        for (Lecture l : getAllLecture())
            if (l.start != null && sdf.format(date).compareTo(sdf.format(l.start)) == 0) {
                if (erg == null)
                    erg = l;
                else if (l.start.before(erg.start))
                    erg = l;
            }
        return erg;
    }

    /**
     * get the next lecture after date
     *
     * @param date date before the next lecture
     * @return next lecture
     */
    @Nullable
    public Lecture getNextLecture(@NonNull Date date) {
        Lecture erg = null;
        for (Lecture l : getAllLecture())
            if (l.start != null && l.start.after(date)) {
                if (erg == null)
                    erg = l;
                else if (l.start.before(erg.start))
                    erg = l;
            }
        return erg;
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
        private int color;
        private final int id;
        private final boolean isImport;

        public Lecture(String name, String docent, String location, Date start, Date end, boolean isImport) {
            this.name = name;
            this.docent = docent;
            this.location = location;
            this.start = start;
            this.end = end;
            this.color = Color.RED;
            this.id = counter++;
            this.isImport = isImport;
        }

        public Lecture(String name, String docent, String location, Date start, Date end, boolean isImport, int color) {
            this.name = name;
            this.docent = docent;
            this.location = location;
            this.start = start;
            this.end = end;
            this.color = color;
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

        public Date getStart() {
            return start;
        }

        public Date getEnd() {
            return end;
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

        public void setDocent(String docent) {
            this.docent = docent;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setStart(Date start) {
            this.start = start;
        }

        public void setEnd(Date end) {
            this.end = end;
        }

        public void setColor(int color) {
            this.color = color;
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
