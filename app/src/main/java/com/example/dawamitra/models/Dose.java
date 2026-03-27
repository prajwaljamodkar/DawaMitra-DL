package com.example.dawamitra.models;

import java.util.ArrayList;

public class Dose {
    public String time;
    public ArrayList<String> medicines;

    public Dose(String time, ArrayList<String> medicines) {
        this.time = time;
        this.medicines = medicines;
    }
}