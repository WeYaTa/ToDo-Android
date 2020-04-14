package com.uph.finalproject;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.os.Handler;
import android.util.Log;
import android.util.Patterns;
import android.widget.Toast;

import com.uph.finalproject.application.MyApplication;
import com.uph.finalproject.data.factory.AppDatabase;
import com.uph.finalproject.data.model.Board;
import com.uph.finalproject.data.model.LoggedInUser;
import com.uph.finalproject.data.model.ToDo;
import com.uph.finalproject.data.model.User;
import com.uph.finalproject.data.repository.BoardRepo;
import com.uph.finalproject.data.repository.ToDoRepo;
import com.uph.finalproject.data.repository.UserRepo;
import com.uph.finalproject.ui.login.LoginFragment;
import com.uph.finalproject.ui.login.LoadingFragment;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private boolean doubleBackToExitPressedOnce = false;
    private AppDatabase db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        SharedPreferences preferences = getSharedPreferences("checkbox", Context.MODE_PRIVATE);
        String rememberMe = preferences.getString("remember", "");
        String username = preferences.getString("username","");
        String pass = preferences.getString("password", "");
        if (rememberMe.equals("true")){
            getSupportFragmentManager().beginTransaction().replace(R.id.login_frame, new LoadingFragment()).commit();
            login(username, pass);
        }else{
            getSupportFragmentManager().beginTransaction().replace(R.id.login_frame, new LoginFragment()).commit();
        }
        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, ((MyApplication)getApplication()).getDATABASE_NAME()).allowMainThreadQueries().build();
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() != 0) {
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

    public void changeFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.addToBackStack(null).replace(R.id.login_frame, fragment);
        fragmentTransaction.commit();
    }

    public boolean isEmailValid(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }

    public void login(final String username, final String password){
        UserRepo apiService = GA.getClient().create(UserRepo.class);
        Call<User> call = apiService.getUserByUserID(username);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, final Response<User> response) {
                User user = (User) response.body();
                if (user != null) {
                    Log.e("User ga null", "mantep");

                    if (password.equals(user.getPassword())) {
                        Log.e("User ga null", "password sama");
                        LoggedInUser loggedInUser = new LoggedInUser(user.getUserID(), user.getDisplayName(), user.getEmail(), user.getPassword());
                        ((MyApplication)getApplication()).setLoggedInUser(loggedInUser);
                        Toast.makeText(getApplicationContext(), "Login successful!", Toast.LENGTH_LONG).show();

                        downloadEverythingRelatedTo(new User(loggedInUser));

                        //finish activity and go to home
                        Intent i = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(i);

                        finish();
                    } else {
                        Log.e("User ga null", "password beda");
                        Toast.makeText(LoginActivity.this, "Invalid username or password!", Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(LoginActivity.this, "Invalid username or password!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, final Throwable t) {
                Log.e("Error", t.getMessage() + "," + t.getCause());
                Toast.makeText(LoginActivity.this, t.getMessage() + "," + t.getCause(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void downloadEverythingRelatedTo(final User user){
//        db = Room.databaseBuilder(getActivity().getApplicationContext(), AppDatabase.class, ((MyApplication)getActivity().getApplication()).getDATABASE_NAME()).allowMainThreadQueries().build();

        putUserToRoom(user);

        BoardRepo apiService = GA.getClient().create(BoardRepo.class);
        //check if Board exists
        Call<List<Board>> call = apiService.getBoardsByUserID(user.getUserID());
        call.enqueue(new Callback<List<Board>>() {
            @Override
            public void onResponse(Call<List<Board>> call, final Response<List<Board>> response) {
                if (response.body() != null) {
                    for(Board board : response.body()){
                        putBoardToRoom(board);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Board>> call, final Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage() + "," + t.getCause(), Toast.LENGTH_LONG).show();
            }
        });

        ToDoRepo apiServiceToDo = GA.getClient().create(ToDoRepo.class);
        //check if Board exists
        Call<List<ToDo>> callToDo = apiServiceToDo.getToDosByUserID(user.getUserID());
        callToDo.enqueue(new Callback<List<ToDo>>() {
            @Override
            public void onResponse(Call<List<ToDo>> call, final Response<List<ToDo>> response) {
                if (response.body() != null) {
                    for(ToDo todo : response.body()){
                        putToDoToRoom(todo);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<ToDo>> call, final Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage() + "," + t.getCause(), Toast.LENGTH_LONG).show();
            }
        });
    }

    //todo
    public void putToDoToRoom(final ToDo todo) {
        new AsyncTask<Void, Void, ToDo>() {
            @Override
            protected ToDo doInBackground(Void... voids) {
                ToDo checkToDo = db.ToDoDAO().getToDoByID(todo.getTodoID());
                return checkToDo;
            }

            @Override
            protected void onPostExecute(ToDo checkToDo) {
                if (checkToDo == null) {
                    insertToDoToRoom(todo);
                }else{
                    Log.e("Room", "ToDo exists!");
                }
            }
        }.execute();
    }
    private void insertToDoToRoom(final ToDo todo) {

        new AsyncTask<Void, Void, Long>() {
            @Override
            protected Long doInBackground(Void... voids) {
                long status = db.ToDoDAO().insertToDo(todo);
                return status;
            }

            @Override
            protected void onPostExecute(Long status) {
                Log.e("Room", "ToDo inserted!");
            }
        }.execute();
    }

    //board
    public void putBoardToRoom(final Board board) {
        new AsyncTask<Void, Void, Board>() {
            @Override
            protected Board doInBackground(Void... voids) {
                Board checkBoard = db.BoardDAO().getBoardbyID(board.getBoardID());
                return checkBoard;
            }

            @Override
            protected void onPostExecute(Board checkBoard) {
                if (checkBoard == null) {
                    insertBoardToRoom(board);
                }else{
                    Log.e("Room", "Board exists!");
                }
            }
        }.execute();
    }
    private void insertBoardToRoom(final Board board) {

        new AsyncTask<Void, Void, Long>() {
            @Override
            protected Long doInBackground(Void... voids) {
                long status = db.BoardDAO().insertBoard(board);
                return status;
            }

            @Override
            protected void onPostExecute(Long status) {
                Log.e("Room", "Board inserted!");
            }
        }.execute();
    }

    //user
    private void updateUserToRoom(final User user) {
        new AsyncTask<Void, Void, Long>() {
            @Override
            protected Long doInBackground(Void... voids) {
                long status = db.UserDAO().updateUser(user);
                return status;
            }

            @Override
            protected void onPostExecute(Long status) {
                Log.e("Room", "User updated!");
            }
        }.execute();
    }
    public void putUserToRoom(final User user) {
        new AsyncTask<Void, Void, User>() {
            @Override
            protected User doInBackground(Void... voids) {
                User checkUser = db.UserDAO().getUserByUserID(user.getUserID());
                return checkUser;
            }

            @Override
            protected void onPostExecute(User checkUser) {
                if (checkUser == null) {
                    insertUserToRoom(user);

                }else{
                    updateUserToRoom(user);
                }
            }
        }.execute();
    }
    private void insertUserToRoom(final User user) {

        new AsyncTask<Void, Void, Long>() {
            @Override
            protected Long doInBackground(Void... voids) {
                long status = db.UserDAO().insertUser(user);
                return status;
            }

            @Override
            protected void onPostExecute(Long status) {
                Log.e("Room", "User inserted!");
            }
        }.execute();
    }

}
