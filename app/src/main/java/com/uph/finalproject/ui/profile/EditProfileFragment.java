package com.uph.finalproject.ui.profile;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.room.Room;

import com.uph.finalproject.GA;
import com.uph.finalproject.KeyboardControl;
import com.uph.finalproject.LoginActivity;
import com.uph.finalproject.MainActivity;
import com.uph.finalproject.R;
import com.uph.finalproject.application.MyApplication;
import com.uph.finalproject.data.factory.AppDatabase;
import com.uph.finalproject.data.model.LoggedInUser;
import com.uph.finalproject.data.model.User;
import com.uph.finalproject.data.model.User;
import com.uph.finalproject.data.model.User;
import com.uph.finalproject.data.repository.UserRepo;
import com.uph.finalproject.data.repository.UserRepo;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileFragment extends Fragment {
    private CardView btnConfirmEditProfile, btnDeleteAccount;
    private TextView inputName, inputEmail, inputUsername, inputPassword;
    private AppDatabase db;
    private LoggedInUser user;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.register_editprofile, container, false);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Edit Profile");
        db = Room.databaseBuilder(getActivity().getApplicationContext(), AppDatabase.class, ((MyApplication)getActivity().getApplication()).getDATABASE_NAME()).allowMainThreadQueries().build();
        user = ((MyApplication)getActivity().getApplication()).getLoggedInUser();
        btnConfirmEditProfile = view.findViewById(R.id.btnConfirmEditProfile);
        inputName= view.findViewById(R.id.inputName);
        inputEmail= view.findViewById(R.id.inputEmail);
        inputUsername= view.findViewById(R.id.inputUsername);
        inputPassword= view.findViewById(R.id.inputPass);
        btnDeleteAccount = view.findViewById(R.id.btnDeleteAccount);

        inputName.setText(user.getDisplayName());
        inputEmail.setText(user.getEmail());
        inputUsername.setText(user.getUserID());
        inputUsername.setFocusable(false);
        inputUsername.setClickable(false);
        inputPassword.setText(user.getPassword());

        btnConfirmEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isEveryfieldValid()){
                    KeyboardControl.hideKeyboard(view, getActivity());
                    User editUser = new User(user.getUserID(), inputPassword.getText().toString(), inputName.getText().toString(), inputEmail.getText().toString());
                    updateUser(editUser);
                }else{
                    Toast.makeText(getActivity().getApplicationContext(), "Please fill in valid data!", Toast.LENGTH_LONG).show();
                }

            }
        });

        btnDeleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder altDial = new AlertDialog.Builder(getActivity());
                altDial.setMessage("Are you sure to delete you account?").setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteUser(new User(user));
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                AlertDialog alert = altDial.create();
                alert.setTitle("Delete Account");
                alert.show();
            }
        });
        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {

                if(!isPasswordValid(inputPassword.getText().toString())){
                    inputPassword.setError("Password must contain > 5 characters! ");
                }
                if(!isEmailValid(inputEmail.getText().toString())){
                    inputEmail.setError("Please fill in valid email!");
                }
                if(inputName.getText().toString().equals("")) inputName.setError("Please fill in this field!");
            }
        };
        inputName.addTextChangedListener(afterTextChangedListener);
        inputEmail.addTextChangedListener(afterTextChangedListener);
        inputPassword.addTextChangedListener(afterTextChangedListener);
    }

    boolean isEveryfieldValid(){
        return isPasswordValid(inputPassword.getText().toString()) && isEmailValid(inputEmail.getText().toString()) && !inputName.getText().toString().equals("");
    }

    public boolean isEmailValid(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }

    void updateUser(final User User) {

        UserRepo apiService = GA.getClient().create(UserRepo.class);
        Call<User> callUser = apiService.updateUserToDB(User);
        callUser.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, final Response<User> response) {
                updateUserToRoom(response.body());
            }

            @Override
            public void onFailure(Call<User> call, final Throwable t) {
                Toast.makeText(getActivity().getApplicationContext(), t.getMessage() + "," + t.getCause(), Toast.LENGTH_LONG).show();
            }
        });

    }

    void updateUserToRoom(final User user) {

        new AsyncTask<Void, Void, User>() {
            @Override
            protected User doInBackground(Void... voids) {
                long status = db.UserDAO().updateUser(user);
                Log.e("Status", String.valueOf(status));
                return user;
            }

            @Override
            protected void onPostExecute(User user) {
                Toast.makeText(getActivity().getApplicationContext(), "User " + user.getUserID() + " updated succesfully! Please re-login!", Toast.LENGTH_SHORT).show();
                getActivity().finish();

                SharedPreferences preferences = getActivity().getSharedPreferences("checkbox", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("remember", "false");
                editor.putString("username", "");
                editor.putString("password", "");
                editor.apply();

                Intent i = new Intent(getActivity().getApplicationContext(), LoginActivity.class);
                startActivity(i);
            }
        }.execute();
    }

    void deleteUser(final User User) {
        UserRepo apiService = GA.getClient().create(UserRepo.class);
        Call<Integer> callUser = apiService.deleteUser("users/" + User.getUserID());
        callUser.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, final Response<Integer> response) {
                if (response.body() == 1) {
                    db.UserDAO().deleteUser(User);
                    Toast.makeText(getActivity().getApplicationContext(), "User " + User.getUserID() + " deleted successfully!", Toast.LENGTH_LONG).show();

                    getActivity().finish();

                    SharedPreferences preferences = getActivity().getSharedPreferences("checkbox", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("remember", "false");
                    editor.putString("username", "");
                    editor.putString("password", "");
                    editor.apply();

                    Intent i = new Intent(getActivity().getApplicationContext(), LoginActivity.class);
                    startActivity(i);
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "User NOT deleted!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Integer> call, final Throwable t) {
                Toast.makeText(getActivity().getApplicationContext(), t.getMessage() + "," + t.getCause(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
