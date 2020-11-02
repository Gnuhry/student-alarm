package com.example.studentalarm;

import android.graphics.Color;
import android.util.Log;

import com.alamkanak.weekview.WeekViewDisplayable;
import com.alamkanak.weekview.WeekViewEvent;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

public class Lecture_Schedule {
    private TimeZone timezone;
    private final List<Lecture> lecture;

    /**
     * Create an empty lecture schedule
     */
    public Lecture_Schedule() {
        lecture = new ArrayList<>();
        timezone = TimeZone.getDefault();
    }

    /**
     * Create an lecture schedule with ics file import
     *
     * @param calenar the ics file object
     */
    public Lecture_Schedule(ICS calenar) {
        lecture = new ArrayList<>();
        for (ICS.vEvent ev : calenar.getvEventList())
            lecture.add(new Lecture(ev.getSUMMARY(), null, ev.getLOCATION(), ev.getDTSTART(), ev.getDTEND()));
        timezone = TimeZone.getDefault();//TODO Getter TimeZone iCalendar
    }

    public void addLecture(Lecture lecture) {
        this.lecture.add(lecture);
    }

    public List<Lecture> getLecture() {
        return lecture;
    }

    public TimeZone getTimezone() {
        return timezone;
    }

    public void setTimezone(TimeZone timezone) {
        this.timezone = timezone;
    }

    /**
     * inner class to represent the lecture information
     */
    public static class Lecture implements WeekViewDisplayable<Lecture> {
        private final String docent, location, name;
        private final Date start, end;
        private static int id = 1;
        private final int color;

        public Lecture(String name, String docent, String location, Date start, Date end) {
            this.name = name;
            this.docent = docent;
            this.location = location;
            this.start = start;
            this.end = end;
            this.color = Color.RED;
        }

        public Lecture(String name, String docent, String location, Date start, Date end, int color) {
            this.name = name;
            this.docent = docent;
            this.location = location;
            this.start = start;
            this.end = end;
            this.color = color;
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
            erg.setTitle(this.name);
            erg.setStartTime(startCal);
            erg.setEndTime(endCal);
            if (this.location != null)
                erg.setLocation(this.location);
            erg.setStyle(builder.build());
            erg.setId(id++);
            return erg.build();
        }
    }
}
