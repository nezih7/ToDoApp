package com.example.todo.Adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todo.AddNewTask;
import com.example.todo.MainActivity;
import com.example.todo.Model.ToDoModel;
import com.example.todo.R;
import com.example.todo.utlis.DataBaseHelper;

import java.util.List;

public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.MyViewHolder> {
    private List<ToDoModel> mlist;
    private MainActivity activity;
    private DataBaseHelper myDB;

    public ToDoAdapter(DataBaseHelper myDB,MainActivity activity){
        this.activity=activity;
        this.myDB=myDB;


    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.tasks_layout,parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final ToDoModel item=mlist.get(position);
        holder.checkBox.setText(item.getTask());
        holder.checkBox.setChecked(toboolean(item.getStatus()));
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(compoundButton.isChecked()){
                    myDB.updateStatus(item.getId(),0);
                }else {
                    myDB.updateStatus(item.getId(),1);
                }
            }
        });
    }
    public boolean toboolean(int num){
        return num!=0;
    }
    public Context getContext(){
        return activity;
    }
    @Override
    public int getItemCount() {
        return mlist != null ? mlist.size() : 0;
    }
    public void setTasks(List<ToDoModel> mlist){
        this.mlist=mlist;
        notifyDataSetChanged();
    }
    public void deleteTask(int position){
        ToDoModel item=mlist.get(position);
        myDB.deleteTask(item.getId());
        mlist.remove(position);
        notifyItemRemoved(position);
    }
    public void editItems(int position){
        ToDoModel item=mlist.get(position);
        Bundle bundle= new Bundle();
        bundle.putInt("Id",item.getId());
        bundle.putString("task", item.getTask());
        AddNewTask task=new AddNewTask();
        task.setArguments(bundle);
        task.show(activity.getSupportFragmentManager(), task.getTag());
    }
    public static class MyViewHolder extends RecyclerView.ViewHolder{
        CheckBox checkBox;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox=itemView.findViewById(R.id.checkbox);

        }
    }
}
