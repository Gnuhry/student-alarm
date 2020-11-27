package com.example.studentalarm.import_;

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
    @NonNull
    private static final String
            Begin_VCalendar = "BEGIN:VCALENDAR",
            Begin_VTimezone = "BEGIN:VTIMEZONE",
            End_VTimezone = "END:VTIMEZONE",
            Begin_VEvent = "BEGIN:VEVENT",
            End_VEvent = "END:VEVENT",
            VEvent_UID = "UID",
            VEvent_Location = "LOCATION",
            VEvent_Summary = "SUMMARY",
            VEvent_DtStamp = "DTSTAMP",
            VEvent_DtStart = "DTSTART",
            VEvent_DtEnd = "DTEND",
            Begin_Timezone_DayLight = "BEGIN:DAYLIGHT",
            End_Timezone_DayLight = "END:DAYLIGHT",
            Begin_Timezone_Standard = "BEGIN:STANDARD",
            End_Timezone_Standard = "END:STANDARD",
            Timezone_RRule = "RRULE",
            Timezone_TZOffsetTo = "TZOFFSETTO",
            Version = "VERSION:2.0",
            Method = "METHOD:PUBLISH",
            CalScale = "CALSCALE:GREGORIAN",
            End_VCalendar = "END:VCALENDAR";


    @NonNull
    private final List<vEvent> vEventList;
    @NonNull
    private final List<vTimezone> vTimezone;
    private static final int year_until_plus_this_year = 50;

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

    @NonNull
    private String setTimeZones(@NonNull String s_date) throws ParseException, InvalidRecurrenceRuleException {
        if (vTimezone.size() == 1 || vTimezone.size() == 2) {
            String offset = vTimezone.get(0).TZOffsetTo;
            Calendar calendar = Calendar.getInstance();
            Date date = (stringToDate(s_date));

            if (date == null) return s_date;
            calendar.setTime(date);
            if (vTimezone.size() == 2) {
                boolean normal = true;
                Calendar calendar1 = Calendar.getInstance(), calendar2 = Calendar.getInstance();

                Calendar calendar_help = Calendar.getInstance();
                Date help = stringToDate(vTimezone.get(0).DTStart);
                Date help2 = stringToDate(vTimezone.get(1).DTStart);
                if (help == null || help2 == null) return s_date;

                calendar_help.setTime(help);
                calendar1.setTimeInMillis(getDatesTimeZone(vTimezone.get(0), calendar_help).get(calendar.get(Calendar.YEAR) - calendar_help.get(Calendar.YEAR)).getTimestamp());
                calendar_help.setTime(help2);
                calendar2.setTimeInMillis(getDatesTimeZone(vTimezone.get(1), calendar_help).get(calendar.get(Calendar.YEAR) - calendar_help.get(Calendar.YEAR)).getTimestamp());
                if (calendar1.after(calendar2)) {
                    Calendar calendar3 = calendar1;
                    calendar1 = calendar2;
                    calendar2 = calendar3;
                    normal = false;
                }
                offset = vTimezone.get((calendar.after(calendar1) && calendar.before(calendar2)) ? normal ? 0 : 1 : normal ? 1 : 0).TZOffsetTo;
            }
            int hour = Integer.parseInt(offset.substring(1, 3)), minute = Integer.parseInt(offset.substring(3, 5));
            if (offset.charAt(0) == '-') {
                hour *= -1;
                minute *= -1;
            }
            calendar.add(Calendar.HOUR_OF_DAY, -hour);
            calendar.add(Calendar.MINUTE, -minute);
            return new SimpleDateFormat("yyyyMMdd-HHmmss", Locale.getDefault()).format(calendar.getTime());
        }
        return s_date;
    }

    @NonNull
    private List<DateTime> getDatesTimeZone(@NonNull vTimezone timezone, Calendar start) throws InvalidRecurrenceRuleException {
        List<DateTime> dateTimes = new ArrayList<>();
        RecurrenceRule rule = new RecurrenceRule(timezone.rule);
        RecurrenceRuleIterator it = rule.iterator(new DateTime(start.getTimeInMillis()));
        int year_ = start.get(Calendar.YEAR);
        while (it.hasNext() && year_ <= Calendar.getInstance().get(Calendar.YEAR) + year_until_plus_this_year) {
            dateTimes.add(it.nextDateTime());
            year_ = dateTimes.get(dateTimes.size() - 1).getYear();
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
            vTimezone.add(new vTimezone(timeZone.substring(id + Begin_Timezone_DayLight.length(), id2).split("\n")));
            id = timeZone.indexOf(Begin_Timezone_Standard, id2);
            id2 = timeZone.indexOf(End_Timezone_Standard, id2);
            vTimezone.add(new vTimezone(timeZone.substring(id + Begin_Timezone_Standard.length(), id2).split("\n")));
            icsFile_ = icsFile_.substring(id2 + End_VTimezone.length());
        }
        id = icsFile_.indexOf(Begin_VEvent);
        id2 = icsFile_.indexOf(End_VEvent, id);
        while (id >= 0 && id2 >= 0) {
            vEventList.add(new vEvent(icsFile_.substring(id + Begin_VEvent.length(), id2).split("\n")));
            id = icsFile_.indexOf(Begin_VEvent, id2);
            id2 = icsFile_.indexOf(End_VEvent, id);
        }

    }

    @Nullable
    public static Date stringToDate(@NonNull String string) throws ParseException {
        return new SimpleDateFormat("yyyyMMdd-HHmmss", Locale.getDefault()).parse(string.replace("T", "-"));
    }

    public static String ExportToICS(List<vEvent> events) {
        StringBuilder erg = new StringBuilder(Begin_VCalendar).append("\n")
                .append(Version).append("\n")
                .append(Method).append("\n")
                .append(CalScale).append("\n");
        for (vEvent event : events)
            erg.append(Begin_VEvent).append("\n")
                    .append(VEvent_UID).append(":").append(event.UID).append("\n")
                    .append(VEvent_Location).append(":").append(event.LOCATION).append("\n")
                    .append(VEvent_Summary).append(":").append(event.SUMMARY).append("\n")
                    .append(VEvent_DtStart).append(":").append(event.DTStart).append("\n")
                    .append(VEvent_DtEnd).append(":").append(event.DTend).append("\n")
                    .append(VEvent_DtStamp).append(":").append(event.DTStamp).append("\n")
                    .append(End_VEvent).append("\n");
        return erg.append(End_VCalendar).toString();
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

        public vEvent() {

        }

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
