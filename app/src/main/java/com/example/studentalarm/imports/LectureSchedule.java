package com.example.studentalarm.imports;

import android.content.Context;
import android.graphics.Color;

import com.example.studentalarm.regular.Hours;
import com.example.studentalarm.regular.RegularLectureSchedule;
import com.example.studentalarm.save.SaveLecture;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class LectureSchedule {
    @NonNull
    public static final SimpleDateFormat FORMAT = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMAN);
    @NonNull
    private final List<Lecture> lecture, importLecture, holidays;
    private static int positionScroll = -1;

    /**
     * Create an empty lecture schedule
     */
    public LectureSchedule() {
        lecture = new ArrayList<>();
        importLecture = new ArrayList<>();
        holidays = new ArrayList<>();
    }

    /**
     * get all lectures from Lecture_Schedule
     *
     * @return all Lectures
     */
    @NonNull
    public List<Lecture> getAllLecture(@NonNull Context context) {
        List<Lecture> all = new ArrayList<>();
        all.addAll(lecture);
        all.addAll(importLecture);
        all.addAll(holidays);
        Calendar from = Calendar.getInstance(), end = Calendar.getInstance();
        from.add(Calendar.YEAR, -3);
        end.add(Calendar.YEAR, 3);
        all.addAll(getRegularLecture(context, from, end));
        Collections.sort(all);
        return all;
    }

    /**
     * get all lectures from Lecture_Schedule with holiday as each lecture per day
     *
     * @return all Lectures
     */
    @NonNull
    public List<Lecture> getAllLectureWithEachHoliday(@NonNull Context context) {
        List<Lecture> all = new ArrayList<>();
        all.addAll(lecture);
        all.addAll(importLecture);
        Calendar from = Calendar.getInstance(), end = Calendar.getInstance();
        from.add(Calendar.YEAR, -3);
        end.add(Calendar.YEAR, 3);
        all.addAll(getRegularLecture(context, from, end));
        for (Lecture l : holidays) {
            Calendar calendar = Calendar.getInstance(), later = Calendar.getInstance(), end_C = Calendar.getInstance();
            calendar.setTime(l.getStart());
            calendar.set(Calendar.MILLISECOND, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            later.setTime(l.getEnd());
            while (later.after(calendar)) {
                end_C.setTimeInMillis(calendar.getTimeInMillis());
                end_C.set(Calendar.HOUR_OF_DAY, 24);
                end_C.set(Calendar.MINUTE, 0);
                all.add(new LectureSchedule.Lecture(l.isImport(), calendar.getTime(), end_C.getTime()).setName(l.getName()).setColor(l.getColor()));
                calendar.add(Calendar.DAY_OF_MONTH, 1);
            }
        }
        Collections.sort(all);
        return all;
    }

    /**
     * get all lectures from Lecture_Schedule with holiday as each lecture per day and day title as lecture with id -1
     *
     * @return all Lectures
     */
    @NonNull
    public List<Lecture> getAllLectureWithEachHolidayAndDayTitle(@NonNull Context context) {
        positionScroll = -1;
        List<Lecture> erg = new ArrayList<>();
        String formatS = "01.01.1900", format2S;
        for (LectureSchedule.Lecture l : getAllLectureWithEachHoliday(context)) {
            format2S = FORMAT.format(l.getStart());
            if (!format2S.equals(formatS)) {
                formatS = format2S;
                if (positionScroll == -1 && l.getStart().after(Calendar.getInstance().getTime()))
                    positionScroll = erg.size();
                erg.add(new LectureSchedule.Lecture(false, l.getStart(), new Date(), -1));
            }
            erg.add(l);
        }
        if (positionScroll == -1 && lecture.size() > 0)
            positionScroll = lecture.size() - 1;
        return erg;
    }

    @NonNull
    public List<Lecture> getLecture() {
        return lecture;
    }

    @NonNull
    public List<Lecture> getImportLecture() {
        return importLecture;
    }

    @NonNull
    public List<Lecture> getHolidays() {
        return holidays;
    }

    public static int getPositionScroll() {
        return positionScroll;
    }

    /**
     * get the next lecture, at least starting tomorrow 00:00:00:00
     *
     * @return next lecture
     */
    @Nullable
    public Lecture getNextFirstDayLecture(@NonNull Context context) {
        boolean first = true;
        Lecture tomorrow = new Lecture(false, getDayAddDay(1), new Date()), today = new Lecture(false, getDayAddDay(0), new Date());
        for (Lecture l : getAllLectureWithoutHolidayAndHolidayEvents(context))
            if (l.compareTo(today) >= 0 && first) {
                first = false;
                if (l.start.after(Calendar.getInstance().getTime()))
                    return l;
            } else if (l.compareTo(tomorrow) >= 0)
                return l;
        return null;
    }

    public void addLecture(@NonNull Lecture lecture) {
        this.lecture.add(lecture);
    }

    public void addHoliday(@NonNull Lecture lecture) {
        this.holidays.add(lecture);
    }

    /**
     * import ics file in lecture schedule
     *
     * @param calendar the ics file object
     */
    @NonNull
    public LectureSchedule importICS(@NonNull ICS calendar) {
        importLecture.clear();
        List<ICS.vEvent> list = calendar.getVEventList();
        if (list != null)
            for (ICS.vEvent ev : list) {
                try {
                    if (ev.DTStart != null && ev.DTend != null && ev.SUMMARY != null) {
                        Date start = ICS.stringToDate(ev.DTStart), end = ICS.stringToDate(ev.DTend);
                        if (start != null && end != null)
                            importLecture.add(new Lecture(true, start, end).setName(ev.SUMMARY).setLocation(ev.LOCATION));
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        return this;
    }

    @NonNull
    public LectureSchedule removeLecture(@NonNull Lecture data) {
        int id1 = lecture.indexOf(data), id2 = importLecture.indexOf(data), id3 = holidays.indexOf(data);
        if (id1 >= 0) lecture.remove(id1);
        if (id2 >= 0) importLecture.remove(id2);
        if (id3 >= 0) holidays.remove(id3);
        return this;
    }

    /**
     * delete all holiday events
     */
    public void clearHolidayEvents() {
        this.holidays.clear();
    }

    /**
     * delete all import events
     */
    public void clearImportEvents() {
        this.importLecture.clear();
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
    @NonNull
    public LectureSchedule clearEvents() {
        this.holidays.clear();
        this.importLecture.clear();
        this.lecture.clear();
        return this;
    }

    /**
     * get all lecture which are not in the holidays
     *
     * @return lecture which are not in the holidays
     */
    @NonNull
    private List<Lecture> getAllLectureWithoutHolidayAndHolidayEvents(@NonNull Context context) {
        List<Lecture> all = new ArrayList<>(), all2 = new ArrayList<>();
        all.addAll(lecture);
        all.addAll(importLecture);
        Calendar from = Calendar.getInstance(), end = Calendar.getInstance();
        from.add(Calendar.YEAR, -3);
        end.add(Calendar.YEAR, 3);
        all.addAll(getRegularLecture(context, from, end));
        if (holidays.size() > 0) {
            boolean skip;
            for (Lecture l : all) {
                skip = false;
                for (int i = 0; i < holidays.size() && !skip; i++)
                    if (l.getStart().after(holidays.get(i).getStart()) && l.getStart().before(holidays.get(i).getEnd())) {
                        all2.add(l);
                        skip = true;
                    }
            }
            all.removeAll(all2);
        }
        Collections.sort(all);
        return all;
    }

    /**
     * get all regular lecture as lecture
     *
     * @param context context of app
     * @param from    date where the regularity should start
     * @param until   date where the regularity should end
     * @return list of lecture
     */
    @NonNull
    private List<Lecture> getRegularLecture(@NonNull Context context, @NonNull Calendar from, @NonNull Calendar until) {
        List<Lecture> erg = new ArrayList<>();
        List<List<Date>> help = getAllDaysWeek(from, until);
        RegularLectureSchedule schedule = RegularLectureSchedule.load(context);
        List<Hours> hours = Hours.load(context);
        for (RegularLectureSchedule.RegularLecture.RegularLectureTime fragmentLecture : schedule.getRegularLectures()) {
            Hours hour = hours.get(fragmentLecture.hour);
            for (Date date : help.get(fragmentLecture.getCalendarDay() - 1)) {
                Date[] dates = getDateWithTime(date, hour);
                if (date != null)
                    erg.add(new Lecture(true, dates[0], dates[1])
                            .setName(fragmentLecture.lecture.getName())
                            .setDocent(fragmentLecture.lecture.getDocent())
                            .setColor(fragmentLecture.lecture.getColor())
                            .setLocation(fragmentLecture.getActiveRoom()));
            }
        }
        return erg;
    }

    /**
     * combine date and times
     *
     * @param date  date to combine
     * @param hours time to combine
     * @return date start and end
     */
    @NonNull
    private Date[] getDateWithTime(@NonNull Date date, @NonNull Hours hours) {
        Date start = hours.getFromAsDate(), end = hours.getUntilAsDate();
        if (start == null || end == null) return null;
        Calendar startAsC = Calendar.getInstance(), endAsC = Calendar.getInstance();
        startAsC.setTime(start);
        endAsC.setTime(end);

        Calendar startC = Calendar.getInstance(), endC = Calendar.getInstance();
        startC.setTime(date);
        startC.set(Calendar.HOUR_OF_DAY, startAsC.get(Calendar.HOUR));
        startC.set(Calendar.MINUTE, startAsC.get(Calendar.MINUTE));
        startC.set(Calendar.SECOND, 0);
        startC.set(Calendar.MILLISECOND, 0);

        endC.setTime(date);
        endC.set(Calendar.HOUR_OF_DAY, endAsC.get(Calendar.HOUR));
        endC.set(Calendar.MINUTE, endAsC.get(Calendar.MINUTE));
        endC.set(Calendar.SECOND, 0);
        endC.set(Calendar.MILLISECOND, 0);
        return new Date[]{startC.getTime(), endC.getTime()};
    }

    /**
     * get all days sort by weekdays
     *
     * @param from  date where the regularity should start
     * @param until date where the regularity should end
     * @return List of list of date (1 layer - weekday, 2 layer - dates)
     */
    @NonNull
    private List<List<Date>> getAllDaysWeek(@NonNull Calendar from, @NonNull Calendar until) {
        List<List<Date>> erg = new ArrayList<>();
        for (int f = 0; f < 7; f++)
            erg.add(new ArrayList<>());
        while (!from.after(until)) {
            erg.get(from.get(Calendar.DAY_OF_WEEK) - 1).add(from.getTime());
            from.add(Calendar.DAY_OF_MONTH, 1);
        }
        return erg;
    }


    /**
     * get highest id
     */
    private int getHighestID() {
        int highest = 0;
        for (Lecture l : lecture)
            if (l.id > highest) highest = l.id;
        for (Lecture l : importLecture)
            if (l.id > highest) highest = l.id;
        return highest;
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
    public void save(@NonNull Context context) {
        FileOutputStream fos;
        try {
            fos = context.openFileOutput("LECTURE", Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(createSave());
            oos.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * create a save object
     *
     * @return lecture_schedule as save object
     */
    @NonNull
    private SaveLecture createSave() {
        SaveLecture saveLecture = new SaveLecture();
        saveLecture.saves = new SaveLecture.Save[3][];
        saveLecture.saves[0] = new SaveLecture.Save[this.lecture.size()];
        for (int i = 0; i < this.lecture.size(); i++) {
            Lecture l = this.lecture.get(i);
            SaveLecture.Save save = new SaveLecture.Save();
            save.name = l.name;
            save.docent = l.docent;
            save.location = l.location;
            save.start = l.start;
            save.end = l.end;
            save.color = l.color;
            save.id = l.id;
            save.isImport = l.isImport;
            save.isAllDayEvent = l.isAllDayEvent;
            saveLecture.saves[0][i] = save;
        }

        saveLecture.saves[1] = new SaveLecture.Save[this.importLecture.size()];
        for (int i = 0; i < this.importLecture.size(); i++) {
            Lecture l = this.importLecture.get(i);
            SaveLecture.Save save = new SaveLecture.Save();
            save.name = l.name;
            save.docent = l.docent;
            save.location = l.location;
            save.start = l.start;
            save.end = l.end;
            save.color = l.color;
            save.id = l.id;
            save.isImport = l.isImport;
            save.isAllDayEvent = l.isAllDayEvent;
            saveLecture.saves[1][i] = save;
        }

        saveLecture.saves[2] = new SaveLecture.Save[this.holidays.size()];
        for (int i = 0; i < this.holidays.size(); i++) {
            Lecture l = this.holidays.get(i);
            SaveLecture.Save save = new SaveLecture.Save();
            save.name = l.name;
            save.start = l.start;
            save.end = l.end;
            save.color = l.color;
            save.id = l.id;
            save.isImport = l.isImport;
            save.isAllDayEvent = l.isAllDayEvent;
            saveLecture.saves[2][i] = save;
        }
        return saveLecture;
    }

    /**
     * Load the Lecture Schedule from the internal storage of the application
     *
     * @param context context of the application
     */
    @NonNull
    public static LectureSchedule load(@NonNull Context context) {
        try {
            FileInputStream fis = context.openFileInput("LECTURE");
            ObjectInputStream ois = new ObjectInputStream(fis);
            LectureSchedule erg = convertSave((SaveLecture) ois.readObject());
            LectureSchedule.Lecture.setCounter(erg.getHighestID() + 1);
            fis.close();
            ois.close();
            return erg;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return new LectureSchedule();
    }

    /**
     * convert save object to lecture
     *
     * @param saveLecture object to convert
     * @return lecture object
     */
    @NonNull
    private static LectureSchedule convertSave(@Nullable SaveLecture saveLecture) {
        LectureSchedule lectureSchedule = new LectureSchedule();
        if (saveLecture == null) return lectureSchedule;
        for (int i = 0; i < saveLecture.saves[0].length; i++) {
            SaveLecture.Save save = saveLecture.saves[0][i];
            lectureSchedule.lecture.add(new Lecture(save.isImport, save.start, save.end, save.id).setColor(save.color).setLocation(save.location).setDocent(save.docent).setName(save.name).setAllDayEvent(save.isAllDayEvent));
        }
        for (int i = 0; i < saveLecture.saves[1].length; i++) {
            SaveLecture.Save save = saveLecture.saves[1][i];
            lectureSchedule.importLecture.add(new Lecture(save.isImport, save.start, save.end, save.id).setColor(save.color).setLocation(save.location).setDocent(save.docent).setName(save.name).setAllDayEvent(save.isAllDayEvent));
        }
        for (int i = 0; i < saveLecture.saves[2].length; i++) {
            SaveLecture.Save save = saveLecture.saves[2][i];
            lectureSchedule.holidays.add(new Lecture(save.isImport, save.start, save.end, save.id).setColor(save.color).setName(save.name).setAllDayEvent(save.isAllDayEvent));
        }
        return lectureSchedule;
    }

    /**
     * inner class to represent the lecture information
     */
    public static class Lecture implements Comparable<Lecture> {
        private static int counter = 1;
        private final int id;
        private final boolean isImport;
        @Nullable
        private String docent, location, name;
        @NonNull
        private Date start, end;
        private int color = Color.RED;
        private boolean isAllDayEvent;

        public Lecture(boolean isImport, @NonNull Date start, @NonNull Date end) {
            this.start = new Date(start.getTime() + TimeZone.getDefault().getOffset(Calendar.ZONE_OFFSET));
            this.end = new Date(end.getTime() + TimeZone.getDefault().getOffset(Calendar.ZONE_OFFSET));
            this.id = counter++;
            this.isImport = isImport;
        }

        private Lecture(boolean isImport, @NonNull Date start, @NonNull Date end, int id) {
            this.start = new Date(start.getTime() + TimeZone.getDefault().getOffset(Calendar.ZONE_OFFSET));
            this.end = new Date(end.getTime() + TimeZone.getDefault().getOffset(Calendar.ZONE_OFFSET));
            this.id = id;
            this.isImport = isImport;
        }

        @Nullable
        public String getDocent() {
            return docent;
        }

        @Nullable
        public String getLocation() {
            return location;
        }

        @NonNull
        public String getName() {
            return name == null ? "" : name;
        }

        @NonNull
        public Date getStart() {
            return start;// new Date(start.getTime() + TimeZone.getDefault().getOffset(Calendar.ZONE_OFFSET));
        }

        @NonNull
        public Date getEnd() {
            return end;// new Date(end.getTime() + TimeZone.getDefault().getOffset(Calendar.ZONE_OFFSET));
        }

        public int getId() {
            return id;
        }

        public int getColor() {
            return color;
        }

        public boolean isAllDayEvent() {
            return isAllDayEvent;
        }

        public boolean isImport() {
            return isImport;
        }

        public static void setCounter(int counter) {
            Lecture.counter = counter;
        }

        @NonNull
        public Lecture setDocent(@Nullable String docent) {
            this.docent = docent;
            return this;
        }

        @NonNull
        public Lecture setLocation(@Nullable String location) {
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

        @NonNull
        public Lecture setAllDayEvent(boolean allDayEvent) {
            isAllDayEvent = allDayEvent;
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
