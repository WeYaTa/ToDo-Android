package com.uph.finalproject.ui.popup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.room.Room;

import com.uph.finalproject.GA;
import com.uph.finalproject.R;
import com.uph.finalproject.application.MyApplication;
import com.uph.finalproject.data.factory.AppDatabase;
import com.uph.finalproject.data.model.Board;
import com.uph.finalproject.data.model.LoggedInUser;
import com.uph.finalproject.data.model.User;
import com.uph.finalproject.data.repository.BoardRepo;
import com.uph.finalproject.data.repository.UserRepo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import yuku.ambilwarna.AmbilWarnaDialog;

public class EditBoardPopUp extends Activity {

    private Button btnChangeColor;
    private CardView btnConfirmEdit;
    private View displayColor;
    private EditText displayName;
    private int setColor;
    private Intent i;
    private AppDatabase db;
    private LoggedInUser user;
    private Board editBoard;
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

        i = getIntent();
        int boardID = i.getIntExtra("boardID", 0);
        String boardName = i.getStringExtra("boardName");
        String boardColor = i.getStringExtra("boardColor");
        user = ((MyApplication) getApplication()).getLoggedInUser();
        db = Room.databaseBuilder(EditBoardPopUp.this, AppDatabase.class, ((MyApplication) getApplication()).getDATABASE_NAME()).allowMainThreadQueries().build();

        editBoard = new Board(boardID,boardName,boardColor, user.getUserID());
        displayName = findViewById(R.id.editName);
        displayName.setText(boardName);

//       setColor = board.getBackgroundColor();
        setColor = Color.parseColor(boardColor);
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

        btnConfirmEdit = findViewById(R.id.btnConfirmEdit);
        btnConfirmEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(displayName.getText().toString().equals("")){
                    displayName.setError("Please fill in this field");
                }else {
                    editBoard.setBoardName(displayName.getText().toString());
                    editBoard.setBackgroundColor(String.format("#%06X", 0xFFFFFF & setColor));
                    updateBoard(editBoard);
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

    void updateBoard(final Board board) {

        BoardRepo apiService = GA.getClient().create(BoardRepo.class);
        Call<Board> callBoard = apiService.updateBoardToDB(board);
        callBoard.enqueue(new Callback<Board>() {
            @Override
            public void onResponse(Call<Board> call, final Response<Board> response) {
                updateBoardToRoom(response.body());
            }

            @Override
            public void onFailure(Call<Board> call, final Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage() + "," + t.getCause(), Toast.LENGTH_LONG).show();
            }
        });

    }

    void updateBoardToRoom(final Board board) {

        new AsyncTask<Void, Void, Board>() {
            @Override
            protected Board doInBackground(Void... voids) {
                long status = db.BoardDAO().updateBoard(board);
                Log.e("Status", String.valueOf(status));
                return board;
            }

            @Override
            protected void onPostExecute(Board board) {
                finish();
                Toast.makeText(EditBoardPopUp.this, "Board " + board.getBoardName() + " updated succesfully!", Toast.LENGTH_SHORT).show();
            }
        }.execute();
    }
}
