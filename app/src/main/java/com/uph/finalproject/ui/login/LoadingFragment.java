package com.uph.finalproject.ui.login;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

import com.uph.finalproject.KeyboardControl;
import com.uph.finalproject.LoginActivity;
import com.uph.finalproject.R;
import com.uph.finalproject.application.MyApplication;
import com.uph.finalproject.data.factory.AppDatabase;


public class LoadingFragment extends Fragment {
    private EditText usernameEditText;
    private EditText passwordEditText;
    private CardView loginButton;
    private ProgressBar loadingProgressBar;
    private TextView register;
    private AppDatabase db;
    private CheckBox remember;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_loading, container, false);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }


}
