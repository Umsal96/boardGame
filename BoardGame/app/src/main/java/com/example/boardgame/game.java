package com.example.boardgame;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.boardgame.Adapter.BoardGameListAdapter;
import com.example.boardgame.item.GameItem;
import com.example.boardgame.utility.JsonToData;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class game extends Fragment {
    private EditText searchBox; // 검색어 입력 칸
    private ImageButton searchButton; // 검색 버튼
    private RecyclerView gameRecyclerView; // 리스트를 보여주는 리사이클러뷰
    private FloatingActionButton inputGame; // 게임 정보 입력 버튼
    ArrayList<GameItem> gt = new ArrayList<>();
    private BoardGameListAdapter boardGameListAdapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_game, container, false);

        searchBox = view.findViewById(R.id.searchBox);
        searchButton = view.findViewById(R.id.searchButton);
        gameRecyclerView = view.findViewById(R.id.gameRecyclerView);
        inputGame = view.findViewById(R.id.inputGame);
        inputGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), inputBoardGame.class);
                startActivity(intent);
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        gameRecyclerView.setLayoutManager(linearLayoutManager);
        boardGameListAdapter = new BoardGameListAdapter(gt);
        gameRecyclerView.setAdapter(boardGameListAdapter);

        getGameList();

        return view;
    }

    private void getGameList(){
        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://3.38.213.196/game/getGameList.php").newBuilder();
        String url = urlBuilder.build().toString();

        Request request = new Request.Builder()
                .url(url)
                .build();

        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.isSuccessful()){
                    String responseData = response.body().string();
                    JsonToData jt = new JsonToData();
                    if (getActivity() != null && !getActivity().isFinishing()){
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                gt.clear();
                                gt.addAll(jt.jsonToGameList(responseData));
                                boardGameListAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                }
            }
        });
    }
}