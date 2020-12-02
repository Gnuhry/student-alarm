package com.example.studentalarm;

import android.graphics.Color;

import java.util.ArrayList;
import java.util.List;

public class RegularLectureSchedule {

    private int days, hours;
    private List<RegularLecture> lectures;

    public RegularLectureSchedule(int days, int hours) {
        //Check day hour
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

    public List<RegularLecture> getLectures() {
        return lectures;
    }

    public void addLecture(RegularLecture lecture) {
        lectures.add(lecture);
    }

    public static class RegularLecture {
        private String name, docent;
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

        public static void setCounter(int new_counter){
            counter=new_counter;
        }

        public RegularLecture addRoom(String room) {
            if(activeRoomId==-1) activeRoomId=0;
            rooms.add(room);
            return this;
        }

        public List<String> getRooms() {
            return rooms;
        }

        public String getName() {
            return name;
        }

        public RegularLecture setName(String name) {
            this.name = name;
            return this;
        }

        public String getDocent() {
            return docent;
        }

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

        public RegularLecture setColor(int color) {
            this.color = color;
            return this;
        }

        public int getActiveRoomId() {
            return activeRoomId;
        }

        public RegularLecture setActiveRoomId(int activeRoomId) {
            this.activeRoomId = activeRoomId;
            return this;
        }

        public String getActiveRoom() {
            if(activeRoomId==-1)
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
