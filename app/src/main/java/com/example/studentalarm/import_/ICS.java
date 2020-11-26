package com.example.studentalarm.import_;

import android.util.Log;

import org.dmfs.rfc5545.DateTime;
import org.dmfs.rfc5545.recur.InvalidRecurrenceRuleException;
import org.dmfs.rfc5545.recur.RecurrenceRule;
import org.dmfs.rfc5545.recur.RecurrenceRuleIterator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ICS {
    private static final String Begin_VCalendar = "BEGIN:VCALENDAR";
    private static final String Begin_VTimezone = "BEGIN:VTIMEZONE";
    private static final String End_VTimezone = "END:VTIMEZONE";
    private static final String Begin_VEvent = "BEGIN:VEVENT";
    private static final String End_VEvent = "END:VEVENT";
    private static final String VEvent_UID = "UID";
    private static final String VEvent_Location = "LOCATION";
    private static final String VEvent_Summary = "SUMMARY";
    private static final String VEvent_DtStamp = "DTSTAMP";
    private static final String VEvent_DtStart = "DTSTART";
    private static final String VEvent_DtEnd = "DTEND";
    private static final String Begin_Timezone_DayLight = "BEGIN:DAYLIGHT";
    private static final String End_Timezone_DayLight = "END:DAYLIGHT";
    private static final String Begin_Timezone_Standard = "BEGIN:STANDARD";
    private static final String End_Timezone_Standard = "END:STANDARD";
    private static final String Timezone_RRule = "RRULE";
    private static final String Timezone_TZOffsetTo = "TZOFFSETTO";


    @NonNull
    private final List<vEvent> vEventList;
    @NonNull
    private final List<vTimezone> vTimezone;
    private final int year = 1950;

    public ICS(@NonNull String icsFile) {
        vEventList = new ArrayList<>();
        vTimezone = new ArrayList<>();
        parse(icsFile);
    }

    /**
     * returns all events from the ics file
     *
     * @return list of iCalendar.events
     */
    @Nullable
    public List<vEvent> getVEventList() {
        try {
            return getVEventListWithUTCTime();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (InvalidRecurrenceRuleException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * returns all events from the ics file with teh UTC time
     *
     * @return list of iCalendar.events
     */
    @NonNull
    private List<vEvent> getVEventListWithUTCTime() throws ParseException, InvalidRecurrenceRuleException {
        for (vEvent event : vEventList) {
            if (event.DTStart != null && event.DTend != null) {
                event.DTStart = setTimeZones(event.DTStart);
                event.DTend = setTimeZones(event.DTend);
            }
        }
        return vEventList;
    }

    @Nullable
    private String setTimeZones(@NonNull String s_date) throws ParseException, InvalidRecurrenceRuleException {
        if (vTimezone.size() > 1) {
            boolean normal = true;
            List<DateTime> standard = getDatesTimeZone(vTimezone.get(0)),
                    standard2 = getDatesTimeZone(vTimezone.get(1));
            Calendar calendar = Calendar.getInstance(),
                    calendar1 = Calendar.getInstance(),
                    calendar2 = Calendar.getInstance();
            Date date = (stringToDate(s_date));
            if (date == null) return null;
            calendar.setTime(date);
            int year = calendar.get(Calendar.YEAR) - this.year;
            calendar1.setTimeInMillis(standard.get(year).getTimestamp());
            calendar2.setTimeInMillis(standard2.get(year).getTimestamp());

            if (calendar1.after(calendar2)) {
                Calendar calendar3 = calendar1;
                calendar1 = calendar2;
                calendar2 = calendar3;
                normal = false;
            }
            String offset = vTimezone.get((calendar.after(calendar1) && calendar.before(calendar2)) ? normal ? 0 : 1 : normal ? 1 : 0).TZOffsetTo;
            int hour = Integer.parseInt(offset.substring(1, 3)), minute = Integer.parseInt(offset.substring(3, 5));
            if (offset.charAt(0) == '-') {
                hour *= -1;
                minute *= -1;
            }
            calendar.add(Calendar.HOUR_OF_DAY, -hour);
            calendar.add(Calendar.MINUTE, -minute);
            return new SimpleDateFormat("yyyyMMdd-HHmmss", Locale.getDefault()).format(calendar.getTime());
        }
        return null;
    }

    @NonNull
    private List<DateTime> getDatesTimeZone(@NonNull vTimezone timezone) throws InvalidRecurrenceRuleException {
        List<DateTime> dateTimes = new ArrayList<>();
        RecurrenceRule rule = new RecurrenceRule(timezone.rule);
        int year = this.year;
        RecurrenceRuleIterator it = rule.iterator(new DateTime(year, 0, 1));
        while (it.hasNext() && year <= Calendar.getInstance().get(Calendar.YEAR)) {
            dateTimes.add(it.nextDateTime());
            year = dateTimes.get(dateTimes.size() - 1).getYear();
        }
        return dateTimes;
    }

    /**
     * parse an ics file to an object
     *
     * @param icsFile the ics file as string
     */
    private void parse(@NonNull String icsFile) {
        int id = icsFile.indexOf(Begin_VCalendar);
        if (id < 0) return;
        String icsFile_ = icsFile.substring(id + Begin_VCalendar.length());
        id = icsFile_.indexOf(Begin_VTimezone);
        int id2 = icsFile_.indexOf(End_VTimezone);
        if (id >= 0 && id2 >= 0) {
            String timeZone = icsFile_.substring(id + Begin_VTimezone.length(), id2);
            id = timeZone.indexOf(Begin_Timezone_DayLight);
            id2 = timeZone.indexOf(End_Timezone_DayLight);
            vTimezone.add(new vTimezone(timeZone.substring(id + Begin_Timezone_DayLight.length(), id2).split("\\n")));
            id = timeZone.indexOf(Begin_Timezone_Standard, id2);
            id2 = timeZone.indexOf(End_Timezone_Standard, id2);
            vTimezone.add(new vTimezone(timeZone.substring(id + Begin_Timezone_Standard.length(), id2).split("\\n")));
            icsFile_ = icsFile_.substring(id2 + End_VTimezone.length());
        }
        id = icsFile_.indexOf(Begin_VEvent);
        id2 = icsFile_.indexOf(End_VEvent, id);
        while (id >= 0 && id2 >= 0) {
            Log.d(id + "", "" + id2);
            vEventList.add(new vEvent(icsFile_.substring(id + Begin_VEvent.length(), id2).split("\\n")));
            id = icsFile_.indexOf(Begin_VEvent, id2);
            id2 = icsFile_.indexOf(End_VEvent, id);
        }
    }

    @Nullable
    public static Date stringToDate(@NonNull String string) throws ParseException {
        return new SimpleDateFormat("yyyyMMdd-HHmmss", Locale.getDefault()).parse(string.replace("T", "-"));
    }


    /**
     * intern class to represent the timezone information
     */
    public static class vTimezone {
        public String DTStart, TZOffsetTo, rule;

        public vTimezone(@NonNull String[] strings) {
            for (String s : strings)
                if (s.split(":").length > 1)
                    switch (s.split(":")[0]) {
                        case VEvent_DtStart:
                            DTStart = s.split(":")[1];
                            break;
                        case Timezone_TZOffsetTo:
                            TZOffsetTo = s.split(":")[1];
                            break;
                        case Timezone_RRule:
                            rule = s.split(":")[1];
                            break;
                    }
        }
    }


    /**
     * intern class to represent the event information
     */
    public static class vEvent {
        @Nullable
        public String UID, LOCATION, SUMMARY, DTStart, DTend, DTStamp;

        public vEvent(@NonNull String[] strings) {
            for (String s : strings)
                if (s.split(":").length > 1)
                    switch (s.split(":")[0]) {
                        case VEvent_UID:
                            UID = s.split(":")[1];
                            break;
                        case VEvent_Location:
                            LOCATION = s.split(":")[1];
                            break;
                        case VEvent_Summary:
                            SUMMARY = s.substring(8);
                            break;
                        case VEvent_DtStart:
                            DTStart = s.split(":")[1];
                            break;
                        case VEvent_DtEnd:
                            DTend = s.split(":")[1];
                            break;
                        case VEvent_DtStamp:
                            DTStamp = s.split(":")[1];
                            break;
                    }
        }
    }


}
