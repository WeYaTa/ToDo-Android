package com.uph.finalproject.service;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;
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
 * Created by user on 10/26/2019.
 */

public class NotificationService extends IntentService {
    private static final int NOTIFICATION_ID = 0;
    private static final String CHANNEL_ID = "chn";
    private ToDo todos[];
    private Board boards[];
    private AppDatabase db;
    private LoggedInUser user;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public NotificationService() {
        super(null);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(this, "Service onCreate", Toast.LENGTH_LONG).show();
        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, ((MyApplication)getApplication()).getDATABASE_NAME()).allowMainThreadQueries().build();
        user = ((MyApplication)getApplication()).getLoggedInUser();
        todos = db.ToDoDAO().getToDosByUserID(user.getUserID());
        boards = db.BoardDAO().getBoardsByUserID(user.getUserID());
    }



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Toast.makeText(this, "Notification On.", Toast.LENGTH_LONG).show();

        createNotificationChannel();

        // Create an explicit intent for an Activity in your app
        Intent i = new Intent(this, LoginActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_todo)
                .setContentTitle("ToDos Are Waiting To Be Finished!")
                .setStyle(new NotificationCompat.BigTextStyle().bigText("You have "+countBoards(boards)+" boards, "+countToDo(todos)+" todos, and "+countInProgress(todos)+" in-progress tasks!"))
                .setPriority(NotificationCompat.PRIORITY_MAX)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


    int countBoards(Board boards[]){
        int count = 0;
        for (Board board: boards) {
            count++;
        }
        return count;
    }

    int countToDo(ToDo todos[]){
        int count = 0;
        for (ToDo todo: todos) {
            if(todo.getStatus().equals("todo")) count++;
        }
        return count;
    }

    int countInProgress(ToDo todos[]){
        int count = 0;
        for (ToDo todo: todos) {
            if(todo.getStatus().equals("inprogress")) count++;
        }
        return count;
    }
}