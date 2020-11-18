package com.example.studentalarm.Import;

import android.content.Context;
import android.graphics.Color;

import com.alamkanak.weekview.WeekViewDisplayable;
import com.alamkanak.weekview.WeekViewEvent;

import org.jetbrains.annotations.NotNull;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class Lecture_Schedule implements Serializable {
    private TimeZone timezone;
    private final List<Lecture> lecture, import_lecture;

    /**
     * Create an empty lecture schedule
     */
    public Lecture_Schedule() {
        lecture = new ArrayList<>();
        import_lecture = new ArrayList<>();
        timezone = TimeZone.getDefault();
    }

    /**
     * import ics file in lecture schedule
     *
     * @param calendar the ics file object
     */
    public void ImportICS(ICS calendar) {
        import_lecture.clear();
        for (ICS.vEvent ev : calendar.getVEventList())
            import_lecture.add(new Lecture(ev.getSUMMARY(), null, ev.getLOCATION(), ev.getDTStart(), ev.getDTend()));
        timezone = TimeZone.getDefault();//TODO Getter TimeZone iCalendar
    }

    /**
     * get all lectures from Lecture_Schedule
     *
     * @return all Lectures
     */
    public List<Lecture> getAllLecture() {
        List<Lecture> all = new ArrayList<>();
        all.addAll(lecture);
        all.addAll(import_lecture);
        Collections.sort(all);
        return all;
    }

    public void deleteAllImportEvents() {
        this.import_lecture.clear();
    }

    public void addLecture(Lecture lecture) {
        this.lecture.add(lecture);
    }

    public TimeZone getTimezone() {
        return timezone;
    }

    public void setTimezone(TimeZone timezone) {
        this.timezone = timezone;
    }

    /**
     * get the first lecture at day of date
     *
     * @param date day, where the lecture take place
     * @return first lecture of the day
     */
    public Lecture getFirstLectureAtDate(Date date) {
        Lecture erg = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.GERMAN);
        if (date == null) return null;
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
    public Lecture getNextLecture(Date date) {
        Lecture erg = null;
        if (date == null) return null;
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
    public void Save(Context context) {
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
    public static Lecture_Schedule Load(Context context) {
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
    public static class Lecture implements WeekViewDisplayable<Lecture>, Serializable, Comparable<Lecture>{
        private final String docent, location, name;
        private final Date start, end;
        private static int counter = 1;
        private final int color, id;

        public Lecture(String name, String docent, String location, Date start, Date end) {
            this.name = name;
            this.docent = docent;
            this.location = location;
            this.start = start;
            this.end = end;
            this.color = Color.RED;
            this.id = counter++;
        }

        public Lecture(String name, String docent, String location, Date start, Date end, int color) {
            this.name = name;
            this.docent = docent;
            this.location = location;
            this.start = start;
            this.end = end;
            this.color = color;
            this.id = counter++;
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

        @NotNull
        @Override
        public WeekViewEvent<Lecture> toWeekViewEvent() {
            WeekViewEvent.Style.Builder builder = new WeekViewEvent.Style.Builder();
            builder.setBackgroundColor(color);

            if (start == null || end == null || name == null)
                return new WeekViewEvent.Builder<Lecture>().build();
            Calendar startCal = new GregorianCalendar();
            startCal.setTime(this.start);

            Calendar endCal = new GregorianCalendar();
            endCal.setTime(this.end);

            WeekViewEvent.Builder<Lecture> erg = new WeekViewEvent.Builder<>(this);
            StringBuilder sb = new StringBuilder(this.name);
            if (this.docent != null)
                sb.append(" - ").append(this.docent);
            erg.setTitle(sb.toString());
            erg.setStartTime(startCal);
            erg.setEndTime(endCal);
            if (this.location != null)
                erg.setLocation(this.location);
            erg.setStyle(builder.build());
            erg.setId(this.id);
            return erg.build();
        }

        @Override
        public int compareTo(Lecture lecture) {
            return start.compareTo(lecture.start);
        }
    }
}
