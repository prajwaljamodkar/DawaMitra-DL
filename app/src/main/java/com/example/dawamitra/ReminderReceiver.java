package com.example.dawamitra;

import android.content.*;
import com.example.dawamitra.activities.AlarmActivity;

public class ReminderReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Intent i = new Intent(context, AlarmActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Pass dose info to AlarmActivity
        if (intent != null) {
            String doseId = intent.getStringExtra("doseId");
            int doseHour = intent.getIntExtra("doseHour", -1);
            int doseMinute = intent.getIntExtra("doseMinute", -1);

            if (doseId != null) {
                i.putExtra("doseId", doseId);
            }
            i.putExtra("doseHour", doseHour);
            i.putExtra("doseMinute", doseMinute);
        }

        context.startActivity(i);
    }
}