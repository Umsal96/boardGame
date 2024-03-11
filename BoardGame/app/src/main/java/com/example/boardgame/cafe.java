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

import com.example.boardgame.Adapter.CafeListAdapter;
import com.example.boardgame.item.CafeListItem;
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

public class cafe extends Fragment {
    private EditText searchBox; // 검색어 입력칸
    private ImageButton searchButton; // 검색 버튼
    private RecyclerView cafeRecyclerView;
    private FloatingActionButton inputCafe;

    ArrayList<CafeListItem> cli = new ArrayList<>();

    private CafeListAdapter cafeListAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cafe, container, false);

        searchBox = view.findViewById(R.id.searchBox);
        searchButton = view.findViewById(R.id.searchButton);
        cafeRecyclerView = view.findViewById(R.id.cafeRecyclerView);
        inputCafe = view.findViewById(R.id.inputCafe);
        inputCafe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), inputCafe.class);
                startActivity(intent);
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        cafeRecyclerView.setLayoutManager(linearLayoutManager);
        cafeListAdapter = new CafeListAdapter(cli);
        cafeRecyclerView.setAdapter(cafeListAdapter);

        getCafeList();

        return view;
    }

    private void getCafeList(){
        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://3.38.213.196/cafe/getCafeList.php").newBuilder();
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
                    System.out.println(responseData);
                    JsonToData jt = new JsonToData();
                    if(getActivity() != null && !getActivity().isFinishing()){
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                cli.clear();
                                cli.addAll(jt.jsonToCafeList(responseData));
//                                cafeListAdapter.setData(cli);
                                cafeListAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                }
            }
        });
    }
}