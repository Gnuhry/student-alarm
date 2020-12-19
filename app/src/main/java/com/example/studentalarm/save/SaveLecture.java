package com.example.studentalarm.save;

import java.io.Serializable;
import java.util.Date;

import androidx.annotation.Nullable;

public class SaveLecture implements Serializable {
    public Save[][] saves;

    public static class Save implements Serializable {
        @Nullable
        public String docent, location, name;
        public Date start, end;
        public int color;
        public int id;
        public boolean isImport, isAllDayEvent;
    }
}
