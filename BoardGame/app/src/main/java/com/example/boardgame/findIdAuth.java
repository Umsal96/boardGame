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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.boardgame.utility.ApiClient;
import com.example.boardgame.utility.ApiService;
import com.example.boardgame.utility.ResponseModel;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

public class findIdAuth extends AppCompatActivity {

    private EditText findPhone; // 전화번호 입력
    private EditText findAuth; // 인증번호 입력
    private Button sendAuth; // 인증번호 전송 버튼
    private Button checkAuth; // 인증번호 확인 버튼
    private Button toFindId; // 아이디 확인 페이지로 가는 버튼
    private TextView noticePhone; // 전화번호 설명용 글
    private TextView noticeAuth; // 인증번호 설명용 글

    String authCode; // 핸드폰으로 보낸 6글자 인증번호 저장용 변수

    // 버튼의 배경색을 하늘색으로 설정
    int skyBlueButton = Color.parseColor("#3498DB");
    // 버튼의 배경색을 회색으로 설정 (비활성화 상태에서의 색상)
    int grayButton = Color.parseColor("#CCCCCC");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_id_auth);

        findPhone = findViewById(R.id.findPhone);
        findAuth = findViewById(R.id.findAuth);
        sendAuth = findViewById(R.id.sendAuth);
        checkAuth = findViewById(R.id.checkAuth);
        toFindId = findViewById(R.id.toFindId);
        noticePhone = findViewById(R.id.noticePhone);
        noticeAuth = findViewById(R.id.noticeAuth);

        sendAuth.setBackgroundColor(grayButton);
        checkAuth.setBackgroundColor(grayButton);
        toFindId.setBackgroundColor(grayButton);

        // 인증번호 입력칸에 6글자 까지만 입력할 수 있게함
        InputFilter maxLengthFilter1 = new InputFilter.LengthFilter(6);

        // 전화번호 입력칸에 11글자까지만 입력할수 있게 하고, 스페이스바가 입력되지 않게 함
        InputFilter maxLengthFilter = new InputFilter.LengthFilter(11);

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

        findPhone.setFilters(new InputFilter[]{maxLengthFilter, noSpaceFilter}); // 전화번호 입력을 11 자리까지만 받고 공백을 받지 않는다
        findAuth.setFilters(new InputFilter[]{maxLengthFilter1, noSpaceFilter}); // 인증번호 입력을 6 자리 까지만 받고 공백은 받지 않는다.

        // 전화번호 입력칸
        findPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                findPhone.setBackgroundResource(R.drawable.border_layout);
                String phone = findPhone.getText().toString();
                int textLength = phone.length();

                // 입력된 숫자가 11자리고, 처음 3자리가 '010'인 경우 버튼 활성화
                if(textLength == 11 && phone.startsWith("010")){
                    sendAuth.setBackgroundColor(skyBlueButton);
                    sendAuth.setEnabled(true);
                } else {
                    sendAuth.setBackgroundColor(grayButton);
                    sendAuth.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // 인증번호 입력칸
        findAuth.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                findAuth.setBackgroundResource(R.drawable.border_layout);
                String auth = findAuth.getText().toString();
                int textLength = auth.length();

                // 입력된 숫자가 6자리 일때 번호확인 버튼 활성화
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

        sendAuth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new NetworkTask().execute();
            }
        });

        checkAuth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputCode = findAuth.getText().toString();
                System.out.println("inputCode : " + inputCode);
                System.out.println("authCode : " + authCode);

                if(authCode.equals(inputCode)){
                    noticeAuth.setText("인증이 완료되었습니다.");
                    toFindId.setEnabled(true);
                    toFindId.setBackgroundColor(skyBlueButton);
                    findAuth.setBackgroundResource(R.drawable.ok_border_layout);
                    findPhone.setEnabled(false);
                    findAuth.setEnabled(false);
                } else {
                    noticeAuth.setText("올바른 인증번호를 입력해주세요");
                }
            }
        });

        toFindId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = findPhone.getText().toString();
                Intent intent = new Intent(findIdAuth.this, findId.class);
                intent.putExtra("phone", phone);
                startActivity(intent);
            }
        });

    } // end onCreate

    // 핸드폰에 인증번호 전송 통신
    private class NetworkTask extends AsyncTask<Void, Void, ResponseModel>{

        @Override
        protected ResponseModel doInBackground(Void... voids) {
            String phone = findPhone.getText().toString();
            ApiService apiService = ApiClient.getApiService();

            // GET 요청 보내기
            Call<ResponseModel> call = apiService.sendSMS(phone);

            try{
                Response<ResponseModel> response = call.execute();
                if(response.isSuccessful()){
                    System.out.println("인증번호 전송에 성공했습니다.");
                    noticePhone.setText("인증번호 전송에 성공했습니다.");
                    findPhone.setBackgroundResource(R.drawable.ok_border_layout);
                    return response.body();
                } else {
                    System.out.println("인증번호 전송에 실패했습니다.");
                    return null;
                }
            } catch (IOException e){
                e.printStackTrace();
                return null;
            }

        }

        @Override
        protected void onPostExecute(ResponseModel responseModel){
            if(responseModel != null){
                if(responseModel.getStatusCode().equals("202")){
                    authCode = responseModel.getAuthCode();
                    System.out.println("Auth Code : " + authCode);
                }
            }
        }
    }
}