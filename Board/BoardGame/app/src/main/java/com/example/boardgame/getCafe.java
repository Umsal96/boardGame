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

import com.example.boardgame.Adapter.GetCafeReviewAdapter;
import com.example.boardgame.Adapter.GetGameViewPagerAdapter;
import com.example.boardgame.dialog.FoodDialog;
import com.example.boardgame.dialog.GameDialog;
import com.example.boardgame.dialog.InputGameListDialog;
import com.example.boardgame.item.CafeReviewItem;
import com.example.boardgame.item.GetCafeItem;
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

public class getCafe extends AppCompatActivity {

    private ImageButton backPage;
    private ImageButton foodButton;
    private ImageButton gameButton;
    private ImageButton moneyButton;
    private Button inputReview;
    private ViewPager2 cafeViewPager;
    private TextView cafeName;
    private TextView cafeTitle;
    private TextView cafeSummary;
    private TextView cafeArg;
    private RecyclerView reviewRecyclerView;
    private String[] images = new String[0];
    private RatingBar ratingBar;
    private GetCafeItem item;
    private FoodDialog foodDialog;
    private GameDialog gameDialog;
    private InputGameListDialog inputGameListDialog;
    private ArrayList<CafeReviewItem> reviewItem = new ArrayList<>();
    GetGameViewPagerAdapter getGameViewPagerAdapter;
    GetCafeReviewAdapter getCafeReviewAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_cafe);

        Intent intent = getIntent();
        int cafeId = intent.getIntExtra("cafeId", 0);
        int where = intent.getIntExtra("where", 0);
        foodDialog = new FoodDialog(getCafe.this, getCafe.this, cafeId);
        gameDialog = new GameDialog(getCafe.this, getCafe.this, cafeId);
        inputGameListDialog = new InputGameListDialog(getCafe.this, getCafe.this, cafeId);
        if(where != 0){
            foodDialog.show();
        }
        gameDialog.setOnInputClickLister(new GameDialog.OnInputClickLister() {
            @Override
            public void onInputClick(int cafeId) {
                System.out.println("클릭되었습니다.");
                gameDialog.dismiss();

                inputGameListDialog.show();
            }
        });
        inputGameListDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                System.out.println("다이얼로그 꺼짐");
                gameDialog.updateData(cafeId);
            }
        });

        backPage = findViewById(R.id.backPage);
        inputReview = findViewById(R.id.inputReview);
        cafeViewPager = findViewById(R.id.cafeViewPager);
        cafeName = findViewById(R.id.cafeName);
        cafeTitle = findViewById(R.id.cafeTitle);
        cafeSummary = findViewById(R.id.cafeSummary);
        cafeArg = findViewById(R.id.cafeArg);
        reviewRecyclerView = findViewById(R.id.reviewRecyclerView);
        ratingBar = findViewById(R.id.ratingBar);
        foodButton = findViewById(R.id.foodButton); // 음식 리스트 보여줌
        gameButton = findViewById(R.id.gameButton); // 게임 리스트 보여줌
        moneyButton = findViewById(R.id.moneyButton); // 가격표 보여줌

        // 쉐어드 프리퍼런스에 있는 유저의 아이디를 가져옴
        // 1. 쉐어드 프리퍼런스를 사용하기위해 UserData 라는 이름의 파일을 가져옴
        SharedPreferences sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE);
        // 쉐어드 프리퍼런스에 있는 userId 라는 키값을 가지고 있는 값을 가져오고 가져온 값을 int형으로 변환함
        int userId = Integer.parseInt(sharedPreferences.getString("userId", ""));
        backPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(getCafe.this, main.class);
                intent1.putExtra("where", 4);
                startActivity(intent1);
            }
        });
        foodButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isFinishing()){

                    if (!foodDialog.isShowing()) {
                        foodDialog.show();
                    }
                }
            }
        });
        gameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameDialog.show();
            }
        });
        inputReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(getCafe.this, inputCafeReview.class);
                intent1.putExtra("cafeId", cafeId);
                startActivity(intent1);
            }
        });

        //  뷰페이져
        getGameViewPagerAdapter = new GetGameViewPagerAdapter(images);
        cafeViewPager.setAdapter(getGameViewPagerAdapter);

        // 리사이클러뷰
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        reviewRecyclerView.setLayoutManager(linearLayoutManager);
        getCafeReviewAdapter = new GetCafeReviewAdapter(reviewItem, userId);
        reviewRecyclerView.setAdapter(getCafeReviewAdapter);

        // 게임 정보 가져오는 함수
        getCafe(cafeId);
        getCafeList(cafeId);

        getCafeReviewAdapter.setOnMoreMenuClickListener(new GetCafeReviewAdapter.OnMoreMenuClickListener() {
            @Override
            public void onMoreMenuClick(int position, View v) {
                PopupMenu popupMenu = new PopupMenu(getCafe.this, v); // 팝업 메뉴 등록
                popupMenu.getMenuInflater().inflate(R.menu.meeting_board_select_menu, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.action_board_modify:
                                Intent intent1 = new Intent(getCafe.this, getUpdateCafeReview.class);
                                intent1.putExtra("reviewId", reviewItem.get(position).getReview_seq());
                                startActivity(intent1);
                                break;
                            case R.id.action_board_delete:
                                // 삭제 다이얼로그 표시
                                deleteDialog(reviewItem.get(position).getReview_seq(), cafeId);
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });
    }

    private void deleteReview(int reviewId, int cafeId){
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
                            getCafeList(cafeId);
                        }
                    });
                }
            }
        });
    }

    // 삭제 다이얼로그 표시
    private void deleteDialog(int reviewId, int cafeId){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("후기를 삭제 하시겠습니까?")
                .setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteReview(reviewId, cafeId);
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

    // 카페 정보 가져오기
    private void getCafeList(int cafeId){
        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://3.38.213.196/cafeReview/getCafeReviewList.php").newBuilder();
        urlBuilder.addQueryParameter("cafeId", String.valueOf(cafeId));
        String url = urlBuilder.build().toString();
        JsonToData jt = new JsonToData();
        System.out.println("getCafeList 실행");

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
                            reviewItem = jt.jsonToCafeReview(responseData);
                            getCafeReviewAdapter.setData(reviewItem);
                            getCafeReviewAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });
    }
    private void getCafe(int cafeId){
        System.out.println("삭제하는 reviewId : " + cafeId);
        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://3.38.213.196/cafe/getCafe.php").newBuilder();
        urlBuilder.addQueryParameter("cafeId", String.valueOf(cafeId)); // url 쿼리에 id 라는 메개변수 추가
        String url = urlBuilder.build().toString(); // 최종 url 생성
        JsonToGetData jtg = new JsonToGetData();
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
                            item = jtg.jsonGetToGetCafe(responseData);

                            cafeName.setText(item.getCafe_name());
                            cafeTitle.setText(item.getCafe_name());
                            cafeSummary.setText(item.getCafe_content());
                            cafeArg.setText(String.valueOf((float) item.getAverage_review_grade()));
                            ratingBar.setRating(item.getAverage_review_grade());

                            String img = item.getImage_urls();
                            // 이미지가 있을경우
                            if(img != null && !img.equals("null") && !img.equals("")){
                                images = img.split(",");
                                int lang = images.length;
                                for (int i = 0; i < lang; i++) {
                                    System.out.println("이미지의 uri : " + images[i]);
                                }
                                getGameViewPagerAdapter.setImages(images);
                                getGameViewPagerAdapter.notifyDataSetChanged();
                                cafeViewPager.setVisibility(View.VISIBLE);
                            } else {
                                // 이미지가 없다면
                                System.out.println("이미지가 없다면");
                                cafeViewPager.setVisibility(View.GONE);
                            }
                        }
                    });
                }
            }
        });
    } // end getCafe
}