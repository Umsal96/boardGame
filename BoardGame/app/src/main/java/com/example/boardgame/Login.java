package com.example.boardgame;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.boardgame.service.TestService;
import com.example.boardgame.service.socketService;
import com.example.boardgame.utility.NotificationChannelManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/*
* 1. 버튼및 에딧텍스트 변수를 작성
* 2. 작성한 변수를 ui 와연결
* */

public class Login extends AppCompatActivity {
    private Button join; // 회원가입 버튼
    private Button login; // 로그인 버튼
    private Button findId; // 아이디 찾기 버튼
    private Button findPassword; // 비밀번호 찾기 버튼
    private EditText inputEmail; // 이메일 입력
    private EditText inputPassword; // 비밀번호 입력
    String email; // 로그인 할때의 이메일을 저장하는 변수
    String pass; // 로그인 할때의 비밀번호를 저장하는 변수
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // ui 연결
        join = findViewById(R.id.join); // 회원가입 버튼
        login = findViewById(R.id.login); // 로그인 버튼
        inputEmail = findViewById(R.id.inputEmail); // 이메일 입력 칸
        inputPassword = findViewById(R.id.inputPassword); // 비밀번호 입력 칸
        findId = findViewById(R.id.findId); // 아이디 찾기 페이지로 이동하는 버튼
        findPassword = findViewById(R.id.findPassword); // 비밀번호 찾기 페이지로 이동하는 버튼

        // SharedPreferences에서 저장된 토큰을 가져옴
        // 이 코드는 "UserData"라는 이름의 SharedPreferences 객체를 MODE_PRIVATE 모드로 생성합니다.
        SharedPreferences sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
        // 쉐어드 프리퍼런스에서 token 이라는 키값을 가진 벨류값을 가져옴
        String saveToken = sharedPreferences.getString("token", "");
        String userIdString = sharedPreferences.getString("userId", null);

        int userId = -1; // Default value in case parsing fails

        if (userIdString != null && !userIdString.isEmpty()) {
            try {
                userId = Integer.parseInt(userIdString);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }


        // 서비스 시작
        Intent serviceIntent = new Intent(this, socketService.class);
        serviceIntent.putExtra("user_id", userId);
        startService(serviceIntent);

        // 토큰 검사 및 자동 로그인 처리
        // saveToken에 값이 저장되어있는지 확인
        if(!saveToken.isEmpty()){
            if(isTokenValid(saveToken)){ // 가져온 토큰이 유효한 값인지 확인 시간도 확인
                // 토큰이 유효하면 자동 로그인
                // 자동로그인이 되면 main 페이지로 이동한다고 인텐트에 저장
                Intent intent = new Intent(Login.this, main.class);
                startActivity(intent); // 인텐트를 실행
                showToastMessage("자동 로그인"); // 토스트 에 자동로그인이 나오도록 설정
                finish(); // 생명주기를 끝냄
            }
        }

        // 회원가입 버튼을 눌렀을때 약관 페이지로 이동
        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 인텐트에 현제 페이지를 담고 이동할 페이지인 ViewTerms의 컨탠트를 담음
                Intent intent = new Intent(Login.this, ViewTerms.class);
                // 인텐트에 담긴 페이지로 이동
                startActivity(intent);
            }
        });

        // 비밀번호 찾기 페이지로 이동
        findPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, findPwAuth.class);
                startActivity(intent);
            }
        });

        // 아이디 찾기 페이지로 이동
        findId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, findIdAuth.class);
                startActivity(intent);
            }
        });

        // 로그인 버튼
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("클릭되었습니다.");
                email = inputEmail.getText().toString();
                pass = inputPassword.getText().toString();

                new LoginNetworkTask().execute();
            }
        });

    } // end onCreate

    // JWT 토큰 유효성 검사
    private boolean isTokenValid(String token){
        try{
            String secretKey = "g3Zd9Rn$!C7HtP5m@Xw8NqA6fDvSbE1j";
            Jws<Claims> claimsJws = Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes()))
                    .build()
                    .parseClaimsJws(token);

            Claims claims = claimsJws.getBody();

            long currentTime = System.currentTimeMillis() / 1000;
            long expirationTime = claims.getExpiration().getTime() / 1000;

            // 토큰이 만료되지 않았으면 유효
            return expirationTime > currentTime;
        } catch (JwtException e){
            e.printStackTrace();
            return false;
        }
    }

    private class LoginNetworkTask extends AsyncTask<Void, Void, String> {
        String serverUrl = "http://3.38.213.196/userJoin/login.php";
        @Override
        protected String doInBackground(Void... voids) {
            OkHttpClient client = new OkHttpClient();

            RequestBody body = new FormBody.Builder()
                    .add("email", email)
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

        @Override
        protected void onPostExecute(String result){
            if(result != null){
                try{
                    JSONObject jsonObject = new JSONObject(result);
                    String status = jsonObject.getString("status");
                    if (status.equals("1")) {
                        String nickname = jsonObject.getString("nickname");
                        String token = jsonObject.getString("token");
                        String userId = jsonObject.getString("user_id");
                        String email = jsonObject.getString("email");
                        String url = jsonObject.getString("url");

                        // SharedPreferences 에 정보 저장
                        SharedPreferences sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("nickname", nickname);
                        editor.putString("token", token);
                        editor.putString("userId", userId);
                        editor.putString("email", email);
                        editor.putString("url", url);
                        editor.apply();

                        showToastMessage("로그인이 완료되었습니다.");
                        Intent intent = new Intent(Login.this, main.class);
                        intent.putExtra("nickname", nickname);
                        startActivity(intent);
                    } else if(status.equals("2")) {
                        showToastMessage("비밀번호가 일치하지 않습니다.");
                    } else if (status.equals("3")){
                        showToastMessage("해당하는 유저 정보가 없습니다.");
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }else {
                showToastMessage("서버 응답 오류");
            }

        }
    }
    // 토스트 메시지 출력 함수
    private void showToastMessage(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void checkNotificationPermission(Context context){
        // Check for notification permission
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            String[] permissions = {Manifest.permission.POST_NOTIFICATIONS};
            if(ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(Login.this, permissions, 0);
            } else {
                // 노티피케이션 채널 매니저를 초기화 하고 싱글톤 인스턴스를 생성합니다.
                NotificationChannelManager.getInstance(this);
            }
        }
    }
}