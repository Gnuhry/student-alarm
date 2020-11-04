package com.example.studentalarm;

import android.util.Log;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class ICS {
    private String Version, Method, X_WR_Timezone, Calscale;
    private vTimezone vTimezone;
    private List<vEvent> vEventList;
    private final OkHttpClient client = new OkHttpClient();
    private boolean successful=false;

    /**
     * import a ics file into an object
     *
     * @param link        link to the ics file
     * @param synchronous {true} synchronous import {false} asynchronous import
     */
    public ICS(String link, boolean synchronous) {
        vEventList = new ArrayList<>();
        if (synchronous)
            runSynchronous(link);
        else
            runAsynchronous(link);
    }

    /**
     * returns all events from the ics file
     *
     * @return list of iCalenar.events
     */
    public List<vEvent> getvEventList() {
        return vEventList;
    }

    /**
     * get the ics file asynchronous from the internet and parse it to an object
     *
     * @param link web link to the ics file
     */
    private void runAsynchronous(String link) {
        Request request = new Request.Builder()
                .url(link)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) {
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful())
                        Log.e("ICS-Asynchronous", "Unexpected code " + response);
                    parse(responseBody.string());
                } catch (IOException e) {
                    e.printStackTrace();
                    successful=false;
                }
            }
        });
    }

    /**
     * get the ics file synchronous from the internet and parse it to an object
     *
     * @param link web link to the ics file
     */
    public void runSynchronous(String link) {
        successful=false;
        Request request = new Request.Builder()
                .url(link)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful())
                Log.e("ICS-Synchronous", "Unexpected code " + response);
            parse(response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
            successful=false;
        }
    }

    /**
     * parse an ics file to an object
     *
     * @param icsFile the ics file as string
     */
    private void parse(String icsFile) {
        String[] split = icsFile.split("\\n");
        for (int findBegin = 0; findBegin < split.length; findBegin++)
            if (split[findBegin].startsWith("BEGIN:VCALENDAR")) {
                successful=true;
                for (int f = findBegin + 1; f < split.length; f++)
                    switch (split[f].split(":")[0]) {
                        case "BEGIN":
                            List<String> strings = new ArrayList<>();
                            switch (split[f].split(":")[1]) {
                                case "VEVENT":
                                    while (!split[++f].equals("END:VEVENT"))
                                        strings.add(split[f]);
                                    try {
                                        vEventList.add(new vEvent(strings.toArray(new String[0])));
                                    } catch (ParseException ex) { //WRONG DATE
                                        ex.printStackTrace();
                                    }
                                    break;

                                case "VTIMEZONE":
                                    while (!split[++f].equals("END:VTIMEZONE"))
                                        strings.add(split[f]);
                                    vTimezone = new vTimezone(strings.toArray(new String[0]));
                                    break;
                            }
                            break;
                        case "VERSION":
                            Version = split[f].split(":")[1];
                            break;
                        case "METHOD":
                            Method = split[f].split(":")[1];
                            break;
                        case "X-WR-TIMEZONE":
                            X_WR_Timezone = split[f].split(":")[1];
                            break;
                        case "CALSSCALE":
                            Calscale = split[f].split(":")[1];
                            break;
                    }
                return;
            }
    }

    public boolean isSuccessful() {
        return successful;
    }

    /**
     * intern class to represent the timezone information
     */
    public static class vTimezone {
        public vTimezone(String[] strings) {
            //not needed
        }
    }

    /**
     * intern class to represent the event information
     */
    public static class vEvent {
        private String UID, LOCATION, SUMMARY;
        private Date DTSTART, DTEND, DTSTAMP;
        private final DateFormat dformat = new SimpleDateFormat("yyyyMMdd-HHmmss");

        /**
         * parse the event string to an object
         *
         * @param strings strings with information of one event
         * @throws ParseException Exception if date string has the wrong format
         */
        public vEvent(String[] strings) throws ParseException {
            for (String s : strings) {
                if (s.split(":").length > 1) {
                    switch (s.split(":")[0]) {
                        case "UID":
                            UID = s.split(":")[1];
                            break;
                        case "LOCATION":
                            LOCATION = s.split(":")[1];
                            break;
                        case "SUMMARY":
                            SUMMARY = s.substring(8);
                            break;
                        case "DTSTART":
                            DTSTART = dformat.parse(s.split(":")[1].replace('T', '-'));
                            break;
                        case "DTEND":
                            DTEND = dformat.parse(s.split(":")[1].replace('T', '-'));
                            break;
                        case "DTSTAMP":
                            DTSTAMP = dformat.parse(s.split(":")[1].replace('T', '-'));
                            break;
                    }
                }
            }
        }

        public String getUID() {
            return UID;
        }

        public String getLOCATION() {
            return LOCATION;
        }

        public String getSUMMARY() {
            return SUMMARY;
        }

        public Date getDTSTART() {
            return DTSTART;
        }

        public Date getDTEND() {
            return DTEND;
        }

        public Date getDTSTAMP() {
            return DTSTAMP;
        }

        public DateFormat getDformat() {
            return dformat;
        }

        @Override
        public String toString() {
            return "vEvent{" +
                    "SUMMARY='" + SUMMARY + '\'' +
                    ", DTSTART=" + DTSTART +
                    ", DTEND=" + DTEND +
                    '}';
        }
    }

}
