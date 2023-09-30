package com.example.boardgame;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.boardgame.Adapter.ScheduleAdapter;
import com.example.boardgame.item.ScheduleItem;
import com.example.boardgame.utility.JsonToData;
import com.example.boardgame.vo.meetingVO;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class getMeeting extends AppCompatActivity {

    ScheduleAdapter scheduleAdapter;
    private ImageView imageView2; // 모임의 대표 이미지
    private TextView titleName1; // 대표 이미지 위에 표시되는 모임 이름
    private TextView titleName2; // 대표 이미지 밑에 표시되는 모임 이름
    private TextView meetingContent; // 모임의 내용이 표시
    private ImageButton backPage; // 모임 리스트 페이지로 이동하는 버튼
    private ImageButton updateMeeting; // 모임 수정 페이지로 넘어가는 버튼
    private ImageButton viewPeople; // 모임 신청자와 모임 참여자를 볼수있는 페이지로 넘어가는 버튼
    private ImageButton moreMenu; // 모임 탈퇴 모임장 위임 등 여러 선택지를 보여주는 버튼
    private Button button4; // 모임 가입 버튼
    private Button intoSchedule; // 모임일정 만들기 버튼
    private Button intoBoard; // 게시글 작성 버튼
    private RecyclerView scheduleRecyclerView;

    ArrayList<ScheduleItem> st = new ArrayList<>();

    meetingVO vo = new meetingVO();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_meeting);

        // 인텐트에서 정보를 가져옴
        Intent intent = getIntent();
        // 꺼낸 id 는 미팅 고유 아이디이고 이 아이디를 가지고있는 컬럼의 정보를 가져옴
        int id = intent.getIntExtra("id", 0);

        imageView2 = findViewById(R.id.imageView2);
        titleName1 = findViewById(R.id.titleName1);
        titleName2 = findViewById(R.id.titleName2);
        meetingContent = findViewById(R.id.meetingContent);
        backPage = findViewById(R.id.backPage);
        updateMeeting = findViewById(R.id.updateMeeting);
        viewPeople = findViewById(R.id.viewPeople);
        moreMenu = findViewById(R.id.moreMenu);
        button4 = findViewById(R.id.button4);
        intoSchedule = findViewById(R.id.intoSchedule);
        intoBoard = findViewById(R.id.intoBoard);

        getList(id);

        scheduleRecyclerView = findViewById(R.id.scheduleRecyclerView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());

        scheduleRecyclerView.setLayoutManager(linearLayoutManager);

        scheduleAdapter = new ScheduleAdapter(st);

        scheduleRecyclerView.setAdapter(scheduleAdapter);


        getMeeting(id); // 정보 요청 함수

        System.out.println("id : " + id);

        intoSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(getMeeting.this, scheduleMeetingInput.class);
                intent1.putExtra("id", Integer.parseInt(vo.getMeetingSeq()));
                intent1.putExtra("cafeName", vo.getMeetingPlaceName());
                intent1.putExtra("cafeAddress", vo.getMeetingAddress());
                intent1.putExtra("x", vo.getMeetingLnt());
                intent1.putExtra("y", vo.getMeetingLat());
                intent1.putExtra("maxNum", vo.getMeetingMembers());
                intent1.putExtra("currentNum", vo.getMeetingCurrent());
                startActivity(intent1);
            }
        });

        backPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(getMeeting.this, main.class);
                intent1.putExtra("where", 1);
                startActivity(intent1);
            }
        });

        updateMeeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(getMeeting.this, updateMeeting.class);
                intent1.putExtra("id", id);
                startActivity(intent1);
            }
        });

        moreMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(getMeeting.this, v); // 팝업매뉴 등록
                getMenuInflater().inflate(R.menu.meeting_select_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if(item.getItemId() == R.id.action_meeting_out){
                            showMyDialog(id);
                        } else if(item.getItemId() == R.id.action_meeting_delegate){
                            System.out.println("클릭되었습니다.");
                        }
                        return true;
                    }
                });
                popupMenu.show();
            }
        });
    } // end create

    private void getList(int id){
        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://3.38.213.196/schedule/getSchedule.php").newBuilder();
        urlBuilder.addQueryParameter("id", String.valueOf(id)); // url 쿼리에 id 라는 메개변수 추가
        String url = urlBuilder.build().toString();
        JsonToData jt = new JsonToData(); // 받아온 json을 vo객체에 담는 함수가 있는 클래스

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
                    if(jt.jsonSchedule(responseData) != null){
                        st.addAll(jt.jsonSchedule(responseData));
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            scheduleAdapter.notifyDataSetChanged();

                        }
                    });

                }
            } // end onResponse
        });
    }

    // 다이얼로그 를 표시하고 만약 확인을 눌렀을때 미팅 탈퇴및 삭제를 하는 함수를 실행시심
    public void showMyDialog(int id){
        AlertDialog.Builder builder = new AlertDialog.Builder(getMeeting.this);
        builder.setTitle("경고");
        builder.setMessage("모임에 사람이 없어 탈퇴시 모임이 삭제됩니다.");
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                delete(id);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        // 다이얼로그 표시
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void delete(int id){
        // 요청할 url 등록
        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://3.38.213.196/meeting/deleteMeeting.php").newBuilder();
        urlBuilder.addQueryParameter("id", String.valueOf(id)); // url 쿼리에 id 라는 메개변수 추가
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
                Toast.makeText(getApplicationContext(), "삭제가 실패 했습니다. : " + call.toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "삭제가 왼료 되었습니다.", Toast.LENGTH_SHORT).show();

                    }
                });
                // meeting page 로 가능 인텐트
                Intent intent = new Intent(getMeeting.this, main.class);
                intent.putExtra("where", "1"); // 어디로 강제 이동할 것인지 설정
                startActivity(intent);
            }
        });


    }

    // 미팅 정보를 가져오는 함수
    private void getMeeting(int id){
        // 요청할 url 등록
        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://3.38.213.196/meeting/getMeeting.php").newBuilder();
        urlBuilder.addQueryParameter("id", String.valueOf(id)); // url 쿼리에 id 라는 메개변수 추가
        String url = urlBuilder.build().toString(); // 최종 url 생성

        // Request 객체 생성
        Request request = new Request.Builder()
                .url(url)
                .build();

        // client 객체 생성
        OkHttpClient client = new OkHttpClient();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.isSuccessful()){
                    String responseData = response.body().string();
                    JsonToData jt = new JsonToData(); // 받아온 json을 vo객체에 담는 함수가 있는 클래스
                    vo = jt.jsonMeetingGet(responseData);

                    // UI 업데이트는 메인 스레드에서 실행해야 함
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            titleName1.setText(vo.getMeetingName()); // 모임의 이름을 설정함
                            titleName2.setText(vo.getMeetingName()); // 모임의 이름을 설정함
                            meetingContent.setText(vo.getMeetingContent()); // 모임의 내용을 설정함

                            if (vo.getMeetingUrl() != null && !vo.getMeetingUrl().equals("null") && !vo.getMeetingUrl().equals("")) {
                                System.out.println("정상적인 이미지");
                                System.out.println("url : " + vo.getMeetingUrl());
                                Glide.with(getApplicationContext()).load("http://3.38.213.196" + vo.getMeetingUrl()).into(imageView2);
                            } else {
                                System.out.println("url : " + vo.getMeetingUrl());
                                System.out.println("아닌이미지");
                                imageView2.setImageResource(R.drawable.img);
                            }

                            int userSeq = Integer.parseInt(vo.getUserSeq()); // 미팅 테이블에 있는 유저 아이디

                            // 쉐어드 프리퍼런스에 있는 유저의 아이디를 가져옴
                            // 1. 쉐어드 프리퍼런스를 사용하기위해 UserData 라는 이름의 파일을 가져옴
                            SharedPreferences sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
                            // 쉐어드 프리퍼런스에 있는 userId 라는 키값을 가지고 있는 값을 가져오고 가져온 값을 int형으로 변환함
                            int userId = Integer.parseInt(sharedPreferences.getString("userId", ""));
                            if(userSeq == userId){
                                updateMeeting.setVisibility(View.VISIBLE);
                            }else {
                                updateMeeting.setVisibility(View.GONE);
                            }

                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "데이터를 가져오는데 실패했습니다 : " + response.body().string(), Toast.LENGTH_SHORT).show();
                }

            }
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Toast.makeText(getApplicationContext(), "데이터를 가져오는데 실패했습니다 : " + call.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}