package com.uph.finalproject.ui.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.room.Room;

import com.uph.finalproject.GA;
import com.uph.finalproject.KeyboardControl;
import com.uph.finalproject.LoginActivity;
import com.uph.finalproject.MainActivity;
import com.uph.finalproject.application.MyApplication;
import com.uph.finalproject.R;
import com.uph.finalproject.data.factory.AppDatabase;
import com.uph.finalproject.data.model.Board;
import com.uph.finalproject.data.model.LoggedInUser;
import com.uph.finalproject.data.model.ToDo;
import com.uph.finalproject.data.model.User;
import com.uph.finalproject.data.repository.BoardRepo;
import com.uph.finalproject.data.repository.ToDoRepo;
import com.uph.finalproject.data.repository.UserRepo;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginFragment extends Fragment {
    private EditText usernameEditText;
    private EditText passwordEditText;
    private CardView loginButton;
    private ProgressBar loadingProgressBar;
    private TextView register;
    private AppDatabase db;
    private CheckBox remember;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_login, container, false);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = Room.databaseBuilder(getActivity().getApplicationContext(), AppDatabase.class, ((MyApplication)getActivity().getApplication()).getDATABASE_NAME()).allowMainThreadQueries().build();
        usernameEditText = view.findViewById(R.id.username);
        passwordEditText = view.findViewById(R.id.password);
        loginButton = view.findViewById(R.id.login);
        loadingProgressBar = view.findViewById(R.id.loading);
        register = view.findViewById(R.id.register);
        remember = view.findViewById(R.id.remember);

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
                if(usernameEditText.getText().toString().equals("")) usernameEditText.setError("Please fill in this field!");

                if(!((LoginActivity) getActivity()).isPasswordValid(passwordEditText.getText().toString())){
                    passwordEditText.setError("Password must contain > 5 characters! ");
                }
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(((LoginActivity) getActivity()).isPasswordValid(passwordEditText.getText().toString()) && !usernameEditText.getText().toString().equals("")){
                    SharedPreferences preferences = getActivity().getSharedPreferences("checkbox", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("username", usernameEditText.getText().toString());
                    editor.putString("password",passwordEditText.getText().toString());
                    editor.apply();

                    KeyboardControl.hideKeyboard(v, getActivity().getApplicationContext());
                    loadingProgressBar.setVisibility(View.VISIBLE);
                    ((LoginActivity)getActivity()).login(usernameEditText.getText().toString(), passwordEditText.getText().toString());
                    loadingProgressBar.setVisibility(View.INVISIBLE);
                }else{
                    Toast.makeText(getActivity().getApplicationContext(), "Please fill in valid data!", Toast.LENGTH_LONG).show();
                }

            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((LoginActivity) getActivity()).changeFragment(new RegisterFragment());
            }
        });

        remember.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences preferences = getActivity().getSharedPreferences("checkbox", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();

                if(buttonView.isChecked()){
                    editor.putString("remember", "true");

                    Toast.makeText(getActivity(), "Checked", Toast.LENGTH_SHORT).show();
                }else{
                    editor.putString("remember", "false");
                    Toast.makeText(getActivity(), "Unchecked", Toast.LENGTH_SHORT).show();
                }

                editor.apply();
            }
        });
    }


}
