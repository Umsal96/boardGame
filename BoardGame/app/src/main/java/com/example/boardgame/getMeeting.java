package com.example.boardgame;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.boardgame.item.UserItem;
import com.example.boardgame.utility.ExchangeLeaderDialog;
import com.example.boardgame.utility.FragToActData;
import com.example.boardgame.utility.JsonToData;
import com.google.android.material.navigation.NavigationBarView;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class getMeeting extends AppCompatActivity implements FragToActData {

    getMeetingHome getMeetingHome; // 변수 생성 getMeeting 페이지의 홈 페이지
    getMeetingBoard getMeetingBoard; // 변수 생성 getMeeting 페이지의 게시글 페이지
    getMeetingCheat getMeetingCheat; // 변수 생성 getMeeting 페이지 채팅 페이지
    private ImageButton backPage;
    private ImageButton moreMenu;
    private TextView titleName1;
    ArrayList<UserItem> ui;
    private int LeaderUserId; // 현재 접속한 모임의 방장 고유 Id
    private int UserId; // 현재 로그인한 유저의 고유 Id
    private int size; // 현재 모임의 인원수
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_meeting);

        backPage = findViewById(R.id.backPage);
        moreMenu = findViewById(R.id.moreMenu);
        titleName1 = findViewById(R.id.titleName1);

        Intent intent = getIntent(); // 외부에서 받아온 인텐트를 변수에 할당
        int chPage = intent.getIntExtra("where", 0);
        int id = intent.getIntExtra("id", 0); // 미팅의 고유 아이디를 가져옴

        // 쉐어드 프리퍼런스에 있는 유저의 아이디를 가져옴
        // 1. 쉐어드 프리퍼런스를 사용하기위해 UserData 라는 이름의 파일을 가져옴
        SharedPreferences sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE);
        // 쉐어드 프리퍼런스에 있는 userId 라는 키값을 가지고 있는 값을 가져오고 가져온 값을 int형으로 변환함
        UserId = Integer.parseInt(sharedPreferences.getString("userId", ""));

        // 번들에 데이터를 밤음
        Bundle bundle = new Bundle();
        bundle.putInt("id", id);

        // 페이지 연결
        getMeetingHome = new getMeetingHome();
        getMeetingHome.setArguments(bundle);
        getMeetingBoard = new getMeetingBoard();
        getMeetingCheat = new getMeetingCheat();

        getMeetingName(id);

        NavigationBarView MainNavigationBarView = findViewById(R.id.button_meeting_navigationView); // 네이게이션 바 설정
        if(chPage == 0 || chPage == 1){ // 인텐트에 들어있던 where 의 value를 확인해서 만약 1 또는 0 이면 getMeetingHome을 프레그먼트에 표시
            getSupportFragmentManager().beginTransaction().replace(R.id.meetingContainer, getMeetingHome).commit();
            MainNavigationBarView.setSelectedItemId(R.id.main);
        }

        if(chPage != 0 && chPage == 2){ // 인텐트에 들어있던 where 의 value를 확인해서 만약 0이 아니고 2이면 getMeetingBoard를 프레그먼트에 표시
            getSupportFragmentManager().beginTransaction().replace(R.id.meetingContainer, getMeetingBoard).commit();
            MainNavigationBarView.setSelectedItemId(R.id.board);
        }

        // ... 버튼을 클릭했을때 실행되는 이벤트
        moreMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(getMeeting.this, v); // 팝업메뉴 등록
                getMenuInflater().inflate(R.menu.meeting_select_menu, popupMenu.getMenu());

                // 특정 조건에서만 보이는 메뉴를 만들기 위한 사전 작업
                MenuItem itemMeetingOut = popupMenu.getMenu().findItem(R.id.action_meeting_out); // 변수에 팝업메뉴 아이템을 넣음 모임 탈퇴 메뉴
                MenuItem itemMeetingdelegate = popupMenu.getMenu().findItem(R.id.action_meeting_delegate); // 모임장 위임 메뉴

                // 항상 actgion_meeting_out 표시
                itemMeetingOut.setVisible(true);

                // 모임장과 현재 접속 인원이 같을때만 action_meeting_delegate 표시
                if(LeaderUserId == UserId){
                    itemMeetingdelegate.setVisible(true);
                }else{
                    itemMeetingdelegate.setVisible(false);
                }
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if(item.getItemId() == R.id.action_meeting_out){ // 모임 탈퇴하기
                            if(LeaderUserId == UserId){
                                if(size <= 1){
                                    showDeleteDialog(id, UserId);
                                }else {
                                    showLeaderExitDialog(); // 지금 모임장이니까 모임장을 위임하라는 다이얼로그 표시
                                }

                            }else {
                                System.out.println("현재 모임 인원수 : " + size);

                                if(size <= 1){
                                    showDeleteDialog(id, UserId);
                                }else{
                                    // 미팅의 고유 아이디와 현재 로그인한 유저의 고유 아이디를 매게변수로 담음
                                    showExitDialog(id, UserId); // 정말 탈퇴할것이냐고 한번더 묻는 다이얼로그
                                }
                            }

                        }else if(item.getItemId() == R.id.action_meeting_delegate){ // 모임장 위임하기
                            getUserList(id, LeaderUserId);
                            System.out.println("방장의 고유 유저 아이디 : " + LeaderUserId);
                            System.out.println("클릭한 유저의 고유 아이디 : " + UserId);
                            System.out.println("클릭한 모임의 고유 아이디 : " + id);
                            System.out.println("클릭되었습니다.");
                        }
                        return true;
                    }
                });
                popupMenu.show();
            }
        });

        // 뒤로 가기 버튼을 눌렀을때
        backPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 메인 페이지로 이동
                Intent intent1 = new Intent(getMeeting.this, main.class);
                // 그 중에서 어디 페이지로 이동할건지 결정  1은 미팅 페이지
                intent1.putExtra("where", 1);
                startActivity(intent1);
            }
        });

        // 네비게이션 바를 클릭했을때 프레그먼트에 어떤것을 표시할지 지정하는 이벤트
        MainNavigationBarView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int id = item.getItemId(); // 클릭된 아이템의 아이디

                switch (id){
                    case R.id.main:
                        getSupportFragmentManager().beginTransaction().replace(R.id.meetingContainer, getMeetingHome).commit();
                        return true;
                    case R.id.board:
                        getSupportFragmentManager().beginTransaction().replace(R.id.meetingContainer, getMeetingBoard).commit();
                        return true;
                    case R.id.cheat:
                        getSupportFragmentManager().beginTransaction().replace(R.id.meetingContainer, getMeetingCheat).commit();
                        return true;
                }
                return false;
            }
        });
    } // end onCreate



    // 미팅 이름을 받아오는 메소드
    private void getMeetingName(int id){
        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://3.38.213.196/meeting/getMeetingTitle.php").newBuilder();
        urlBuilder.addQueryParameter("id", String.valueOf(id)); // url 쿼리에 id 라는 메개변수 추가
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
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            titleName1.setText(responseData);

                        }
                    });
                }
            }
        });
    } // end getMeetingName

    // 현재 방장이라 모임을 탈퇴를 못한다는 다이얼로그 표시
    private void showLeaderExitDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getMeeting.this);
        builder.setTitle("경고");
        builder.setMessage("모임장이어서 탈퇴를 할수 없습니다. 모임장을 위임해주세요");

        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        // 다이얼로그 표시
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // 모임에서 진짜 탈퇴하겠냐고 한번더 물어보는 메소드
    private void showExitDialog(int id, int userId){
        AlertDialog.Builder builder = new AlertDialog.Builder(getMeeting.this);
        builder.setTitle("경고");
        builder.setMessage("탈퇴 하시겠습니까?");

        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // id : 모임의 고유 아이디, userId : 현재 로그인한 유저의 고유 아이디
                exitMeeting(id, userId); // 모임에서 나가는 메소드
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

    // 삭제 관련된 다이얼로그 표시
    private void showDeleteDialog(int id, int userId){
        AlertDialog.Builder builder = new AlertDialog.Builder(getMeeting.this);
        builder.setTitle("경고");
        builder.setMessage("모임에 사람이 없어 탈퇴시 모임이 삭제됩니다.");
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                exitMeeting(id, userId);
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

    // 해당 모임 탈퇴 메소드
    private void exitMeeting(int meetingId, int userId){
        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://3.38.213.196/meeting/exitMeeting.php").newBuilder();
        urlBuilder.addQueryParameter("meetingId", String.valueOf(meetingId)); // url 쿼리에 meetingId 라는 메개변수 추가
        urlBuilder.addQueryParameter("userId", String.valueOf(userId)); // url 쿼리에 userId 라는 메개변수 추가
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
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "탈퇴가 왼료 되었습니다.", Toast.LENGTH_SHORT).show();
                        size--;

                    }
                });
                // meeting page 로 가능 인텐트
                Intent intent = new Intent(getMeeting.this, main.class);
                intent.putExtra("where", "1"); // 어디로 강제 이동할 것인지 설정
                startActivity(intent);
            }
        });
    }
    // 모임에 가입된 모임장을 제외한 유저를 가져옴
    private void getUserList(int id, int leaderUserId){
        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://3.38.213.196/meeting/getNotLeaderUserMeetingList.php").newBuilder();
        urlBuilder.addQueryParameter("meetingId", String.valueOf(id));
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
                    System.out.println("리더아님");
                    System.out.println(responseData);
                    JsonToData jt = new JsonToData();

                    ui = jt.jsonToUserList(responseData);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ExchangeLeaderDialog exchangeLeaderDialog = new ExchangeLeaderDialog(getMeeting.this, ui, leaderUserId, id);
                            exchangeLeaderDialog.show();

                        }
                    });
                }
            }
        });
    }

    // 해당 미팅 삭제 요청 메소드
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

    @Override
    public void onDataPass(int LeaderUserId, int size) {
        this.LeaderUserId = LeaderUserId;
        this.size = size;
    }
}