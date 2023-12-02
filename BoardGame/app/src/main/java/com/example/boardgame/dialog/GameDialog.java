package com.example.boardgame.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.boardgame.Adapter.CafeGameAdapter;
import com.example.boardgame.Adapter.FoodAdapter;
import com.example.boardgame.R;
import com.example.boardgame.item.CafeGameItem;
import com.example.boardgame.utility.JsonToData;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GameDialog extends Dialog {
    private CafeGameAdapter cafeGameAdapter;
    private RecyclerView gameRecyclerView;
    private ArrayList<CafeGameItem> cgi = new ArrayList<>();
    private Activity activity;
    private Button inputGameButton;
    private int cafeId;
    private OnInputClickLister onInputClickLister;
    public interface OnInputClickLister{
        void onInputClick(int cafeId);
    }
    public void setOnInputClickLister(OnInputClickLister lister){
        onInputClickLister = lister;
    }
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_dialog);

        inputGameButton = findViewById(R.id.inputGameButton);
        inputGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onInputClickLister.onInputClick(cafeId);
            }
        });

        gameRecyclerView = findViewById(R.id.gameRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        gameRecyclerView.setLayoutManager(linearLayoutManager);
        cafeGameAdapter = new CafeGameAdapter(cgi);
        gameRecyclerView.setAdapter(cafeGameAdapter);

        cafeGameAdapter.setOnDeleteClickLister(new FoodAdapter.OnDeleteClickLister() {
            @Override
            public void onDeleteClick(int position, int foodId) {
                System.out.println("클릭되었습니다. : " + cgi.get(position).getCafe_game_seq());
                deleteDialog(cgi.get(position).getCafe_game_seq(), cafeId);
            }
        });

        getGameList(cafeId);
    }

    private void deleteDialog(int getCafeGameSeq, int cafeId){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("리스트에서 삭제 하시겠습니까?")
                .setPositiveButton("삭제", new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteGame(getCafeGameSeq, cafeId);
                    }
                })
                .setNegativeButton("최소", new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        // 다이얼로그 표시
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // 게임 리스트 삭제
    private void deleteGame(int getCafeGameSeq, int cafeId){
        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://3.38.213.196/cafeGame/deleteCafeGame.php").newBuilder();
        urlBuilder.addQueryParameter("getCafeGameSeq", String.valueOf(getCafeGameSeq)); // url 쿼리에 id 라는 메개변수 추가
        String url = urlBuilder.build().toString(); // 최종 url 생성

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
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getGameList(cafeId);
                        }
                    });
                }
            }
        });
    }

    public void updateData(int cafeId){
        getGameList(cafeId);
        System.out.println("데이터 업데이트");
        cafeGameAdapter.notifyDataSetChanged();
    }
    public GameDialog(@NonNull Context context, Activity activity, int cafeId){
        super(context);
        this.activity = activity;
        this.cafeId = cafeId;
    }

    // 게임 리스트 가져오기
    private void getGameList(int cafeId){
        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://3.38.213.196/cafeGame/getCafeGameList.php").newBuilder();
        urlBuilder.addQueryParameter("cafeId", String.valueOf(cafeId)); // url 쿼리에 id 라는 메개변수 추가
        String url = urlBuilder.build().toString(); // 최종 url 생성
        JsonToData js = new JsonToData();

        // Request 객체 생성
        Request request = new Request.Builder()
                .url(url)
                .build();

        // client 객체 생성
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
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            cgi.clear();
                            cgi.addAll(js.jsonToCafeGame(responseData));
                            cafeGameAdapter.setData(cgi);
                            cafeGameAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });
    }
}
