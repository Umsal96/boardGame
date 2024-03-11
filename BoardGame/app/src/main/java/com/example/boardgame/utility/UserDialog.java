package com.example.boardgame.utility;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.boardgame.Adapter.UserAdapter;
import com.example.boardgame.R;
import com.example.boardgame.item.UserItem;

import java.util.ArrayList;


public class UserDialog extends Dialog {

    UserAdapter userAdapter;

    private RecyclerView userRecyclerView;

    ArrayList<UserItem> ui = new ArrayList<>(); // 리사이클러뷰 변수 등

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_dialog);

        userRecyclerView = findViewById(R.id.userRecyclerView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        userRecyclerView.setLayoutManager(linearLayoutManager);

        userAdapter = new UserAdapter(ui);

        userRecyclerView.setAdapter(userAdapter);

    }

    public UserDialog(@NonNull Context context, ArrayList<UserItem> ui) {
        super(context);
        this.ui = ui;
    }
}
