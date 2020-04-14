package com.uph.finalproject;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.uph.finalproject.application.MyApplication;
import com.uph.finalproject.data.model.LoggedInUser;
import com.uph.finalproject.receiver.NotificationReceiver;
import com.uph.finalproject.service.AlarmService;
import com.uph.finalproject.ui.profile.ProfileFragment;
import com.uph.finalproject.ui.home.HomeFragment;
import com.uph.finalproject.ui.todo.ToDoFragment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    boolean doubleBackToExitPressedOnce = false;
    private DrawerLayout drawer;
    private TextView displayName;
    private LoggedInUser user;
    private MenuItem notificationOption;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();

        user = ((MyApplication) getApplication()).getLoggedInUser();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        final NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setCheckedItem(R.id.nav_home);

        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = (TextView) headerView.findViewById(R.id.nameDisplayNav);
        navUsername.setText(user.getDisplayName());


        this.getSupportFragmentManager().addOnBackStackChangedListener(
                new FragmentManager.OnBackStackChangedListener() {
                    public void onBackStackChanged() {
                        Fragment current = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                        if (current instanceof HomeFragment || current instanceof ToDoFragment) {
                            navigationView.setCheckedItem(R.id.nav_home);
                        } else {
                            navigationView.setCheckedItem(R.id.nav_profile);
                        }
                    }
                });
    }

    public void changeFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.addToBackStack(null).replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (getSupportFragmentManager().getBackStackEntryCount() != 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            case R.id.nav_home: {
                changeFragment(new HomeFragment());
                break;
            }
            case R.id.nav_profile: {
                changeFragment(new ProfileFragment());
                break;
            }
            case R.id.nav_logout: {
                AlertDialog.Builder altDial = new AlertDialog.Builder(MainActivity.this);
                altDial.setMessage("Do you want to logout?").setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ((MyApplication) getApplication()).setLoggedInUser(null);

                                SharedPreferences preferences = getSharedPreferences("checkbox", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putString("remember", "false");
                                editor.putString("username", "");
                                editor.putString("password", "");
                                editor.apply();

                                Intent i = new Intent(getApplicationContext(), NotificationReceiver.class);
                                i.setAction(user.getUserID());
                                Calendar calendar = Calendar.getInstance();
                                calendar.setTimeInMillis(System.currentTimeMillis());
                                AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                                PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, i, 0);
                                alarmManager.cancel(pendingIntent);
                                finish();

                                i = new Intent(getApplicationContext(), LoginActivity.class);
                                startActivity(i);
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                AlertDialog alert = altDial.create();
                alert.setTitle("Logout");
                alert.show();
                break;
            }

        }
        //close navigation drawer
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);
        SharedPreferences preferences = getSharedPreferences("notification"+user.getUserID(), Context.MODE_PRIVATE);
        menu.findItem(R.id.action_notification).setChecked(preferences.getBoolean("notification", false));
        return true;
    }

    @SuppressLint("ShortAlarm")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_notification:
                SharedPreferences preferences = getSharedPreferences("notification"+user.getUserID(), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                Intent i = new Intent(getApplicationContext(), NotificationReceiver.class);
                i.setAction(user.getUserID());
                AlarmManager alarmManager=(AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, i, 0);


                if(item.isChecked()) {
                    editor.putBoolean("notification", false);
                    item.setChecked(false);
                    alarmManager.cancel(pendingIntent);
                    Toast.makeText(getApplicationContext(), "Notification Off", Toast.LENGTH_SHORT).show();
                }else {
                    editor.putBoolean("notification", true);
                    item.setChecked(true);
                    alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,System.currentTimeMillis(),1000, pendingIntent);
                    Toast.makeText(getApplicationContext(), "Notification On", Toast.LENGTH_SHORT).show();
                }
                editor.apply();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
