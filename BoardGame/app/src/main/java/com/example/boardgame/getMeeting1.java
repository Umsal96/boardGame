package com.example.boardgame;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationBarView;

public class getMeeting1 extends AppCompatActivity {

    getMeetingHome getMeetingHome; // 변수 생성 getMeeting 페이지의 홈 페이지
    getMeetingBoard getMeetingBoard; // 변수 생성 getMeeting 페이지의 게시글 페이지
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_meeting1);

        // 페이지 연결
        getMeetingHome = new getMeetingHome();
        getMeetingBoard = new getMeetingBoard();

        Intent intent = getIntent(); // 외부에서 받아온 인텐트를 변수에 할당
        int chPage = intent.getIntExtra("where", 0);
        int id = intent.getIntExtra("id", 0); // 미팅의 고유 아이디를 가져옴


        NavigationBarView MainNavigationBarView = findViewById(R.id.button_meeting_navigationView); // 네이게이션 바 설정
        if(chPage == 0 || chPage == 1){ // 인텐트에 들어있던 where 의 value를 확인해서 만약 1 또는 0 이면 getMeetingHome을 프레그먼트에 표시
            getSupportFragmentManager().beginTransaction().replace(R.id.meetingContainer, getMeetingHome);
            MainNavigationBarView.setSelectedItemId(R.id.main);
        }

        if(chPage != 0 && chPage == 2){ // 인텐트에 들어있던 where 의 value를 확인해서 만약 0이 아니고 2이면 getMeetingBoard를 프레그먼트에 표시
            getSupportFragmentManager().beginTransaction().replace(R.id.meetingContainer, getMeetingBoard);
            MainNavigationBarView.setSelectedItemId(R.id.board);
        }

        // 네비게이션 바를 클릭했을때 프레그먼트에 어떤것을 표시할지 지정하는 이벤트
        MainNavigationBarView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int id = item.getItemId(); // 클릭된 아이템의 아이디

                switch (id){
                    case R.id.main:
                        getSupportFragmentManager().beginTransaction().replace(R.id.meetingContainer, getMeetingHome);
                        return true;
                    case R.id.board:
                        getSupportFragmentManager().beginTransaction().replace(R.id.meetingContainer, getMeetingBoard);
                        return true;
                }
                return false;
            }
        });
    } // end onCreate

    private void getMeetingName(int id){

    }
}