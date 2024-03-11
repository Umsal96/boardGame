package com.example.boardgame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.w3c.dom.Text;

public class findPwAuth extends AppCompatActivity {

    private EditText inputEmail;
    private EditText inputAuth;
    private Button sendEmail;
    private Button checkAuth;
    private Button toFindPw;
    private TextView noticeEmail;
    private TextView noticeAuth;

    String sendAuth;

    // 버튼의 배경색을 하늘색으로 설정
    int skyBlueButton = Color.parseColor("#3498DB");
    // 버튼의 배경색을 회색으로 설정 (비활성화 상태에서의 색상)
    int grayButton = Color.parseColor("#CCCCCC");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_pw_auth);
        RequestQueue queue = Volley.newRequestQueue(this);
        inputEmail = findViewById(R.id.inputEmail);
        inputAuth = findViewById(R.id.inputAuth);
        sendEmail = findViewById(R.id.sendEmail);
        checkAuth = findViewById(R.id.checkAuth);
        toFindPw = findViewById(R.id.toFindPw);
        noticeEmail = findViewById(R.id.noticeEmail);
        noticeAuth = findViewById(R.id.noticeAuth);

        sendEmail.setBackgroundColor(grayButton);
        checkAuth.setBackgroundColor(grayButton);
        toFindPw.setBackgroundColor(grayButton);

        toFindPw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = inputEmail.getText().toString();
                Intent intent = new Intent(findPwAuth.this, findPw.class);
                intent.putExtra("email", email);
                startActivity(intent);
            }
        });

        // 인증번호 입력칸에 6글자 까지만 입력할 수 있게함
        InputFilter maxLengthFilter = new InputFilter.LengthFilter(6);

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

        inputAuth.setFilters(new InputFilter[]{maxLengthFilter, noSpaceFilter});

        sendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = inputEmail.getText().toString();
                String baseURl = "http://3.38.213.196/userJoin/sendEmail.php";
                String url = baseURl + "?email=" + email;
                System.out.println(url);
                StringRequest stringRequest = new StringRequest(
                    Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if (response.equals("2")) {
                                noticeEmail.setText("인증번호 전송이 실패했습니다.");
                            } else {
                                noticeEmail.setText("인증번호가 전송되었습니다.");
                                inputEmail.setBackgroundResource(R.drawable.ok_border_layout);
                                sendAuth = response.toString();
                                System.out.println("Auth : " + sendAuth);
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    });
                queue.add(stringRequest);
            }
        });

        // 이메일 입력할때 이벤트
        inputEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                inputEmail.setBackgroundResource(R.drawable.border_layout);
                String email = inputEmail.getText().toString();
                if(isValidEmail(email)){ // 입력된 글자의 형식이 이메일 형식이면
                    sendEmail.setEnabled(true);
                    sendEmail.setBackgroundColor(skyBlueButton);
                    noticeEmail.setText("인증번호를 요청해주세요");
                } else { // 이메일 형식이 아니면
                    sendEmail.setEnabled(false);
                    sendEmail.setBackgroundColor(grayButton);
                    noticeEmail.setText("올바른 이메일 형식을 입력해주세요");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // 인증번호를 입력할때 이벤트
        inputAuth.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                inputAuth.setBackgroundResource(R.drawable.border_layout);
                String auth = inputAuth.getText().toString();
                int textLength = auth.length();

                // 입력된 숫자가 6자리 일때 버튼 활성화
                if(textLength == 6){
                    checkAuth.setBackgroundColor(skyBlueButton);
                    checkAuth.setEnabled(true);
                    noticeAuth.setText("올바른 인증번호를 입력해주세요");
                } else { // 입력된 숫자가 6자리 미만 일때
                    checkAuth.setBackgroundColor(grayButton);
                    checkAuth.setEnabled(false);

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        checkAuth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = inputAuth.getText().toString(); // 이볅한 인증번호를 input 번수에 넣음
                System.out.println("authCode : " + sendAuth); // 전송된 인증번호 출력

                if(sendAuth.equals(input)){ // 전송된 인증번호와 입력된 인증번호가 같을때
                    noticeAuth.setText("인증이 완료되었습니다.");
                    toFindPw.setEnabled(true);
                    toFindPw.setBackgroundColor(skyBlueButton);
                    inputAuth.setBackgroundResource(R.drawable.ok_border_layout);
                    inputEmail.setEnabled(false);
                    inputAuth.setEnabled(false);
                } else {
                    noticeAuth.setText("올바른 인증번호를 입력해 주세요");
                }

            }
        });

    } // end onCreate

    // 이메일 형식 확인 메소드
    private boolean isValidEmail(String email){
        String regex = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"; // 간단한 이메일 형식 정규 표현식
        return email.matches(regex);
    }
}