package com.example.boardgame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.boardgame.utility.ApiClient;
import com.example.boardgame.utility.ApiService;
import com.example.boardgame.utility.ResponseModel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import retrofit2.Call;
import retrofit2.Response;

// sms 인증 페이지
public class SMSAuth extends AppCompatActivity {

    private EditText joinPhone; // 전화번호 입력칸
    private EditText joinAuth; // 인증번호 입력칸
    private Button callAuth; // 인증 요청 버튼
    private Button checkAuth; // 인증번호 확인 버튼
    private Button toUserInfo; // 회원정보 입력 페이지로 이동 버튼
    private TextView noticePhone; // 전화번호 관련 메시지
    private TextView noticeAuth; // 인증번호 관련 메시지

    // 버튼의 배경색을 하늘색으로 설정
    int skyBlueButton = Color.parseColor("#3498DB"); // 하늘색의 색상코드 저장
    // 버튼의 배경색을 회색으로 설정 (비활성화 상태에서의 색상)
    int grayButton = Color.parseColor("#CCCCCC"); // 회색의 색상코드 저장
    String authCode; // 인증 저장용 변수

    @Override
    protected void onCreate(Bundle savedInstanceState) { // 화면이 나오면 실행됨
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smsauth); // activity_smsauth 레이아웃과 연결

        // ui 연결
        joinPhone = findViewById(R.id.joinPhone); // 전화번호 입력칸
        joinAuth = findViewById(R.id.joinAuth); // 인증번호 입력칸
        callAuth = findViewById(R.id.callAuth); // 인증 요청 버튼
        checkAuth = findViewById(R.id.checkAuth); // 인증 번호 확인 버튼
        toUserInfo = findViewById(R.id.toUserInfo); // 이메일 인증 페이지로 이동
        noticePhone = findViewById(R.id.noticePhone); // 전화번호 관련 메시지
        noticeAuth = findViewById(R.id.noticeAuth); // 인증번호 관련 메시지

        callAuth.setBackgroundColor(grayButton); // 인증 요청 버튼의 색을 회색으로 설정
        checkAuth.setBackgroundColor(grayButton); // 인증 번호 확인 버튼의 색을 회색으로 설정
        toUserInfo.setBackgroundColor(grayButton); // 이메일 인증 페이지로 이동하는 버튼의 색을 회색으로 설정

        // 인증번호 입력칸에 6글자 까지만 입력할 수 있게함
        InputFilter maxLengthFilter1 = new InputFilter.LengthFilter(6);

