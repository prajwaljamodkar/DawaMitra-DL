package com.example.dawamitra;

import android.content.*;
import com.example.dawamitra.activities.AlarmActivity;

public class ReminderReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Intent i = new Intent(context, AlarmActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }
}