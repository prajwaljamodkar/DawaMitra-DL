package com.example.dawamitra.models;

import java.util.ArrayList;
import java.util.UUID;

public class Dose {
    public String id;
    public String time;       // Display label e.g. "Morning 8:00 AM"
    public int hour;          // 0-23 for alarm scheduling
    public int minute;        // 0-59 for alarm scheduling
    public ArrayList<Medicine> medicines;

    public Dose() {
        this.id = UUID.randomUUID().toString();
        this.medicines = new ArrayList<>();
    }

    public Dose(String time, int hour, int minute, ArrayList<Medicine> medicines) {
        this.id = UUID.randomUUID().toString();
        this.time = time;
        this.hour = hour;
        this.minute = minute;
        this.medicines = (medicines != null) ? medicines : new ArrayList<>();
    }
}