package com.example.studentalarm.save;

import java.io.Serializable;

public class SaveRegularLectureSchedule implements Serializable{
    public SaveTime[] times;
    public SaveRegularLecture[] schedule;
    public int day, hour;

    public static class SaveRegularLecture implements Serializable{
        public String name, docent;
        public String[] rooms;
        public int id, color, activeRoomId;
    }

    public static class SaveTime implements Serializable {
        public int day, hour, room_id;
        public SaveRegularLecture saveRegularLecture;
    }
}
