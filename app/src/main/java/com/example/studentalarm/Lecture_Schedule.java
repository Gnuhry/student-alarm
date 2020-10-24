package com.example.studentalarm;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class Lecture_Schedule {
    private TimeZone timezone;
    private List<Lecture> lecture;
    //TODO Soll es möglich sein, ereignsise hinzuzufügen, wenn es einen Import gibt?

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

    public void addLecture(Lecture lecture){
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
    public class Lecture {
        private String docent, location, name;
        private Date start, end;

        public Lecture(String name, String docent, String location, Date start, Date end) {
            this.name = name;
            this.docent = docent;
            this.location = location;
            this.start = start;
            this.end = end;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDocent() {
            return docent;
        }

        public void setDocent(String docent) {
            this.docent = docent;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public Date getStart() {
            return start;
        }

        public void setStart(Date start) {
            this.start = start;
        }

        public Date getEnd() {
            return end;
        }

        public void setEnd(Date end) {
            this.end = end;
        }
    }
}
