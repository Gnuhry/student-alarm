package com.example.studentalarm;

import android.os.Bundle;

import com.alamkanak.weekview.WeekView;

import java.text.SimpleDateFormat;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    WeekView.SimpleAdapter<Lecture_Schedule.Lecture> adapter;
    SimpleDateFormat format = new SimpleDateFormat("EEE dd.MM");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        WeekView weekview = findViewById(R.id.weekView);
        adapter = new WeekView.SimpleAdapter<>();
        weekview.setAdapter(adapter);
        weekview.setTimeFormatter(hour -> {
            if (hour < 10)
                return "0" + hour + " h";
            return "" + hour + " h";
        });
        weekview.setDateFormatter(date -> format.format(date.getTime()));
        adapter.submit(Lecture_Schedule.Load(this).getAllLecture());
        Thread x = new Thread(() -> {
            Lecture_Schedule l = Import.ICSImport("http://vorlesungsplan.dhbw-mannheim.de/ical.php?uid=7758001", this);
            adapter.submit(l.getAllLecture());
        });
        x.start();
    }
}