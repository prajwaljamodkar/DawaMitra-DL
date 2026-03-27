package com.example.dawamitra.activities;

import android.media.MediaPlayer;
import android.os.*;
import android.speech.tts.TextToSpeech;
import android.view.WindowManager;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dawamitra.R;

import java.util.Locale;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.*;
import android.speech.tts.TextToSpeech;
import android.view.WindowManager;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class AlarmActivity extends AppCompatActivity {

    MediaPlayer mediaPlayer;
    TextToSpeech tts;
    Handler handler = new Handler();

    Runnable voiceRunnable;

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

        // 🔊 LOUD ALARM SOUND LOOP
        mediaPlayer = MediaPlayer.create(this, android.provider.Settings.System.DEFAULT_ALARM_ALERT_URI);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

        // 🗣️ TEXT TO SPEECH SETUP
        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                tts.setLanguage(Locale.ENGLISH);

                // REPEAT VOICE EVERY 10 SEC
                voiceRunnable = new Runnable() {
                    @Override
                    public void run() {
                        tts.speak("Please take your medicine now", TextToSpeech.QUEUE_FLUSH, null, null);
                        handler.postDelayed(this, 10000);
                    }
                };

                handler.post(voiceRunnable);
            }
        });

        // ✅ TAKEN BUTTON (STOP EVERYTHING)
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

        PendingIntent pi = PendingIntent.getBroadcast(
                this, 999, i, PendingIntent.FLAG_IMMUTABLE
        );

        long trigger = System.currentTimeMillis() + (5 * 60 * 1000); // 5 min

        am.set(AlarmManager.RTC_WAKEUP, trigger, pi);
    }

    private void stopAlarm() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }

        if (tts != null) {
            tts.stop();
            tts.shutdown();
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

    // 🚫 DISABLE BACK BUTTON (CRUCIAL)
    @Override
    public void onBackPressed() {
        // DO NOTHING → forces user to act
    }
}