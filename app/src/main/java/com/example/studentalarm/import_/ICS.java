package com.example.studentalarm.import_;

import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import biweekly.Biweekly;
import biweekly.component.VEvent;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class ICS {

    /**
     * get the ics file synchronous from the internet and parse it to an object
     *
     * @param link web link to the ics file
     * @return return all events
     */
    @Nullable
    public static List<VEvent> loadSynchronous(@NonNull String link) {
        Request request = new Request.Builder()
                .url(link)
                .build();

        try (Response response = new OkHttpClient().newCall(request).execute()) {
            if (!response.isSuccessful())
                Log.e("ICS-Synchronous", "Unexpected code " + response);
            ResponseBody body = response.body();
            if (body != null)
                return parse(body.string());
            else
                Log.e("ICS-Synchronous", "No body");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * parse an ics text to a list of events
     *
     * @param text ics file as string
     * @return list of events
     */
    private static List<VEvent> parse(String text) {
        return Biweekly.parse(text).first().getEvents();
    }

    //-------------------------Asynchronous------------------------------------
    private List<VEvent> vEventList;

    /**
     * import a ics file into an object
     *
     * @param link link to the ics file
     */
    public ICS(@NonNull String link) {
        vEventList = new ArrayList<>();
        runAsynchronous(link);
    }

    /**
     * returns all events from the ics file
     *
     * @return list of iCalendar.events
     */
    @NonNull
    public List<VEvent> getVEventList() {
        return vEventList;
    }

    /**
     * get the ics file asynchronous from the internet and parse it to an object
     *
     * @param link web link to the ics file
     */
    private void runAsynchronous(@NonNull String link) {
        Request request = new Request.Builder()
                .url(link)
                .build();

        new OkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful())
                        Log.e("ICS-Asynchronous", "Unexpected code " + response);
                    if (responseBody != null)
                        vEventList = parse(responseBody.string());
                    else
                        Log.e("ICS-Asynchronous", "No body");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public boolean isSuccessful() {
        return !vEventList.equals(new ArrayList<>());
    }
}
