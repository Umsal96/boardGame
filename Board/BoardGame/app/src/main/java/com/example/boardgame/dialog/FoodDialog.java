package com.example.boardgame.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.boardgame.Adapter.FoodAdapter;
import com.example.boardgame.R;
import com.example.boardgame.inputFood;
import com.example.boardgame.item.FoodItem;
import com.example.boardgame.utility.JsonToData;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FoodDialog extends Dialog {
    private FoodAdapter foodAdapter;
    private RecyclerView foodRecyclerView;
    private ArrayList<FoodItem> fi = new ArrayList<>();
    private Activity activity;
    private Button inputFoodButton;
    private int cafeId;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.food_dialog);

        inputFoodButton = findViewById(R.id.inputFoodButton);
        inputFoodButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(getContext(), inputFood.class);
                intent1.putExtra("cafeId", cafeId);
                getContext().startActivity(intent1);
            }
        });
        foodRecyclerView = findViewById(R.id.foodRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        foodRecyclerView.setLayoutManager(linearLayoutManager);
        foodAdapter = new FoodAdapter(fi);
        foodRecyclerView.setAdapter(foodAdapter);
        foodAdapter.setOnDeleteClickLister(new FoodAdapter.OnDeleteClickLister() {
            @Override
            public void onDeleteClick(int position, int foodId) {
                System.out.println("클릭되었습니다. : " + fi.get(position).getFood_seq());
                deleteDialog(fi.get(position).getFood_seq(), fi.get(position).getCafe_seq());
            }
        });

        getFoodList(cafeId);
    }
    public FoodDialog(@NonNull Context context, Activity activity, int cafeId){
        super(context);
        this.activity = activity;
        this.cafeId = cafeId;
    }
    // 삭제 다이얼로그 표시
    private void deleteDialog(int foodId, int cafeId){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("음식 정보를 삭제 하시겠습니까?")
                .setPositiveButton("삭제", new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteFood(foodId, cafeId);
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
    // 음식 리스트 삭제
    private void deleteFood(int foodId, int cafeId){
        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://3.38.213.196/food/deleteFood.php").newBuilder();
        urlBuilder.addQueryParameter("foodId", String.valueOf(foodId)); // url 쿼리에 id 라는 메개변수 추가
        String url = urlBuilder.build().toString(); // 최종 url 생성

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
                            getFoodList(cafeId);
                        }
                    });
                }
            }
        });
    }
    // 음식 리스트를 가져옴
    private void getFoodList(int cafeId){
        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://3.38.213.196/food/getFoodList.php").newBuilder();
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
                            fi.clear();
                            fi.addAll(js.jsonToFoodList(responseData));
                            System.out.println("fi : " + fi.size());
                            foodAdapter.setData(fi);
                            foodAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });
    }
}