        // 전화번호 입력칸에 11글자까지만 입력할수 있게 하고, 스페이스바가 입력되지 않게 함
        InputFilter maxLengthFilter = new InputFilter.LengthFilter(11);
        // 스페이스바를 입력하지 못하도록 필터를 추가
        InputFilter noSpaceFilter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {
                // 스페이스바를 입력하면 무시하고 입력을 막음
                for (int i = start; i < end; i ++){
                    if(Character.isWhitespace(source.charAt(i))){
                        return ""; // 스페이스바 입력을 막기 위해 빈 문자열을 반환
                    }
                }
                return null; // 입력 허용
            }
        };

        // 전화번호 입력 칸 11 글자 까지 입력받고 공백을 받기 않게 필터 추가
        joinPhone.setFilters(new InputFilter[]{maxLengthFilter, noSpaceFilter});
        // 인증번호 입력 칸 6 글자 까지 입력받고 공백을 받기 않게 필터 추가
        joinAuth.setFilters(new InputFilter[]{maxLengthFilter1, noSpaceFilter});

        // 핸드폰 번호를 입력했을때 실행되는 이벤트
        joinPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            // 핸드폰 번호 칸이 수정되면 실행되는 이벤트
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 핸드폰 번호 입력 칸의 태두리를 빨간색으로 변경
                joinPhone.setBackgroundResource(R.drawable.border_layout);
                // 핸드폰 번호 입력칸의 글자를 문자열로 가져와 phone 변수에 넣음
                String phone = joinPhone.getText().toString();
                // 변수의 글자수를 변수에 넣음
                int textLength = phone.length();

                // 입력된 숫자가 11자리이고, 처음 3자리가 '010'인 경우 버튼 활성화
                if(textLength == 11 && phone.startsWith("010")){
                    callAuth.setBackgroundColor(skyBlueButton);
                    callAuth.setEnabled(true);
                }else {
                    callAuth.setBackgroundColor(grayButton);
                    callAuth.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        // 인증 번호를 입력했을때 6 글자를 입력했을때 입력칸의 배경색과 인증요청 버튼을 활성화 시킴
        joinAuth.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                joinAuth.setBackgroundResource(R.drawable.border_layout);
                String auth = joinAuth.getText().toString();
                int textLength = auth.length();

                // 입력된 숫자가 6자리 일때 버튼 활성화
                if(textLength == 6){
                    checkAuth.setBackgroundColor(skyBlueButton);
                    checkAuth.setEnabled(true);
                    noticeAuth.setText("올바른 인증번호를 입력해주세요");
                } else {
                    checkAuth.setBackgroundColor(grayButton);
                    checkAuth.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // 인증번호 확인 버튼
        checkAuth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputCode = joinAuth.getText().toString();
                System.out.println("inputCode : " + inputCode);
                System.out.println("authCode : " + authCode);

                if(authCode.equals(inputCode)){
                    noticeAuth.setText("인증이 완료되었습니다.");
                    toUserInfo.setEnabled(true);
                    toUserInfo.setBackgroundColor(skyBlueButton);
                    joinAuth.setBackgroundResource(R.drawable.ok_border_layout);
                    joinPhone.setEnabled(false);
                    joinAuth.setEnabled(false);
                } else {
                    noticeAuth.setText("올바른 인증번호를 입력해주세요.");
                }
            }
        });

        // 인증 요청 버튼
        callAuth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ConnectTask().execute();
            }
        });

        // 회원정보 입력 페이지로 이동
        toUserInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = joinPhone.getText().toString();
                showToastMessage("인증이 완료 되었습니다.");
                Intent intent = new Intent(SMSAuth.this, EmailAuth.class);
                intent.putExtra("phoneNumber", phone);
                startActivity(intent);
            }
        });


    } // end onCreate

    // 인증번호 전송 통신
    private class NetworkTask extends AsyncTask<Void, Void, ResponseModel>{

        @Override
        protected ResponseModel doInBackground(Void... voids) {
            String phone = joinPhone.getText().toString();
            // Retrofit 객체 생성
            ApiService apiService = ApiClient.getApiService();

            // GET 요청 보내기
            Call<ResponseModel> call = apiService.sendSMS(phone);

            try{
                Response<ResponseModel> response = call.execute();
                if(response.isSuccessful()){
                    System.out.println("인증번호 전송에 성공했습니다.");
                    joinPhone.setBackgroundResource(R.drawable.ok_border_layout);
                    return response.body();
                    // 가져온 데이터 처리
                } else {
                    System.out.println("인증번호 전송에 실패했습니다.");
                    // 에러 처리
                    return null;
                }
            } catch (IOException e){
                e.printStackTrace();
                // 네트워크 에러 처리
                return null;
            }
        }

        @Override
        protected void onPostExecute(ResponseModel responseModel){
            if(responseModel != null){
                if (responseModel.getStatusCode().equals("202")){
                    authCode = responseModel.getAuthCode();
                    System.out.println("Auth Code : " + authCode);
                }
            }
        }
    }

    // 전화번호 중복 여부 검사 통신
    private class ConnectTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            String phone = joinPhone.getText().toString();
            try{
                String phpScriptUrl = "http://3.38.213.196/userJoin/phoneCheck.php?phone=" + phone;
                URL url = new URL(phpScriptUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                StringBuilder response = new StringBuilder();

                while((line = reader.readLine()) != null){
                    response.append(line);
                }
                reader.close();
                connection.disconnect();

                return response.toString();
            } catch (Exception e) {
                e.printStackTrace();
                return "2";
            }
        }

        @Override
        protected void onPostExecute(String result){
            if(result.equals("1")){
                noticePhone.setText("인증번호를 전송합니다.");
                new NetworkTask().execute();
            }else if (result.equals("2")) {
                noticePhone.setText("전화번호가 중복되었습니다.");
            }
        }
    }
    // 토스트 메시지 출력 함수
    private void showToastMessage(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}