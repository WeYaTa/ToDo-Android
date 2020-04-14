package com.uph.finalproject.ui.popup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.room.Room;

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
import android.widget.TextView;
import android.widget.Toast;

import com.uph.finalproject.GA;
import com.uph.finalproject.R;
import com.uph.finalproject.application.MyApplication;
import com.uph.finalproject.data.factory.AppDatabase;
import com.uph.finalproject.data.model.LoggedInUser;
import com.uph.finalproject.data.model.ToDo;
import com.uph.finalproject.data.model.ToDo;
import com.uph.finalproject.data.model.Board;
import com.uph.finalproject.data.repository.ToDoRepo;
import com.uph.finalproject.data.repository.BoardRepo;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddToDoPopUp extends Activity {

    private CardView btnConfirmEdit;
    private EditText displayDesc;
    private TextView title, txtBtn;
    private AppDatabase db;
    private LoggedInUser user;
    private int boardID ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.todo_pop_up);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels, height = dm.heightPixels;

        getWindow().setLayout((int)Math.round(width*.8), (int)Math.round(height*.4));
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        params.x = 0;
        params.y = -320;

        getWindow().setAttributes(params);

        user = ((MyApplication)getApplication()).getLoggedInUser();
        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, ((MyApplication)getApplication()).getDATABASE_NAME()).allowMainThreadQueries().build();

        title=findViewById(R.id.title);
        title.setText("Add New ToDo");
        txtBtn = findViewById(R.id.txtBtn);
        txtBtn.setText("Add ToDo");
        displayDesc = findViewById(R.id.editToDoDesc);
        btnConfirmEdit = findViewById(R.id.btnConfirmEditToDo);

        Intent i = getIntent();
        boardID = i.getIntExtra("boardID", 0);

        btnConfirmEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(displayDesc.getText().toString().equals("")){
                    displayDesc.setError("Please fill in this field");
                }else {
                    ToDo newToDo = new ToDo("todo", displayDesc.getText().toString(), boardID, user.getUserID());
                    addNewToDo(newToDo);
                }
            }
        });

    }

    void addNewToDo(final ToDo ToDo) {
        BoardRepo apiService = GA.getClient().create(BoardRepo.class);
        Call<Board> call = apiService.getBoardByID(boardID);
        call.enqueue(new Callback<Board>() {
            @Override
            public void onResponse(Call<Board> call, final Response<Board> response) {
                if (response.body() != null) {
                    //add new ToDo
                    ToDoRepo apiService = GA.getClient().create(ToDoRepo.class);
                    Call<ToDo> callToDo = apiService.postToDoToDB(ToDo);
                    callToDo.enqueue(new Callback<ToDo>() {
                        @Override
                        public void onResponse(Call<ToDo> call, final Response<ToDo> response) {
                            addNewToDoToRoom((ToDo)response.body());
                        }

                        @Override
                        public void onFailure(Call<ToDo> call, final Throwable t) {
                            Toast.makeText(getApplicationContext(), t.getMessage() + "," + t.getCause(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<Board> call, final Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage() + "," + t.getCause(), Toast.LENGTH_LONG).show();
            }
        });
    }

    void addNewToDoToRoom(final ToDo ToDo) {

        new AsyncTask<Void, Void, ToDo>() {
            @Override
            protected ToDo doInBackground(Void... voids) {
                long status = db.ToDoDAO().insertToDo(ToDo);
                Log.e("Status", String.valueOf(status));
                return ToDo;
            }

            @Override
            protected void onPostExecute(ToDo ToDo) {
                finish();
                Toast.makeText(AddToDoPopUp.this, "ToDo " + ToDo.getDescription() + " added succesfully!", Toast.LENGTH_SHORT).show();
            }
        }.execute();
    }
}
