package com.uph.finalproject.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.uph.finalproject.MainActivity;
import com.uph.finalproject.R;
import com.uph.finalproject.data.factory.AppDatabase;
import com.uph.finalproject.data.model.Board;
import com.uph.finalproject.ui.todo.ToDoFragment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AdapterBoardRecyclerView extends RecyclerView.Adapter<AdapterBoardRecyclerView.ViewHolder> implements Filterable {

    private ArrayList<Board> boardList;
    public ArrayList<Board> allBoardsList;
    private Context context;
    private AppDatabase db;

    public AdapterBoardRecyclerView(ArrayList<Board> Boards, Context ctx){
        this.boardList = Boards;
        this.allBoardsList = new ArrayList<>(boardList);
        context = ctx;

//        db = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "Boarddb").allowMainThreadQueries().build();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    Filter filter = new Filter() {

        //runs on background thread
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Board> filteredList = new ArrayList<>();
            if(constraint.toString().isEmpty()){
                filteredList.addAll(allBoardsList);
            }else{
                for (Board board: allBoardsList) {
                    Log.e("Board nama", board.getBoardName());
                    if(board.getBoardName().toLowerCase().contains(constraint.toString().toLowerCase())){
                        filteredList.add(board);
                    }
                }
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredList;

            return filterResults;
        }

        //runs on ui thread
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if(results.values != null){
                boardList.clear();
                boardList.addAll((Collection<? extends Board>) results.values);
                notifyDataSetChanged();
            }

        }
    };

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView boardDisplayName;
        CardView cardView;

        ViewHolder(View v) {
            super(v);
            boardDisplayName = v.findViewById(R.id.boardName);
            cardView = v.findViewById(R.id.cv_main);
        }
    }

    @Override
    public int getItemCount() {
        return boardList.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_board, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final String name = boardList.get(position).getBoardName();
        final String color = boardList.get(position).getBackgroundColor();

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //go to todo fragment
                ((MainActivity) view.getContext()).changeFragment(new ToDoFragment(boardList.get(position)));
            }
        });

        holder.boardDisplayName.setText(name);
        holder.cardView.setCardBackgroundColor(Color.parseColor(color));
    }


}
