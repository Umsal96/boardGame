package com.example.boardgame.utility;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.boardgame.Adapter.ExchangeLeaderAdapter;
import com.example.boardgame.R;
import com.example.boardgame.item.UserItem;

import java.util.ArrayList;

public class ExchangeLeaderDialog extends Dialog {
    ExchangeLeaderAdapter exchangeLeaderAdapter;
    private RecyclerView userRecyclerView;
    ArrayList<UserItem> ui;
    int leaderUserId;
    int meetingId; // 미팅의 고유 아이디
    @Override
    protected void  onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exchange_leader_dialog);

        userRecyclerView = findViewById(R.id.userRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        userRecyclerView.setLayoutManager(linearLayoutManager);
        exchangeLeaderAdapter = new ExchangeLeaderAdapter(ui, leaderUserId, meetingId);
        userRecyclerView.setAdapter(exchangeLeaderAdapter);
    }
    public ExchangeLeaderDialog(@NonNull Context context, ArrayList<UserItem> ui, int leaderUserId, int id){
        super(context);
        this.ui = ui;
        this.leaderUserId = leaderUserId;
        this.meetingId = id;
    }
}
