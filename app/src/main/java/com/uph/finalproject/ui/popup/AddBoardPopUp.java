package com.uph.finalproject.ui.popup;

import androidx.cardview.widget.CardView;
import androidx.room.Room;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.uph.finalproject.GA;
import com.uph.finalproject.KeyboardControl;
import com.uph.finalproject.application.MyApplication;
import com.uph.finalproject.R;
import com.uph.finalproject.data.factory.AppDatabase;
import com.uph.finalproject.data.model.Board;
import com.uph.finalproject.data.model.LoggedInUser;
import com.uph.finalproject.data.model.User;
import com.uph.finalproject.data.repository.BoardRepo;
import com.uph.finalproject.data.repository.UserRepo;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import yuku.ambilwarna.AmbilWarnaDialog;

public class AddBoardPopUp extends Activity {
    private LoggedInUser user;
    private Button btnChangeColor;
    private CardView btnAddBoard;
    private View displayColor;
    private EditText displayName;
    private TextView title, txtBtn;
    private int setColor;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.board_pop_up);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels, height = dm.heightPixels;

        getWindow().setLayout((int) Math.round(width * .8), (int) Math.round(height * .45));
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        params.x = 0;
        params.y = -320;

        getWindow().setAttributes(params);

        user = ((MyApplication) getApplication()).getLoggedInUser();
        db = Room.databaseBuilder(AddBoardPopUp.this, AppDatabase.class, ((MyApplication) getApplication()).getDATABASE_NAME()).allowMainThreadQueries().build();

        title = findViewById(R.id.title);
        title.setText("Add New Board");

        txtBtn = findViewById(R.id.txtBtn);
        txtBtn.setText("Add Board");

        displayName = findViewById(R.id.editName);

//       setColor = board.getBackgroundColor();
        setColor = getBaseContext().getResources().getColor(R.color.colorPrimaryDark);
        //set board default color
        displayColor = findViewById(R.id.displayColor);
        displayColor.setBackgroundColor(setColor);

        btnChangeColor = findViewById(R.id.btnChangeColor);
        btnChangeColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openColorPicker();
            }
        });

        btnAddBoard = findViewById(R.id.btnConfirmEdit);
        btnAddBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //add board to database
                KeyboardControl.hideKeyboard(getWindow().getDecorView().findViewById(android.R.id.content), getApplicationContext());

                if(displayName.getText().toString().equals("")){
                    displayName.setError("Please fill in this field");
                }else{
                    Board newBoard = new Board(displayName.getText().toString(), String.format("#%06X", 0xFFFFFF & setColor), user.getUserID());
                    addNewBoard(newBoard);
                }

            }
        });

    }

    void openColorPicker() {
        AmbilWarnaDialog colorPicker = new AmbilWarnaDialog(this, setColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {

            }

            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                setColor = color;
                displayColor.setBackgroundColor(setColor);
            }
        });
        colorPicker.show();
    }

    void addNewBoard(final Board board) {
        UserRepo apiService = GA.getClient().create(UserRepo.class);
        Call<User> call = apiService.getUserByUserID("users/" + user.getUserID());
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, final Response<User> response) {
                if (response.body() != null) {
                    //add new board
                    BoardRepo apiService = GA.getClient().create(BoardRepo.class);
                    Call<Board> callBoard = apiService.postBoardToDB(board);
                    callBoard.enqueue(new Callback<Board>() {
                        @Override
                        public void onResponse(Call<Board> call, final Response<Board> response) {
                            addNewBoardToRoom((Board)response.body());
                        }

                        @Override
                        public void onFailure(Call<Board> call, final Throwable t) {
                            Toast.makeText(getApplicationContext(), t.getMessage() + "," + t.getCause(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<User> call, final Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage() + "," + t.getCause(), Toast.LENGTH_LONG).show();
            }
        });
    }

    void addNewBoardToRoom(final Board board) {

        new AsyncTask<Void, Void, Board>() {
            @Override
            protected Board doInBackground(Void... voids) {
                long status = db.BoardDAO().insertBoard(board);
                Log.e("Status", String.valueOf(status));
                return board;
            }

            @Override
            protected void onPostExecute(Board board) {
                finish();
                Toast.makeText(AddBoardPopUp.this, "Board " + board.getBoardName() + " added succesfully!", Toast.LENGTH_SHORT).show();
            }
        }.execute();
    }
}
