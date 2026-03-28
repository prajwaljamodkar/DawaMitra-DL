package com.example.dawamitra.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.dawamitra.models.Dose;
import com.example.dawamitra.models.Medicine;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class DoseManager {

    private static final String PREFS_NAME = "dawamitra_doses";
    private static final String KEY_DOSES = "doses_json";
    private static final String KEY_FIRST_LAUNCH = "first_launch";

    public static void saveDoses(Context context, ArrayList<Dose> doses) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String json = new Gson().toJson(doses);
        prefs.edit().putString(KEY_DOSES, json).apply();
    }

    public static ArrayList<Dose> loadDoses(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(KEY_DOSES, null);

        if (json == null) return new ArrayList<>();

        Type type = new TypeToken<ArrayList<Dose>>() {}.getType();
        ArrayList<Dose> list = new Gson().fromJson(json, type);
        return (list != null) ? list : new ArrayList<>();
    }

    public static Dose findDoseById(Context context, String doseId) {
        ArrayList<Dose> doses = loadDoses(context);
        for (Dose d : doses) {
            if (d.id != null && d.id.equals(doseId)) {
                return d;
            }
        }
        return null;
    }

    public static Dose findDoseByTime(Context context, int hour, int minute) {
        ArrayList<Dose> doses = loadDoses(context);
        for (Dose d : doses) {
            if (d.hour == hour && d.minute == minute) {
                return d;
            }
        }
        return null;
    }

    public static void updateDose(Context context, Dose updatedDose) {
        ArrayList<Dose> doses = loadDoses(context);
        for (int i = 0; i < doses.size(); i++) {
            if (doses.get(i).id.equals(updatedDose.id)) {
                doses.set(i, updatedDose);
                break;
            }
        }
        saveDoses(context, doses);
    }

    public static void deleteDose(Context context, String doseId) {
        ArrayList<Dose> doses = loadDoses(context);
        for (int i = 0; i < doses.size(); i++) {
            if (doses.get(i).id.equals(doseId)) {
                doses.remove(i);
                break;
            }
        }
        saveDoses(context, doses);
    }

    public static boolean isFirstLaunch(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(KEY_FIRST_LAUNCH, true);
    }

    public static void setFirstLaunchDone(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(KEY_FIRST_LAUNCH, false).apply();
    }

    public static void seedInitialData(Context context) {
        ArrayList<Dose> doses = new ArrayList<>();

        // Morning dose
        ArrayList<Medicine> morningMeds = new ArrayList<>();
        morningMeds.add(new Medicine("Paracetamol", "#4CAF50", "tablet"));
        morningMeds.add(new Medicine("Vitamin C", "#FF9800", "capsule"));
        doses.add(new Dose("Morning 8:00 AM", 8, 0, morningMeds));

        // Afternoon dose
        ArrayList<Medicine> afternoonMeds = new ArrayList<>();
        afternoonMeds.add(new Medicine("Metformin", "#2196F3", "tablet"));
        doses.add(new Dose("Afternoon 2:00 PM", 14, 0, afternoonMeds));

        // Night dose
        ArrayList<Medicine> nightMeds = new ArrayList<>();
        nightMeds.add(new Medicine("Aspirin", "#E91E63", "tablet"));
        nightMeds.add(new Medicine("Calcium", "#9C27B0", "syrup"));
        doses.add(new Dose("Night 9:00 PM", 21, 0, nightMeds));

        saveDoses(context, doses);
    }
}
