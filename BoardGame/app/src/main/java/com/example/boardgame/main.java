package com.example.boardgame;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationBarView;

public class main extends AppCompatActivity {

    meeting meeting; // 미팅 페이지
    profile profile; // 프로파일 페이지
    home home; // 메인 페이지

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 페이지 연결

        meeting = new meeting();
        profile = new profile();
        home = new home();

        Intent intent = getIntent();
        int chPage = intent.getIntExtra("where", 0); // 어디서 페이지가 이동해 왔는지 저장

        NavigationBarView MainNavigationBarView = findViewById(R.id.button_main_navigationview);
        if(chPage == 0 || chPage == 1){
            getSupportFragmentManager().beginTransaction().replace(R.id.container, home).commit();
            MainNavigationBarView.setSelectedItemId(R.id.home);
        }

        if(chPage != 0 && chPage == 5){
            getSupportFragmentManager().beginTransaction().replace(R.id.container, profile).commit();
            MainNavigationBarView.setSelectedItemId(R.id.profile);
        }

        // 메인 네비게시션 바를 클릭햇을때
        MainNavigationBarView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int id = item.getItemId();

                switch (id){
                    case R.id.home:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, home).commit();
                        return true;
                    case R.id.meeting:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, meeting).commit();
                        return true;
                    case R.id.profile:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, profile).commit();
                        return true;
                }

                return false;
            }
        });
    }
}