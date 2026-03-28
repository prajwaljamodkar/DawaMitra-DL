package com.example.dawamitra.models;

import java.util.UUID;

public class Medicine {
    public String id;
    public String name;
    public String color;      // Hex color e.g. "#FF5722"
    public String iconType;   // "tablet", "capsule", "syrup", "injection", "drops"

    public Medicine() {
        this.id = UUID.randomUUID().toString();
    }

    public Medicine(String name, String color, String iconType) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.color = color;
        this.iconType = iconType;
    }
}
