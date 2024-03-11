package com.example.boardgame.utility;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.boardgame.Adapter.ScheduleUserAdapter;
import com.example.boardgame.R;
import com.example.boardgame.item.UserNItem;

import java.util.ArrayList;

public class ScheduleDialog extends Dialog {
    ScheduleUserAdapter scheduleUserAdapter;
    private RecyclerView scheduleUserRecyclerView;
    ArrayList<UserNItem> ui;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule_user_dialog);

        scheduleUserRecyclerView = findViewById(R.id.scheduleUserRecyclerView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        scheduleUserRecyclerView.setLayoutManager(linearLayoutManager);

        scheduleUserAdapter = new ScheduleUserAdapter(ui);

        scheduleUserRecyclerView.setAdapter(scheduleUserAdapter);

    }

    public ScheduleDialog(@NonNull Context context, ArrayList<UserNItem> ui){
        super(context);
        this.ui = ui;
    }

}
