package com.example.studentalarm.imports;

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
    @NonNull
    private static final String
            BEGIN_VCALENDAR = "BEGIN:VCALENDAR",
            BEGIN_VTIMEZONE = "BEGIN:VTIMEZONE",
            END_VTIMEZONE = "END:VTIMEZONE",
            BEGIN_VEVENT = "BEGIN:VEVENT",
            END_VEVENT = "END:VEVENT",
            VEVENT_UID = "UID",
            VEVENT_LOCATION = "LOCATION",
            VEVENT_SUMMARY = "SUMMARY",
            VEVENT_DT_STAMP = "DTSTAMP",
            VEVENT_DT_START = "DTSTART",
            VEVENT_DT_END = "DTEND",
            VEVENT_RRULE = "RRULE",
            VEVENT_RRULE_FREQ = "FREQ",
            VEVENT_RRULE_BYDAY = "BYDAY",
            VEVENT_RRULE_INTERVAL = "INTERVAL",
            BEGIN_TIMEZONE_DAY_LIGHT = "BEGIN:DAYLIGHT",
            END_TIMEZONE_DAY_LIGHT = "END:DAYLIGHT",
            BEGIN_TIMEZONE_STANDARD = "BEGIN:STANDARD",
            END_TIMEZONE_STANDARD = "END:STANDARD",
            TIMEZONE_R_RULE = "RRULE",
            TIMEZONE_TZ_OFFSET_TO = "TZOFFSETTO",
            VERSION = "VERSION:2.0",
            METHOD = "METHOD:PUBLISH",
            CAL_SCALE = "CALSCALE:GREGORIAN",
            END_VCALENDAR = "END:VCALENDAR";
    private static final int YEAR_UNTIL_PLUS_THIS_YEAR = 50;
    private static final String LOG = "ICS";

    @NonNull
    private final List<vEvent> vEventList;
    @NonNull
    private final List<vTimezone> vTimezone;

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

    public boolean isSuccessful() {
        return !vEventList.isEmpty();
    }


    /**
     * convert ICS string to date
     *
     * @param string ICS string to convert
     * @return date
     */
    @Nullable
    public static Date stringToDate(@NonNull String string) throws ParseException {
        return new SimpleDateFormat("yyyyMMdd-HHmmss", Locale.getDefault()).parse(string.replace("T", "-"));
    }

    /**
     * Format ICS file string
     *
     * @param events events to export
     * @return ics file as string
     */
    @NonNull
    public static String exportToICS(@NonNull List<vEvent> events) {
        StringBuilder erg = new StringBuilder(BEGIN_VCALENDAR).append("\n")
                .append(VERSION).append("\n")
                .append(METHOD).append("\n")
                .append(CAL_SCALE).append("\n");
        for (vEvent event : events) {
            erg.append(BEGIN_VEVENT).append("\n")
                    .append(VEVENT_UID).append(":").append(event.UID).append("\n")
                    .append(VEVENT_SUMMARY).append(":").append(event.SUMMARY).append("\n")
                    .append(VEVENT_DT_START).append(":").append(event.DTStart).append("\n")
                    .append(VEVENT_DT_END).append(":").append(event.DTend).append("\n")
                    .append(VEVENT_DT_STAMP).append(":").append(event.DTStamp).append("\n");
            if (event.LOCATION != null)
                erg.append(VEVENT_LOCATION).append(":").append(event.LOCATION).append("\n");
            if (event.RRule != null) {
                StringBuilder rule = new StringBuilder();
                rule.append(VEVENT_RRULE).append(":");
                if (event.RRule.FREQ != null)
                    rule.append(VEVENT_RRULE_FREQ).append("=").append(event.RRule.FREQ).append(";");
                if (event.RRule.INTERVAL != null)
                    rule.append(VEVENT_RRULE_INTERVAL).append("=").append(event.RRule.INTERVAL).append(";");
                if (event.RRule.BY_DAY != null)
                    rule.append(VEVENT_RRULE_BYDAY).append("=").append(event.RRule.BY_DAY).append(";");
                if (!rule.toString().equals(VEVENT_RRULE + ":"))
                    erg.append(rule.delete(rule.length() - 1, rule.length())).append("\n");
            }
            erg.append(END_VEVENT).append("\n");
        }
        return erg.append(END_VCALENDAR).toString();
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

    /**
     * Get all DateTimes of the timezone rules
     *
     * @param timezone timezone to get the rules from
     * @param start    when the rule starting
     * @return list of all rule dates
     */
    @NonNull
    private List<DateTime> getDatesTimeZone(@NonNull vTimezone timezone, @NonNull Calendar start) throws InvalidRecurrenceRuleException {
        List<DateTime> dateTimes = new ArrayList<>();
        RecurrenceRule rule = new RecurrenceRule(timezone.rule);
        RecurrenceRuleIterator it = rule.iterator(new DateTime(start.getTimeInMillis()));
        int year_ = start.get(Calendar.YEAR);
        while (it.hasNext() && year_ <= Calendar.getInstance().get(Calendar.YEAR) + YEAR_UNTIL_PLUS_THIS_YEAR) {
            dateTimes.add(it.nextDateTime());
            year_ = dateTimes.get(dateTimes.size() - 1).getYear();
        }
        return dateTimes;
    }

    /**
     * change date to UTC
     *
     * @param s_date string date to change
     * @return string date as UTC
     */
    @NonNull
    private String setTimeZones(@NonNull String s_date) throws ParseException, InvalidRecurrenceRuleException {
        Log.d(LOG, "date before add timezone: " + s_date);
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
            Log.d(LOG, "Add " + hour + " hour and " + minute + " min");
            calendar.add(Calendar.HOUR_OF_DAY, -hour);
            calendar.add(Calendar.MINUTE, -minute);
            String erg = new SimpleDateFormat("yyyyMMdd-HHmmss", Locale.getDefault()).format(calendar.getTime());
            Log.d(LOG, "date after add timezone: " + erg);
            return erg;
        }
        return s_date;
    }

    /**
     * parse an ics file to an object
     *
     * @param icsFile the ics file as string
     */
    private void parse(@NonNull String icsFile) {
        int id = icsFile.indexOf(BEGIN_VCALENDAR);
        if (id < 0) return;
        String icsFile_ = icsFile.substring(id + BEGIN_VCALENDAR.length());
        id = icsFile_.indexOf(BEGIN_VTIMEZONE);
        int id2 = icsFile_.indexOf(END_VTIMEZONE);
        if (id >= 0 && id2 >= 0) {
            String timeZone = icsFile_.substring(id + BEGIN_VTIMEZONE.length(), id2);
            Log.d(LOG, "timeZone: " + timeZone);
            id = timeZone.indexOf(BEGIN_TIMEZONE_DAY_LIGHT);
            id2 = timeZone.indexOf(END_TIMEZONE_DAY_LIGHT);
            vTimezone.add(new vTimezone(timeZone.substring(id + BEGIN_TIMEZONE_DAY_LIGHT.length(), id2).split("\n")));
            id = timeZone.indexOf(BEGIN_TIMEZONE_STANDARD, id2);
            id2 = timeZone.indexOf(END_TIMEZONE_STANDARD, id2);
            vTimezone.add(new vTimezone(timeZone.substring(id + BEGIN_TIMEZONE_STANDARD.length(), id2).split("\n")));
            icsFile_ = icsFile_.substring(id2 + END_VTIMEZONE.length());
        }
        id = icsFile_.indexOf(BEGIN_VEVENT);
        id2 = icsFile_.indexOf(END_VEVENT, id);
        while (id >= 0 && id2 >= 0) {
            String event = icsFile_.substring(id + BEGIN_VEVENT.length(), id2);
            Log.d(LOG, "event: " + event);
            vEventList.add(new vEvent(event.split("\n")));
            id = icsFile_.indexOf(BEGIN_VEVENT, id2);
            id2 = icsFile_.indexOf(END_VEVENT, id);
        }

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
                        case VEVENT_DT_START:
                            DTStart = s.split(":")[1];
                            break;
                        case TIMEZONE_TZ_OFFSET_TO:
                            TZOffsetTo = s.split(":")[1];
                            break;
                        case TIMEZONE_R_RULE:
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
        public vRRule RRule;

        public vEvent() {
        }

        public vEvent(@NonNull String[] strings) {
            for (String s : strings)
                if (s.split(":").length > 1)
                    switch (s.split(":")[0]) {
                        case VEVENT_UID:
                            UID = s.split(":")[1];
                            break;
                        case VEVENT_LOCATION:
                            LOCATION = s.split(":")[1];
                            break;
                        case VEVENT_SUMMARY:
                            SUMMARY = s.substring(8);
                            break;
                        case VEVENT_DT_START:
                            DTStart = s.split(":")[1];
                            break;
                        case VEVENT_DT_END:
                            DTend = s.split(":")[1];
                            break;
                        case VEVENT_DT_STAMP:
                            DTStamp = s.split(":")[1];
                            break;
                        case VEVENT_RRULE:
                            RRule = new vRRule(s.split(":")[1].split(";"));
                            break;
                    }
        }
    }

    public static class vRRule {
        public String FREQ, BY_DAY, INTERVAL;

        public vRRule() {
        }

        public vRRule(@NonNull String[] strings) {
            for (String s : strings)
                if (s.split("=").length > 1)
                    switch (s.split("=")[0]) {
                        case VEVENT_RRULE_FREQ:
                            FREQ = s.split("=")[1];
                            break;
                        case VEVENT_RRULE_BYDAY:
                            BY_DAY = s.split("=")[1];
                            break;
                        case VEVENT_RRULE_INTERVAL:
                            INTERVAL = s.split("=")[1];
                            break;
                    }
        }
    }


}
