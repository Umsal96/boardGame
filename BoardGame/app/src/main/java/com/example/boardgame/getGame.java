package com.example.boardgame;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.boardgame.Adapter.GetGameReviewAdapter;
import com.example.boardgame.Adapter.GetGameViewPagerAdapter;
import com.example.boardgame.item.GameReviewItem;
import com.example.boardgame.item.GetGameItem;
import com.example.boardgame.utility.JsonToData;
import com.example.boardgame.utility.JsonToGetData;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class getGame extends AppCompatActivity {
    private ImageButton backPage;
    private ViewPager2 gameViewPager;
    private TextView gameName;
    private Button inputReview;
    private TextView gameTitle;
    private TextView gameSummary;
    private TextView gameRule;
    private RecyclerView reviewRecyclerView;
    private RatingBar ratingBar;
    private TextView gameArg;
    private GetGameItem item;
    private ArrayList<GameReviewItem> reviewItem = new ArrayList<>();
    private String[] images = new String[0];
    GetGameViewPagerAdapter getGameViewPagerAdapter; //
    GetGameReviewAdapter getGameReviewAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_game);

        Intent intent = getIntent();
        int gameId = intent.getIntExtra("gameId", 0);

        backPage = findViewById(R.id.backPage);
        gameViewPager = findViewById(R.id.gameViewPager);
        gameName = findViewById(R.id.gameName);
        inputReview = findViewById(R.id.inputReview);
        gameTitle = findViewById(R.id.gameTitle);
        gameSummary = findViewById(R.id.gameSummary);
        gameRule = findViewById(R.id.gameRule);
        reviewRecyclerView = findViewById(R.id.reviewRecyclerView);
        ratingBar = findViewById(R.id.ratingBar);
        gameArg = findViewById(R.id.gameArg);

        backPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(getGame.this, main.class);
                intent1.putExtra("where", 3);
                startActivity(intent1);
            }
        });

        // 쉐어드 프리퍼런스에 있는 유저의 아이디를 가져옴
        // 1. 쉐어드 프리퍼런스를 사용하기위해 UserData 라는 이름의 파일을 가져옴
        SharedPreferences sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE);
        // 쉐어드 프리퍼런스에 있는 userId 라는 키값을 가지고 있는 값을 가져오고 가져온 값을 int형으로 변환함
        int userId = Integer.parseInt(sharedPreferences.getString("userId", ""));

        // 뷰 페이져
        getGameViewPagerAdapter = new GetGameViewPagerAdapter(images);
        gameViewPager.setAdapter(getGameViewPagerAdapter);

        // 리사이클러뷰
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        reviewRecyclerView.setLayoutManager(linearLayoutManager);
        getGameReviewAdapter = new GetGameReviewAdapter(reviewItem, userId);
        reviewRecyclerView.setAdapter(getGameReviewAdapter);

        getGame(gameId);
        getGameList(gameId);
        getGameReviewAdapter.setOnMoreMenuClickListener(new GetGameReviewAdapter.OnMoreMenuClickListener() {
            @Override
            public void onMoreMenuClick(int position, View v) {
                PopupMenu popupMenu = new PopupMenu(getGame.this, v); // 팝업 메뉴 등록
                popupMenu.getMenuInflater().inflate(R.menu.meeting_board_select_menu, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.action_board_modify:
                                Intent intent1 = new Intent(getGame.this, updateGameReview.class);
                                intent1.putExtra("reviewId", reviewItem.get(position).getReview_seq());
                                startActivity(intent1);
                                break;
                            case R.id.action_board_delete:
                                deleteDialog(reviewItem.get(position).getReview_seq(), gameId);
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });
        inputReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(getGame.this, inputGameReview.class);
                intent1.putExtra("gameId", gameId);
                startActivity(intent1);
            }
        });
    }

    private void deleteReview(int reviewId, int gameId){
        System.out.println("삭제 합니다.");
        System.out.println("삭제하는 reviewId : " + reviewId);
        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://3.38.213.196/gameReview/deleteReview.php").newBuilder();
        urlBuilder.addQueryParameter("reviewId", String.valueOf(reviewId)); // url 쿼리에 id 라는 메개변수 추가
        String url = urlBuilder.build().toString(); // 최종 url 생성

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

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getGameList(gameId);
                        }
                    });
                }
            }
        });
    }

    // 삭제할건지 보여주는 다이얼로그
    private void deleteDialog(int reviewId, int gameId){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("후기를 삭제 하시겠습니까?")
                .setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteReview(reviewId, gameId);
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        // 다이얼로그 표시
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    // 리뷰목록들을 가져옴
    private void getGameList(int gameId){
        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://3.38.213.196/gameReview/getGameReviewList.php").newBuilder();
        urlBuilder.addQueryParameter("gameId", String.valueOf(gameId));
        String url = urlBuilder.build().toString();
        JsonToData jt = new JsonToData();

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

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            reviewItem = jt.jsonToGameReview(responseData);
                            getGameReviewAdapter.setData(reviewItem);
                            getGameReviewAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });
    }

    // 게임의 정보를 가져옴
    private void getGame(int gameId){
        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://3.38.213.196/game/getGame.php").newBuilder();
        urlBuilder.addQueryParameter("gameId", String.valueOf(gameId));
        String url = urlBuilder.build().toString();
        JsonToGetData jtg = new JsonToGetData();

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
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            item = jtg.jsonGetToGetGame(responseData);

                            gameName.setText(item.getGame_name());
                            gameTitle.setText(item.getGame_name());
                            gameSummary.setText(item.getGame_summary());
                            gameRule.setText(item.getGame_detail());

                            gameArg.setText(String.valueOf((float) item.getAverage_review_grade()));
                            ratingBar.setRating(item.getAverage_review_grade());

                            String img = item.getImage_urls();

                            // 이미지가 있다면
                            if (img != null && !img.equals("null") && !img.equals("")){
                                images = img.split(",");
                                int lang = images.length;
                                for (int i = 0; i < lang; i++) {
                                    System.out.println("이미지의 uri : " + images[i]);
                                }
                                getGameViewPagerAdapter.setImages(images);
                                getGameViewPagerAdapter.notifyDataSetChanged();
                                gameViewPager.setVisibility(View.VISIBLE);
                            }else {
                                // 이미지가 없다면
                                System.out.println("이미지가 없습니다.");
                                gameViewPager.setVisibility(View.GONE);
                            }
                        }
                    });
                }
            }
        });
    } // end getGame
}