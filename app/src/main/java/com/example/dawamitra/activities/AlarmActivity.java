package com.example.dawamitra.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
import android.os.*;
import android.speech.tts.TextToSpeech;
import android.view.*;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dawamitra.R;
import com.example.dawamitra.adapters.MedicineAdapter;
import com.example.dawamitra.data.DoseManager;
import com.example.dawamitra.models.Dose;
import com.example.dawamitra.models.Medicine;

import java.util.Locale;

public class AlarmActivity extends AppCompatActivity {

    MediaPlayer mediaPlayer;
    TextToSpeech tts;
    Handler handler = new Handler();
    Runnable voiceRunnable;

    private Dose currentDose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // FULL SCREEN + LOCK SCREEN
        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        );

        setContentView(R.layout.activity_alarm);

        Button takenBtn = findViewById(R.id.takenBtn);
        Button snoozeBtn = findViewById(R.id.snoozeBtn);
        TextView tvAlarmTime = findViewById(R.id.tvAlarmTime);
        LinearLayout medicineContainer = findViewById(R.id.alarmMedicineContainer);

        // Get dose info from intent
        String doseId = getIntent().getStringExtra("doseId");
        int doseHour = getIntent().getIntExtra("doseHour", -1);
        int doseMinute = getIntent().getIntExtra("doseMinute", -1);

        // Try to find dose by ID first, then by time
        if (doseId != null) {
            currentDose = DoseManager.findDoseById(this, doseId);
        }
        if (currentDose == null && doseHour >= 0) {
            currentDose = DoseManager.findDoseByTime(this, doseHour, doseMinute);
        }

        // Show dose time
        if (currentDose != null) {
            tvAlarmTime.setText(currentDose.time);
        }

        // Build medicine cards
        if (currentDose != null && currentDose.medicines != null) {
            for (Medicine med : currentDose.medicines) {
                View cardView = getLayoutInflater().inflate(
                        R.layout.item_alarm_medicine, medicineContainer, false);

                // Color circle
                View colorCircle = cardView.findViewById(R.id.alarmMedColor);
                if (med.color != null) {
                    GradientDrawable gd = new GradientDrawable();
                    gd.setShape(GradientDrawable.OVAL);
                    gd.setColor(Color.parseColor(med.color));
                    colorCircle.setBackground(gd);
                }

                // Icon
                ImageView icon = cardView.findViewById(R.id.alarmMedIcon);
                icon.setImageResource(MedicineAdapter.getIconForType(med.iconType));

                // Name
                TextView name = cardView.findViewById(R.id.alarmMedName);
                name.setText(med.name != null ? med.name : "Medicine");

                // Type
                TextView type = cardView.findViewById(R.id.alarmMedType);
                String typeText = med.iconType != null ? med.iconType : "tablet";
                type.setText(typeText.substring(0, 1).toUpperCase() + typeText.substring(1));

                medicineContainer.addView(cardView);
            }
        }

        // 🔊 LOUD ALARM SOUND LOOP
        try {
            mediaPlayer = MediaPlayer.create(this, android.provider.Settings.System.DEFAULT_ALARM_ALERT_URI);
            if (mediaPlayer != null) {
                mediaPlayer.setLooping(true);
                mediaPlayer.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 🗣️ TEXT TO SPEECH - speaks each medicine name
        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                tts.setLanguage(Locale.ENGLISH);

                voiceRunnable = new Runnable() {
                    @Override
                    public void run() {
                        if (currentDose != null && currentDose.medicines != null && !currentDose.medicines.isEmpty()) {
                            StringBuilder sb = new StringBuilder("Please take your medicine now. ");
                            for (Medicine med : currentDose.medicines) {
                                sb.append(med.name).append(", ");
                            }
                            tts.speak(sb.toString(), TextToSpeech.QUEUE_FLUSH, null, null);
                        } else {
                            tts.speak("Please take your medicine now", TextToSpeech.QUEUE_FLUSH, null, null);
                        }
                        handler.postDelayed(this, 15000);
                    }
                };

                handler.post(voiceRunnable);
            }
        });

        // ✅ TAKEN BUTTON
        takenBtn.setOnClickListener(v -> {
            stopAlarm();
            finish();
        });

        // 😴 SNOOZE BUTTON (5 MIN)
        snoozeBtn.setOnClickListener(v -> {
            snoozeAlarm();
            stopAlarm();
            finish();
        });
    }

    private void snoozeAlarm() {
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);

        Intent i = new Intent(this, com.example.dawamitra.ReminderReceiver.class);
        if (currentDose != null) {
            i.putExtra("doseId", currentDose.id);
            i.putExtra("doseHour", currentDose.hour);
            i.putExtra("doseMinute", currentDose.minute);
        }

        PendingIntent pi = PendingIntent.getBroadcast(
                this, 999, i, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        long trigger = System.currentTimeMillis() + (5 * 60 * 1000); // 5 min

        am.set(AlarmManager.RTC_WAKEUP, trigger, pi);
    }

    private void stopAlarm() {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.stop();
                mediaPlayer.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
            mediaPlayer = null;
        }

        if (tts != null) {
            tts.stop();
            tts.shutdown();
            tts = null;
        }

        if (handler != null && voiceRunnable != null) {
            handler.removeCallbacks(voiceRunnable);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopAlarm();
    }

    @Override
    public void onBackPressed() {
        // DO NOTHING → forces user to act
    }
}