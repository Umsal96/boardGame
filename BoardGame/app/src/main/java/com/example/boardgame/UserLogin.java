package com.example.boardgame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.boardgame.utility.NetworkManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UserLogin extends AppCompatActivity {

    private EditText joinNick; // 닉네임 입력칸
    private EditText joinPassword; // 비밀번호 입력칸
    private EditText joinRePassword; // 비밀번호 재입력칸
    private Button checkNick; // 닉네임 중복검사 버튼
    private Button endJoin; // 회원가입 완료 버튼
    private TextView noticeNick; // 닉네임 관련 메시지 텍스트
    private TextView noticeRePassword; // 비밀번호 확인 관련 메시지 텍스트

    private boolean bCheckNick, bCheckPassword, bReCheckPassword;

   // 버튼의 배경색을 하늘색으로 설정
    int skyBlueButton = Color.parseColor("#3498DB");
    // 버튼의 배경색을 회색으로 설정 (비활성화 상태에서의 색상)
    int grayButton = Color.parseColor("#CCCCCC");
    String phoneNumber;
    String email;
    String nick;
    String pass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

        // 인텐트에서 데이터 추출
        Intent intent = getIntent();
        phoneNumber = intent.getStringExtra("phone");
        email = intent.getStringExtra("email");

        // ui 연결
        joinNick = findViewById(R.id.joinNick); // 닉네임 입력칸
        joinPassword = findViewById(R.id.joinPassword); // 비밀번호 입력칸
        joinRePassword = findViewById(R.id.joinRePassword); // 비밀번호 재입력칸
        checkNick = findViewById(R.id.checkNick); // 닉네임 중복검사 버튼
        endJoin = findViewById(R.id.endJoin); // 회원가입 완료 버튼
        noticeNick = findViewById(R.id.noticeNick);
        noticeRePassword = findViewById(R.id.noticeRePassword);

        // 닉네임 중복검사 버튼
        checkNick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new NickNetworkTask().execute();
            }
        });

        // 회원가입 버튼 클릭시 실행되는 메소드
        endJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nick = joinNick.getText().toString();
                pass = joinPassword.getText().toString();

                new JoinNetworkTask().execute();
            }
        });

        // 닉네임 editText에 데이터를 입력했을떄 실행되는 이벤트
        joinNick.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                joinNick.setBackgroundResource(R.drawable.border_layout);
                checkNick.setEnabled(true);
                bCheckNick = false;
                endJoin.setEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });// end 닉네임 editText에 데이터를 입력했을떄 실행되는 이벤트

        // 비밀번호 editText에 데이터를 입력했을떄 실행되는 이벤트
        joinPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                joinPassword.setBackgroundResource(R.drawable.border_layout);
                String chPassword = joinPassword.getText().toString();
                boolean chp = isValidPassword(chPassword);
                if(chp){
                    joinPassword.setBackgroundResource(R.drawable.ok_border_layout);
                    bCheckPassword = true;
                    if(bCheckNick == true && bCheckPassword == true && bReCheckPassword == true){
                        endJoin.setEnabled(true);
                    } else {
                        endJoin.setEnabled(false);
                    }
                } else {
                    joinPassword.setBackgroundResource(R.drawable.border_layout);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        // 비밀번호 재입력 editText에 데이터를 입력했을떄 실행되는 이벤트
        joinRePassword.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String chPassword = joinPassword.getText().toString();
                joinRePassword.setBackgroundResource(R.drawable.border_layout);
                String chRePassword = joinRePassword.getText().toString();
                if(chPassword.equals(chRePassword)){
                    joinRePassword.setBackgroundResource(R.drawable.ok_border_layout);
                    noticeRePassword.setText("비밀번호와 같습니다.");
                    bReCheckPassword = true;
                    if(bCheckNick == true && bCheckPassword == true && bReCheckPassword == true){
                        endJoin.setEnabled(true);
                    } else {
                        endJoin.setEnabled(false);
                    }
                }else {
                    joinRePassword.setBackgroundResource(R.drawable.border_layout);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });



    } // end onCreate



    // 비밀번호 형식 확인 메소드
    private boolean isValidPassword(String nick){
        String regex = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
        return nick.matches(regex);
    }



    // 회원가입 통신
    private class JoinNetworkTask extends AsyncTask<Void, Void, String>{

        String serverUrl = "http://3.38.213.196/userJoin/userJoin.php";

        @Override
        protected String doInBackground(Void... voids) {
            OkHttpClient client = new OkHttpClient();

            RequestBody body = new FormBody.Builder()
                    .add("phone", phoneNumber)
                    .add("email", email)
                    .add("nick", nick)
                    .add("pass", pass)
                    .build();

            Request request = new Request.Builder()
                    .url(serverUrl)
                    .post(body)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (IOException e){
                e.printStackTrace();
                return null;
            }
        }
        // 토스트 메시지 출력 함수

        @Override
        protected void onPostExecute(String result){
            if(result != null){
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String status = jsonObject.getString("status");
                    if(status.equals("1")){
                        String nickname = jsonObject.getString("nickname");
                        String token = jsonObject.getString("token");
                        String userId = jsonObject.getString("user_id");
                        String email = jsonObject.getString("email");

                        // 쉐어드 프피퍼런스에 정보 저장
                        SharedPreferences sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("nickname", nickname);
                        editor.putString("token", token);
                        editor.putString("userId", userId);
                        editor.putString("email", email);
                        editor.putString("url", "null");
                        editor.apply();

                        showToastMessage("회원가입이 완료되었습니다.");
                        Intent intent = new Intent(UserLogin.this, main.class);
                        startActivity(intent);
                    } else if(status.equals("2")){
                        showToastMessage("회원가입이 실패하였습니다.");
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }

        }
    }
    private void showToastMessage(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    // 닉네임 중복검사 통신
    private class NickNetworkTask extends AsyncTask<Void, Void, String>{

        @Override
        protected String doInBackground(Void... voids) {
            String nick = joinNick.getText().toString();
            NetworkManager networkManager = new NetworkManager();
            String serverUrl = "http://3.38.213.196/userJoin/nickCheck.php?nick=" + nick;
            return networkManager.fetchDataFromServer(serverUrl);
        }

        @Override
        protected void onPostExecute(String result){
            if(result.equals("1")){
                noticeNick.setText("사용이 가능한 닉네임 입니다.");
                joinNick.setBackgroundResource(R.drawable.ok_border_layout);
                bCheckNick = true;
                if(bCheckNick == true && bCheckPassword == true && bReCheckPassword == true){
                    endJoin.setEnabled(true);
                } else {
                    endJoin.setEnabled(false);
                }
            } else if(result.equals("2")){
                noticeNick.setText("중복된 닉네임 입니다.");
            }
        }

    }

}