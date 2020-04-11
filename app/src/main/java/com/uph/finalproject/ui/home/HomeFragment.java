package com.uph.finalproject.ui.home;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
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
import androidx.appcompat.widget.SearchView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.uph.finalproject.GA;
import com.uph.finalproject.application.MyApplication;
import com.uph.finalproject.R;
import com.uph.finalproject.adapter.AdapterBoardRecyclerView;
import com.uph.finalproject.data.factory.AppDatabase;
import com.uph.finalproject.data.model.Board;

import java.util.ArrayList;
import java.util.Arrays;

import com.uph.finalproject.data.model.LoggedInUser;
import com.uph.finalproject.data.model.ToDo;
import com.uph.finalproject.data.model.User;
import com.uph.finalproject.data.repository.BoardRepo;
import com.uph.finalproject.ui.popup.AddBoardPopUp;
import com.uph.finalproject.ui.popup.EditBoardPopUp;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {
    private LoggedInUser user;
    private RecyclerView boardRV;
    private RecyclerView.Adapter rvBoardAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Board> boardList;
    private FloatingActionButton fab;
    private SwipeRefreshLayout refresh;
    private TextView emptyview;
    private AppDatabase db;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.home, menu);
        super.onCreateOptionsMenu(menu, inflater);

        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                ((AdapterBoardRecyclerView) boardRV.getAdapter()).getFilter().filter(newText);
                return false;
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Boards");
        user = ((MyApplication)getActivity().getApplication()).getLoggedInUser();
        boardList = new ArrayList<>();

        emptyview = view.findViewById(R.id.emptyView);
        refresh = view.findViewById(R.id.swipeRefreshLayout);
        boardRV = view.findViewById(R.id.board_rv);
        fab = view.findViewById(R.id.fab);
        db = Room.databaseBuilder(getActivity().getApplicationContext(), AppDatabase.class, ((MyApplication)getActivity().getApplication()).getDATABASE_NAME()).allowMainThreadQueries().build();


        boardRV.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        boardRV.setLayoutManager(layoutManager);

        rvBoardAdapter = new AdapterBoardRecyclerView(boardList, getActivity().getApplicationContext());
        boardRV.setAdapter(rvBoardAdapter);

        resetBoards();

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(boardRV);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //popup
                Intent i = new Intent(getActivity().getApplicationContext(), AddBoardPopUp.class);
                startActivity(i);
            }
        });

        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                resetBoards();
            }
        });
    }

    void resetBoards() {
        boardList.clear();
        ((AdapterBoardRecyclerView)boardRV.getAdapter()).allBoardsList.clear();
        boardList.addAll(Arrays.asList(db.BoardDAO().getBoardsByUserID(user.getUserID())));
        ((AdapterBoardRecyclerView)boardRV.getAdapter()).allBoardsList.addAll(boardList);
        rvBoardAdapter.notifyDataSetChanged();
        refresh.setRefreshing(false);

        if(boardList.isEmpty()) emptyview.setVisibility(View.VISIBLE);
        else emptyview.setVisibility(View.GONE);
    }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {

            int fromPosition = viewHolder.getAdapterPosition();
            int toPosition = target.getAdapterPosition();

            //update position to database

            boardRV.getAdapter().notifyItemMoved(fromPosition, toPosition);
            return false;
        }

        Board deletedBoard = null;

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            switch (direction) {

                case ItemTouchHelper.LEFT:
                    final int position = viewHolder.getAdapterPosition();
                    deletedBoard = boardList.get(position);
                    boardList.remove(position);
                    rvBoardAdapter.notifyItemRemoved(position);

                    Snackbar.make(fab, "Deleted " + deletedBoard.getBoardName(), Snackbar.LENGTH_LONG).setAction("Undo", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            boardList.add(position, deletedBoard);
                            rvBoardAdapter.notifyItemInserted(position);
                        }
                    }).addCallback(new Snackbar.Callback() {
                        @Override
                        public void onDismissed(Snackbar snackbar, int dismissType) {
                            super.onDismissed(snackbar, dismissType);

                            if(dismissType == DISMISS_EVENT_TIMEOUT || dismissType == DISMISS_EVENT_SWIPE
                                    || dismissType == DISMISS_EVENT_CONSECUTIVE || dismissType == DISMISS_EVENT_MANUAL){
                                //delete from db
                                ((AdapterBoardRecyclerView)boardRV.getAdapter()).allBoardsList.remove(position);
                                deleteBoard(deletedBoard);
                            }

                        }
                    }).show();
                    break;
                case ItemTouchHelper.RIGHT:
                    final int position_r = viewHolder.getAdapterPosition();
                    rvBoardAdapter.notifyItemChanged(position_r);


                    //popup
                    Intent i = new Intent(getActivity().getApplicationContext(), EditBoardPopUp.class);
                    i.putExtra("boardID", boardList.get(position_r).getBoardID());
                    i.putExtra("boardName", boardList.get(position_r).getBoardName());
                    i.putExtra("boardColor", boardList.get(position_r).getBackgroundColor());
                    startActivity(i);

                    break;
            }
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

    @Override
    public void onResume() {
        super.onResume();
        resetBoards();
    }

    void deleteBoard(final Board board){
        BoardRepo apiService = GA.getClient().create(BoardRepo.class);
        Call<Integer> callBoard = apiService.deleteBoard("boards/"+board.getBoardID());
        callBoard.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, final Response<Integer> response) {
                if(response.body() == 1){
                    db.BoardDAO().deleteBoard(board);
                    Toast.makeText(getActivity().getApplicationContext(), "Board "+board.getBoardName()+" deleted successfully!", Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(getActivity().getApplicationContext(), "Board NOT deleted!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Integer> call, final Throwable t) {
                Toast.makeText(getActivity().getApplicationContext(), t.getMessage() + "," + t.getCause(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
