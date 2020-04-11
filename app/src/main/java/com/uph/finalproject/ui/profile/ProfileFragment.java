package com.uph.finalproject.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.room.Room;

import com.uph.finalproject.MainActivity;
import com.uph.finalproject.R;
import com.uph.finalproject.application.MyApplication;
import com.uph.finalproject.data.factory.AppDatabase;
import com.uph.finalproject.data.model.Board;
import com.uph.finalproject.data.model.LoggedInUser;
import com.uph.finalproject.data.model.ToDo;
import com.uph.finalproject.data.model.User;

public class ProfileFragment extends Fragment {
    private CardView btnEditProfile, btnDeleteAccount;
    private TextView displayName, displayEmail, displayUsername, displayPassword, boardQty, todoQty, inprogressQty;
    private AppDatabase db;
    private LoggedInUser user;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_profile, container, false);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Profile");

        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        displayName = view.findViewById(R.id.displayName);
        displayEmail = view.findViewById(R.id.displayEmail);
        displayUsername = view.findViewById(R.id.displayUsername);
        displayPassword = view.findViewById(R.id.displayPass);
        boardQty = view.findViewById(R.id.boardQty);
        todoQty = view.findViewById(R.id.todoQty);
        inprogressQty = view.findViewById(R.id.inprogressQty);

        user = ((MyApplication) getActivity().getApplication()).getLoggedInUser();
        db = Room.databaseBuilder(getActivity().getApplicationContext(), AppDatabase.class, ((MyApplication) getActivity().getApplication()).getDATABASE_NAME()).allowMainThreadQueries().build();

        ToDo todos[] = db.ToDoDAO().getToDosByUserID(user.getUserID());
        Board boards[] = db.BoardDAO().getBoardsByUserID(user.getUserID());

        boardQty.setText(String.valueOf(countBoards(boards)));
        todoQty.setText(String.valueOf(countToDo(todos)));
        inprogressQty.setText(String.valueOf(countInProgress(todos)));
        displayName.setText(user.getDisplayName());
        displayEmail.setText(user.getEmail());
        displayUsername.setText(user.getUserID());
        displayPassword.setText(passHide(user.getPassword()));

        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).changeFragment(new EditProfileFragment());
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        ToDo todos[] = db.ToDoDAO().getToDosByUserID(user.getUserID());
        Board boards[] = db.BoardDAO().getBoardsByUserID(user.getUserID());

        boardQty.setText(String.valueOf(countBoards(boards)));
        todoQty.setText(String.valueOf(countToDo(todos)));
        inprogressQty.setText(String.valueOf(countInProgress(todos)));
        inprogressQty.setText(String.valueOf(countInProgress(todos)));
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

    String passHide(String pass){
        String result ="";
        for (int i = 0; i < pass.length(); i++){
            char x = pass.charAt(i);
            if (i < pass.length() - 2){
                result += '*';
            }else{
                result += x;
            }
        }
        return result;
    }
}
