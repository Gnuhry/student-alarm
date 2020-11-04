package com.example.studentalarm;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;

import com.alamkanak.weekview.WeekViewDisplayable;
import com.alamkanak.weekview.WeekViewEvent;

import org.jetbrains.annotations.NotNull;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
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
        for (ICS.vEvent ev : calendar.getvEventList())
            import_lecture.add(new Lecture(ev.getSUMMARY(), null, ev.getLOCATION(), ev.getDTSTART(), ev.getDTEND()));
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
        return all;
    }

    public void deleteAllImportEvents(){
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
    public static class Lecture implements WeekViewDisplayable<Lecture>, Serializable {
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

        @NotNull
        @Override
        public WeekViewEvent<Lecture> toWeekViewEvent() { //TODO add Docent to Text
            WeekViewEvent.Style.Builder builder = new WeekViewEvent.Style.Builder();
            builder.setBackgroundColor(color);

            Calendar startCal = new GregorianCalendar();
            startCal.setTime(this.start);
            Log.d("Lecture - Start Date", startCal.toString());

            Calendar endCal = new GregorianCalendar();
            endCal.setTime(this.end);
            Log.d("Lecture - End Date", endCal.toString());

            WeekViewEvent.Builder<Lecture> erg = new WeekViewEvent.Builder<>(this);
            erg.setTitle(this.name + " - " + this.docent);
            erg.setStartTime(startCal);
            erg.setEndTime(endCal);
            if (this.location != null)
                erg.setLocation(this.location);
            erg.setStyle(builder.build());
            erg.setId(this.id);
            return erg.build();
        }
    }
}
