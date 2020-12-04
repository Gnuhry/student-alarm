package com.example.studentalarm.save;

import java.io.Serializable;

public class SaveHour implements Serializable {
    public Save[] save;

    public static class Save implements Serializable {
        public int id;
        public String from, until;
    }
}
