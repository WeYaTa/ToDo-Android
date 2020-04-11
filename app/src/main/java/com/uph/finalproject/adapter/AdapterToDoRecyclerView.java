package com.uph.finalproject.adapter;

import android.content.Context;
import android.graphics.Color;
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
import com.uph.finalproject.data.model.ToDo;
import com.uph.finalproject.ui.todo.ToDoFragment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AdapterToDoRecyclerView extends RecyclerView.Adapter {

    private ArrayList<ToDo> todoList;
    private Context context;
    private AppDatabase db;
    private String titleDesc = "aaaaaItem&&&&&Title";

    public AdapterToDoRecyclerView(ArrayList<ToDo> ToDos, Context ctx) {
        this.todoList = ToDos;
        context = ctx;
    }


    @Override
    public int getItemViewType(int position) {
        if (todoList.get(position).getDescription().equals(titleDesc)) {
            return 0;
        }
        return 1;
    }

    public class ViewHolderToDo extends RecyclerView.ViewHolder {
        TextView todoDesc;
        CardView cardView;

        ViewHolderToDo(View v) {
            super(v);
            todoDesc = v.findViewById(R.id.todoDescription);
            cardView = v.findViewById(R.id.todo_title_cv);
        }
    }

    public class ViewHolderTitle extends RecyclerView.ViewHolder {
        TextView todoStatusTitle;
        CardView cardView;

        ViewHolderTitle(View v) {
            super(v);
            todoStatusTitle = v.findViewById(R.id.todoStatusTitle);
            cardView = v.findViewById(R.id.todo_title_cv);
        }
    }

    @Override
    public int getItemCount() {
        return todoList.size();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view;

        if (viewType == 0) {
            view = layoutInflater.inflate(R.layout.item_todo_title, parent, false);
            return new ViewHolderTitle(view);
        }
        view = layoutInflater.inflate(R.layout.item_todo, parent, false);
        return new ViewHolderToDo(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final String desc = todoList.get(position).getDescription();
        final String status = todoList.get(position).getStatus();

        if (desc.equals(titleDesc)) {
            ViewHolderTitle vhTitle = (ViewHolderTitle) holder;


            switch (status) {
                case "todo":
                    vhTitle.todoStatusTitle.setText("To Do");
                    break;
                case "inprogress":
                    vhTitle.todoStatusTitle.setText("In Progress");
                    break;
                case "finished":
                    vhTitle.todoStatusTitle.setText("Finished");
                    break;
                default:
                    vhTitle.todoStatusTitle.setText("To Do");
                    break;
            }
        } else {
            ViewHolderToDo vhToDo = (ViewHolderToDo) holder;
            vhToDo.todoDesc.setText(desc);

            switch (status) {
                case "inprogress":
                    vhToDo.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.colorinprogress));
                    break;
                case "finished":
                    vhToDo.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.colorfinished));
                    break;
                case "todo":
                default:
                    vhToDo.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.colortodo));
                    break;
            }
        }
    }
}
