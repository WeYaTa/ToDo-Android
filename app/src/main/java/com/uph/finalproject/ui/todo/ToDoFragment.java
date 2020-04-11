package com.uph.finalproject.ui.todo;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.uph.finalproject.GA;
import com.uph.finalproject.R;
import com.uph.finalproject.adapter.AdapterToDoRecyclerView;
import com.uph.finalproject.application.MyApplication;
import com.uph.finalproject.data.factory.AppDatabase;
import com.uph.finalproject.data.model.Board;
import com.uph.finalproject.data.model.ToDo;
import com.uph.finalproject.data.repository.BoardRepo;
import com.uph.finalproject.data.repository.ToDoRepo;
import com.uph.finalproject.ui.popup.AddToDoPopUp;
import com.uph.finalproject.ui.popup.EditToDoPopUp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ToDoFragment extends Fragment {

    private Board board;
    private RecyclerView todoRV;
    private RecyclerView.Adapter rvToDoAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<ToDo> todoList;
    private FloatingActionButton fab;
    private SwipeRefreshLayout refresh;
    private AppDatabase db;
    private String titleDesc = "aaaaaItem&&&&&Title";

    public ToDoFragment(Board board) {
        this.board = board;
        todoList = new ArrayList<>();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_todo, container, false);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(board.getBoardName() + " ToDo List");

        db = Room.databaseBuilder(getActivity().getApplicationContext(), AppDatabase.class, ((MyApplication) getActivity().getApplication()).getDATABASE_NAME()).allowMainThreadQueries().build();
        refresh = view.findViewById(R.id.swipeRefreshLayout);
        fab = view.findViewById(R.id.fab);
        todoRV = view.findViewById(R.id.todo_rv);
        todoRV.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        todoRV.setLayoutManager(layoutManager);

        rvToDoAdapter = new AdapterToDoRecyclerView(todoList, getActivity().getApplicationContext());
        todoRV.setAdapter(rvToDoAdapter);

        resetToDos();

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(todoRV);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //popup
                Intent i = new Intent(getActivity().getApplicationContext(), AddToDoPopUp.class);
                i.putExtra("boardID", board.getBoardID());
                startActivity(i);
            }
        });


        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                resetToDos();
            }
        });
    }

    void resetToDos() {
        todoList.clear();
        todoList.addAll(Arrays.asList(db.ToDoDAO().getToDosByBoardID(board.getBoardID())));

        todoList.add(new ToDo("todo", titleDesc));
        todoList.add(new ToDo("inprogress", titleDesc));
        todoList.add(new ToDo("finished", titleDesc));
        sortToDoAndTitle();
        todoRV.getAdapter().notifyDataSetChanged();
        refresh.setRefreshing(false);
    }

    public void sortToDoAndTitle() {
        Collections.sort(todoList, new Comparator() {

            public int compare(Object o1, Object o2) {

                String x1 = ((ToDo) o1).getStatus().toLowerCase();
                String x2 = ((ToDo) o2).getStatus().toLowerCase();
                int sComp = x2.compareTo(x1);

                if (sComp != 0) {
                    return sComp;
                }

                String x = ((ToDo) o1).getDescription().toLowerCase();
                String y = ((ToDo) o2).getDescription().toLowerCase();
                return x.compareTo(y);
            }
        });
    }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        private boolean mOrderChanged;
        ToDo deletedToDo = null;
        ToDo updateTodo;

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            int fromPosition = viewHolder.getAdapterPosition();
            int toPosition = target.getAdapterPosition();


            if (toPosition != 0) {
                updateTodo = todoList.get(fromPosition);
                String statusFrom = todoList.get(fromPosition).getStatus();
                String statusTo = todoList.get(toPosition).getStatus();
                String statusTo_OneRowBefore = todoList.get(toPosition - 1).getStatus();


                if (!statusFrom.equals(statusTo)) updateTodo.setStatus(statusTo);
                else updateTodo.setStatus(statusTo_OneRowBefore);

                Collections.swap(todoList, fromPosition, toPosition);
                todoRV.getAdapter().notifyItemMoved(fromPosition, toPosition);
                mOrderChanged = true;
            }

            return false;
        }

        @Override
        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
            super.onSelectedChanged(viewHolder, actionState);

            if (actionState == ItemTouchHelper.ACTION_STATE_IDLE && mOrderChanged) {
                //update change to database
                updateToDo(updateTodo);
                todoRV.getAdapter().notifyDataSetChanged();
                mOrderChanged = false;
            }
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            switch (direction) {

                case ItemTouchHelper.LEFT:
                    final int position = viewHolder.getAdapterPosition();
                    deletedToDo = todoList.get(position);
                    todoList.remove(position);
                    rvToDoAdapter.notifyItemRemoved(position);
                    Snackbar.make(fab, "Deleted " + deletedToDo.getDescription(), Snackbar.LENGTH_LONG).setAction("Undo", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            todoList.add(position, deletedToDo);
                            rvToDoAdapter.notifyItemInserted(position);
                        }
                    }).addCallback(new Snackbar.Callback() {
                        @Override
                        public void onDismissed(Snackbar snackbar, int dismissType) {
                            super.onDismissed(snackbar, dismissType);

                            if (dismissType == DISMISS_EVENT_TIMEOUT || dismissType == DISMISS_EVENT_SWIPE
                                    || dismissType == DISMISS_EVENT_CONSECUTIVE || dismissType == DISMISS_EVENT_MANUAL) {
                                //delete from db
                                deleteToDo(deletedToDo);
                            }

                        }
                    }).show();
                    break;

                case ItemTouchHelper.RIGHT:
                    final int position_r = viewHolder.getAdapterPosition();
                    rvToDoAdapter.notifyItemChanged(position_r);

                    //popup
                    Intent i = new Intent(getActivity().getApplicationContext(), EditToDoPopUp.class);
                    i.putExtra("todoID", todoList.get(position_r).getTodoID());
                    i.putExtra("todoDesc", todoList.get(position_r).getDescription());
                    i.putExtra("todoStatus", todoList.get(position_r).getStatus());
                    i.putExtra("boardID", todoList.get(position_r).getBoardID());
                    startActivity(i);

                    break;
            }
        }

        @Override
        public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
            if (viewHolder instanceof AdapterToDoRecyclerView.ViewHolderTitle) return 0;
            return super.getMovementFlags(recyclerView, viewHolder);
        }

        @Override
        public int getSwipeDirs(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
            if (viewHolder instanceof AdapterToDoRecyclerView.ViewHolderTitle) return 0;
            return super.getSwipeDirs(recyclerView, viewHolder);
        }


        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            new RecyclerViewSwipeDecorator.Builder(getActivity().getApplicationContext(), c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.colorSwipeDelete))
                    .addSwipeLeftActionIcon(R.drawable.ic_delete_sweep)
                    .addSwipeLeftLabel("Delete")
                    .setSwipeLeftActionIconTint(Color.WHITE)
                    .setSwipeLeftLabelColor(Color.WHITE)
                    .setSwipeLeftLabelTextSize(TypedValue.COMPLEX_UNIT_SP, 20)
                    .addSwipeRightBackgroundColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.colorSwipeEdit))
                    .addSwipeRightActionIcon(R.drawable.ic_edit)
                    .addSwipeRightLabel("Edit")
                    .setSwipeRightLabelTextSize(TypedValue.COMPLEX_UNIT_SP, 20)
                    .create()
                    .decorate();

        }
    };

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
                Toast.makeText(getActivity().getApplicationContext(), t.getMessage() + "," + t.getCause(), Toast.LENGTH_LONG).show();
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
                Toast.makeText(getActivity().getApplicationContext(), "ToDo " + ToDo.getDescription() + " updated succesfully!", Toast.LENGTH_SHORT).show();
            }
        }.execute();
    }

    void deleteToDo(final ToDo todo) {
        ToDoRepo apiService = GA.getClient().create(ToDoRepo.class);
        Call<Integer> callToDo = apiService.deleteToDo("todos/" + todo.getTodoID());
        callToDo.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, final Response<Integer> response) {
                if (response.body() == 1) {
                    db.ToDoDAO().deleteToDo(todo);
                    Toast.makeText(getActivity().getApplicationContext(), "ToDo " + todo.getDescription() + " deleted successfully!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "ToDo NOT deleted!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Integer> call, final Throwable t) {
                Toast.makeText(getActivity().getApplicationContext(), t.getMessage() + "," + t.getCause(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        resetToDos();
    }
}
