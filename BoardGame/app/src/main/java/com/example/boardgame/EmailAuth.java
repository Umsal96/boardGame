package com.example.boardgame;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.boardgame.utility.NetworkManager;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import org.w3c.dom.Text;

public class EmailAuth extends AppCompatActivity {

    private EditText inputEmail; // 이메일 입력
    private EditText inputAuth; // 인증번호 입력
    private Button checkEmail; // 이메일 확인 버튼
    private Button checkAuth; // 인증번호 확인 버튼
    private Button toJoin; // 정보 입력 페이지로 이동용 버튼
    private TextView noticeEmail; // 결과 출력 텍스트뷰
    private TextView noticeAuth; // 결과 출력 텍스트뷰

    // 색깔 지정 하늘색
    int skyBlueButton = Color.parseColor("#3498DB");
    // 색깔 지정 회색
    int grayButton = Color.parseColor("#CCCCCC");

    boolean boolEmail = false; // 이메일 인증 요청 버튼을 눌렀는지 안눌렀는지 확인용
    private RequestQueue queue;
    String phone;

    String sendAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_auth);
        queue = Volley.newRequestQueue(this);
        // ui 연결
        inputEmail = findViewById(R.id.inputEmail); // 이메일 입력
        inputAuth = findViewById(R.id.inputAuth); // 인증번호 입력
        checkEmail = findViewById(R.id.checkEmail); // 이메일 확인 버튼
        checkAuth = findViewById(R.id.checkAuth); // 인증번호 확인 버튼
        noticeEmail = findViewById(R.id.noticeEmail); // 결과 출력 텍스트뷰
        noticeAuth = findViewById(R.id.noticeAuth); // 결과 출력 텍스트뷰
        toJoin = findViewById(R.id.toJoin); // 정보 입력 페이지로 이동하는 버튼

        checkEmail.setBackgroundColor(grayButton); // 인증 요청 버튼의 색을 회색으로 변경
        checkAuth.setBackgroundColor(grayButton); // 인증번호 확인 버튼의 색을 회색으로 변경
        toJoin.setBackgroundColor(grayButton);

        Intent intent = getIntent();
        phone = intent.getStringExtra("phoneNumber");

        toJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = inputEmail.getText().toString();
                Intent intent1 = new Intent(EmailAuth.this, UserLogin.class);
                intent1.putExtra("phone", phone);
                intent1.putExtra("email", email);
                startActivity(intent1);
            }
        });

        checkEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = inputEmail.getText().toString();
                String baseUrl = "http://3.38.213.196/userJoin/emailCheck.php";
                String url = baseUrl + "?email=" + email;

                StringRequest stringRequest = new StringRequest(
                    Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if (response.equals("1")) {
                                noticeEmail.setText("인증번호가 전송되었습니다.");
                                inputEmail.setBackgroundResource(R.drawable.ok_border_layout);
                                boolEmail = true;
                                handleEmailSent();
                            } else if (response.equals("2")) {
                                noticeEmail.setText("이메일이 중복되었습니다.");
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // 에러 처리
                        }
                    });
                queue.add(stringRequest);

            }
        });

        // 번호 확인 버튼 클릭시 실행되는 이벤트
        checkAuth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = inputAuth.getText().toString(); // 입력한 인증번호를 Input 번수에 넣음
                System.out.println("authCode : " + sendAuth); // 전송된 인증번호 출력

                if(sendAuth.equals(input)){
                    noticeAuth.setText("인증이 완료되었습니다.");
                    toJoin.setEnabled(true);
                    toJoin.setBackgroundColor(skyBlueButton);
                    inputAuth.setBackgroundResource(R.drawable.ok_border_layout);
                    inputEmail.setEnabled(false);
                    inputAuth.setEnabled(false);
                } else {
                    noticeAuth.setText("올바른 인증번호를 입력해주세요.");
                }
            }
        });

        // 인증번호 입력칸에 글자를 입력했을때 실행되는 이벤트
        inputAuth.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                inputAuth.setBackgroundResource(R.drawable.border_layout);
                String auth = inputAuth.getText().toString(); // 입력한 인증번호
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

        // 입력을 6글자만 받게 하기 위한 필터
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

        inputEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                inputEmail.setBackgroundResource(R.drawable.border_layout);
                String email = inputEmail.getText().toString();
                boolEmail = false;
                if(isValidEmail(email) && boolEmail == false){
                    checkEmail.setEnabled(true);
                    checkEmail.setBackgroundColor(skyBlueButton);
                    noticeEmail.setText("인증 요청을 해주세요");
                } else if (isValidEmail(email) && boolEmail == true) {
                    checkEmail.setEnabled(true);
                    checkEmail.setBackgroundColor(skyBlueButton);
                    noticeEmail.setText("인증번호가 전송되었습니다.");
                } else {
                    checkEmail.setEnabled(false);
                    checkEmail.setBackgroundColor(grayButton);
                    noticeEmail.setText("올바른 이메일 형식을 입력해주세요");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    } // end onCreate

    // 이메일 형식 확인 메소드
    private boolean isValidEmail(String email){
        String regex = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"; // 간단한 이메일 형식 정규 표현식
        return email.matches(regex);
    }

    private void handleEmailSent(){
        // '1'을 받을 경우 처리하는 기능
        String email = inputEmail.getText().toString();
        String baseUrl = "http://3.38.213.196/userJoin/sendEmail.php";
        String url = baseUrl + "?email=" + email;

        StringRequest newRequest = new StringRequest(
                Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.equals('2')){
                            noticeEmail.setText("인증번호 전송이 실패했습니다..");
                        } else {

                            checkEmail.setEnabled(true);
                            checkEmail.setBackgroundColor(R.drawable.ok_border_layout);
                            sendAuth = response.toString(); // 전송된 인증번호
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
        queue.add(newRequest);
    }

    private void showToastMessage(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}