package com.uph.finalproject.receiver;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.room.Room;

import com.uph.finalproject.LoginActivity;
import com.uph.finalproject.R;
import com.uph.finalproject.application.MyApplication;
import com.uph.finalproject.data.factory.AppDatabase;
import com.uph.finalproject.data.model.Board;
import com.uph.finalproject.data.model.LoggedInUser;
import com.uph.finalproject.data.model.ToDo;

/**
 * Created by user on 6/14/2018.
 */

public class NotificationReceiver extends BroadcastReceiver {

    private static final int NOTIFICATION_ID = 0;
    private static final String CHANNEL_ID = "chn";
    private ToDo todos[];
    private Board boards[];
    private AppDatabase db;
    private LoggedInUser user;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("Hello", "Receiver masuk");

        String username = intent.getAction();
        AppDatabase db = Room.databaseBuilder(context, AppDatabase.class, ((MyApplication) context.getApplicationContext()).getDATABASE_NAME()).allowMainThreadQueries().build();

        Log.e("Username", username);
        Board boards[] = db.BoardDAO().getBoardsByUserID(username);
        ToDo todos[] = db.ToDoDAO().getToDosByUserID(username);

        createNotificationChannel(context);

        // Create an explicit intent for an Activity in your app
        Intent i = new Intent(context, LoginActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, i, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_todo))
                .setSmallIcon(R.drawable.ic_todo)
                .setContentTitle("ToDos Are Waiting To Be Finished!")
//                .setContentText("You have " + boards.length + " boards, " + countToDo(todos) + " todos and " + countInProgress(todos) + " in-progress tasks waiting for you doing!")
                .setStyle(new NotificationCompat.BigTextStyle().bigText("You have " + boards.length + " boards, " + countToDo(todos) + " todos and " + countInProgress(todos) + " in-progress tasks waiting for you doing!"))
                .setPriority(NotificationCompat.PRIORITY_MAX)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    private void createNotificationChannel(Context context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getString(R.string.channel_name);
            String description = context.getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    int countToDo(ToDo todos[]) {
        int count = 0;
        for (ToDo todo : todos) {
            if (todo.getStatus().equals("todo")) count++;
        }
        return count;
    }

    int countInProgress(ToDo todos[]) {
        int count = 0;
        for (ToDo todo : todos) {
            if (todo.getStatus().equals("inprogress")) count++;
        }
        return count;
    }
}