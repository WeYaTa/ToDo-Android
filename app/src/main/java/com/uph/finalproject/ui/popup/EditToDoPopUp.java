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
import android.widget.Toast;

import com.uph.finalproject.GA;
import com.uph.finalproject.R;
import com.uph.finalproject.application.MyApplication;
import com.uph.finalproject.data.factory.AppDatabase;
import com.uph.finalproject.data.model.LoggedInUser;
import com.uph.finalproject.data.model.ToDo;
import com.uph.finalproject.data.repository.ToDoRepo;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditToDoPopUp extends Activity {

    private CardView btnConfirmEdit;
    private EditText displayDesc;
    private Intent i;
    private AppDatabase db;
    private LoggedInUser user;

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

        user = ((MyApplication) getApplication()).getLoggedInUser();
        db = Room.databaseBuilder(EditToDoPopUp.this, AppDatabase.class, ((MyApplication) getApplication()).getDATABASE_NAME()).allowMainThreadQueries().build();
        i = getIntent();
        int id = i.getIntExtra("todoID", 0);
        String desc = i.getStringExtra("todoDesc");
        String status = i.getStringExtra("todoStatus");
        int boardID = i.getIntExtra("boardID", 0);
        final ToDo editToDo = new ToDo(id, status,desc,boardID, user.getUserID());

        displayDesc = findViewById(R.id.editToDoDesc);
        displayDesc.setText(desc);

        btnConfirmEdit = findViewById(R.id.btnConfirmEditToDo);
        btnConfirmEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(displayDesc.getText().toString().equals("")){
                    displayDesc.setError("Please fill in this field");
                }else {
                    editToDo.setDescription(displayDesc.getText().toString());
                    updateToDo(editToDo);
                }
            }
        });

    }

    void updateToDo(final ToDo ToDo) {

        ToDoRepo apiService = GA.getClient().create(ToDoRepo.class);
        Call<ToDo> callToDo = apiService.updateToDoToDB(ToDo);
        callToDo.enqueue(new Callback<ToDo>() {
            @Override
            public void onResponse(Call<ToDo> call, final Response<ToDo> response) {
                updateToDoToRoom(response.body());
            }

            @Override
            public void onFailure(Call<ToDo> call, final Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage() + "," + t.getCause(), Toast.LENGTH_LONG).show();
            }
        });

    }

    void updateToDoToRoom(final ToDo ToDo) {

        new AsyncTask<Void, Void, ToDo>() {
            @Override
            protected ToDo doInBackground(Void... voids) {
                long status = db.ToDoDAO().updateToDo(ToDo);
                Log.e("Status", String.valueOf(status));
                return ToDo;
            }

            @Override
            protected void onPostExecute(ToDo ToDo) {
                finish();
                Toast.makeText(EditToDoPopUp.this, "ToDo " + ToDo.getDescription() + " updated succesfully!", Toast.LENGTH_SHORT).show();
            }
        }.execute();
    }
}
