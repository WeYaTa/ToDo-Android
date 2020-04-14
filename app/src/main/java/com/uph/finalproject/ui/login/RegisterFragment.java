package com.uph.finalproject.ui.login;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.uph.finalproject.GA;
import com.uph.finalproject.KeyboardControl;
import com.uph.finalproject.LoginActivity;
import com.uph.finalproject.R;
import com.uph.finalproject.data.factory.AppDatabase;
import com.uph.finalproject.data.model.User;
import com.uph.finalproject.data.repository.UserRepo;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterFragment extends Fragment {
    private CardView btnRegister;
    private TextView inputName, inputEmail, inputUsername, inputPassword;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.register_editprofile, container, false);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView tv = view.findViewById(R.id.textView);
        tv.setText("Register Account");

        btnRegister = view.findViewById(R.id.btnConfirmEditProfile);
        inputName = view.findViewById(R.id.inputName);
        inputEmail = view.findViewById(R.id.inputEmail);
        inputUsername = view.findViewById(R.id.inputUsername);
        inputPassword = view.findViewById(R.id.inputPass);
        CardView btnDeleteAccount = view.findViewById(R.id.btnDeleteAccount);
        btnDeleteAccount.setVisibility(View.GONE);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KeyboardControl.hideKeyboard(view,getActivity().getApplicationContext() );
                if(isEveryfieldValid()){
                    User newUser = new User(inputUsername.getText().toString(), inputPassword.getText().toString(), inputName.getText().toString(), inputEmail.getText().toString());
                    register(newUser);

                }else{
                    Toast.makeText(getActivity().getApplicationContext(), "Please fill in all the field correctly!", Toast.LENGTH_LONG).show();
                }
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
                if(inputUsername.getText().toString().equals("")) inputUsername.setError("Please fill in this field!");

                if(!((LoginActivity) getActivity()).isPasswordValid(inputPassword.getText().toString())){
                    inputPassword.setError("Password must contain > 5 characters! ");
                }
                if(!((LoginActivity) getActivity()).isEmailValid(inputEmail.getText().toString())){
                    inputEmail.setError("Please fill in valid email!");
                }
                if(inputName.getText().toString().equals("")) inputName.setError("Please fill in this field!");
            }
        };
        inputName.addTextChangedListener(afterTextChangedListener);
        inputEmail.addTextChangedListener(afterTextChangedListener);
        inputUsername.addTextChangedListener(afterTextChangedListener);
        inputPassword.addTextChangedListener(afterTextChangedListener);
    }

    boolean isEveryfieldValid(){
        return !inputUsername.getText().toString().equals("") && ((LoginActivity) getActivity()).isPasswordValid(inputPassword.getText().toString())
                && ((LoginActivity) getActivity()).isEmailValid(inputEmail.getText().toString()) && !inputName.getText().toString().equals("");
    }

    public void register(final User user){

        final UserRepo apiService = GA.getClient().create(UserRepo.class);

        //check if User exists
        Call<User> call = apiService.getUserByUserID(user.getUserID());
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, final Response<User> response) {
                if (response.body() != null) {
                    Toast.makeText(getActivity().getApplicationContext(), "Username taken !", Toast.LENGTH_LONG).show();
                    inputUsername.requestFocus();
                }else{
                    //register user
                    call = apiService.postUserToDB(user);
                    call.enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(Call<User> call, final Response<User> response) {
                            Toast.makeText(getActivity().getApplicationContext(),"You are registered successfully!" , Toast.LENGTH_LONG).show();
                            getActivity().onBackPressed();
                        }

                        @Override
                        public void onFailure(Call<User> call, final Throwable t) {
                            Toast.makeText(getActivity().getApplicationContext(), t.getMessage() + "," + t.getCause(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<User> call, final Throwable t) {
                Toast.makeText(getActivity().getApplicationContext(), t.getMessage() + "," + t.getCause(), Toast.LENGTH_LONG).show();
            }
        });
    }

}
