package com.uph.finalproject.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.uph.finalproject.receiver.NotificationReceiver;


/**
 * Created by user on 10/26/2019.
 */

public class AlarmService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(this, "Notification On", Toast.LENGTH_LONG).show();
    }

    @Override
    public int onStartCommand(Intent intent, int id, int startID) {
        Log.e("Alarm Service", "Start");
        Intent i = new Intent(getApplicationContext(), NotificationReceiver.class);
        AlarmManager alarmManager=(AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, i, 0);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,System.currentTimeMillis(),1000, pendingIntent);

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}