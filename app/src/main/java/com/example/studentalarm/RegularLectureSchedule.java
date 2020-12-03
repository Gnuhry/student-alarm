package com.example.studentalarm;

import android.graphics.Color;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class RegularLectureSchedule {

    private int days, hours;
    @NonNull
    private final List<RegularLecture> lectures;

    public RegularLectureSchedule(int days, int hours) {
        if (days < 4 || days > 6)
            days = 5;
        if (hours < 0 || hours > 23)
            hours = 10;
        lectures = new ArrayList<>();
        this.days = days;
        this.hours = hours;
    }

    public int getDays() {
        return days;
    }

    public int getHours() {
        return hours;
    }

    public void setDays(int days) {
        this.days = days;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    @NonNull
    public List<RegularLecture> getLectures() {
        return lectures;
    }

    public void addLecture(RegularLecture lecture) {
        lectures.add(lecture);
    }

    public static class RegularLecture {
        private String name, docent;
        @NonNull
        private final List<String> rooms;
        private final int id;
        private static int counter = 0;
        private int color = Color.RED, activeRoomId;

        public RegularLecture(String name) {
            id = counter++;
            rooms = new ArrayList<>();
            activeRoomId = -1;
            this.name = name;
        }

        public RegularLecture(String name, int id) {
            this.id = id;
            rooms = new ArrayList<>();
            activeRoomId = -1;
            this.name = name;
        }

        public static void setCounter(int new_counter) {
            counter = new_counter;
        }

        public void addRoom(String room) {
            if (activeRoomId == -1) activeRoomId = 0;
            rooms.add(room);
        }

        @NonNull
        public List<String> getRooms() {
            return rooms;
        }

        @NonNull
        public RegularLecture setAllRooms(@NonNull List<String> rooms) {
            this.rooms.clear();
            this.rooms.addAll(rooms);
            if (rooms.size() > 0)
                activeRoomId = 0;
            return this;
        }

        public String getName() {
            return name;
        }

        @NonNull
        public RegularLecture setName(String name) {
            this.name = name;
            return this;
        }

        public String getDocent() {
            return docent;
        }

        @NonNull
        public RegularLecture setDocent(String docent) {
            this.docent = docent;
            return this;
        }

        public int getId() {
            return id;
        }

        public int getColor() {
            return color;
        }

        @NonNull
        public RegularLecture setColor(int color) {
            this.color = color;
            return this;
        }

        public int getActiveRoomId() {
            return activeRoomId;
        }

        @NonNull
        public RegularLecture setActiveRoomId(int activeRoomId) {
            this.activeRoomId = activeRoomId;
            return this;
        }

        @Nullable
        public String getActiveRoom() {
            if (activeRoomId == -1 || activeRoomId >= rooms.size())
                return null;
            return rooms.get(activeRoomId);
        }

        public static class RegularLectureTime {
            public final int day, hour, room_id;
            public final RegularLecture lecture;

            public RegularLectureTime(int day, int hour, int room_id, RegularLecture lecture) {
                this.day = day;
                this.hour = hour;
                this.room_id = room_id;
                this.lecture = lecture;
            }
        }
    }

}
