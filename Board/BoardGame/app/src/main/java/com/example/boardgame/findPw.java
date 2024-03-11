package com.example.boardgame;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

public class findPw extends AppCompatActivity {

    private EditText inputPassword;
    private EditText inputRePassword;
    private TextView noticePassword;
    private TextView noticeRePassword;
    private Button endPassword;

    // 버튼의 배경색을 하늘색으로 설정
    int skyBlueButton = Color.parseColor("#3498DB");
    // 버튼의 배경색을 회색으로 설정 (비활성화 상태에서의 색상)
    int grayButton = Color.parseColor("#CCCCCC");
    private RequestQueue queue;
    String email;
    String pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_pw);
        queue = Volley.newRequestQueue(this);

        Intent intent = getIntent();
        email = intent.getStringExtra("email");

        // Ui 연결
        inputPassword = findViewById(R.id.inputPassword);
        inputRePassword = findViewById(R.id.inputRePassword);
        noticePassword = findViewById(R.id.noticePassword);
        noticeRePassword = findViewById(R.id.noticeRePassword);
        endPassword = findViewById(R.id.endPassword);

        endPassword.setBackgroundColor(grayButton);

        // 이메일 존재 여부 확인용 통신
        String baseUrl = "http://3.38.213.196/userJoin/emailCheck.php";
        String url = baseUrl + "?email=" + email;

        System.out.println("url : " + url);

        StringRequest stringRequest = new StringRequest(
            Request.Method.GET, url,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    // 이메일로 유저를 찾을 수 없음
                    if (response.equals("1")) {
                        // 해당하는 사용자가 없습니다.
                        AlertDialog.Builder builder = new AlertDialog.Builder(findPw.this);
                        builder.setTitle("오류")
                            .setMessage("이메일로 유저를 찾을 수 없음")
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent1 = new Intent(findPw.this, Login.class);
                                    startActivity(intent1);
                                }
                            });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
        queue.add(stringRequest);

        endPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pass = inputPassword.getText().toString();
                new FindPasswordTask().execute();
            }
        });


        // 신규 비밀번호 입력시 발생하는 이벤트
        inputPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                inputPassword.setBackgroundResource(R.drawable.border_layout);
                String chPassword = inputPassword.getText().toString();
                boolean chp = isValidPassword(chPassword);
                if(chp){
                    inputPassword.setBackgroundResource(R.drawable.ok_border_layout);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // 신규 비밀번호 재입력시 발생하는 이벤트
        inputRePassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String chPassword = inputPassword.getText().toString();
                inputRePassword.setBackgroundResource(R.drawable.border_layout);
                String chRePassword = inputRePassword.getText().toString();
                if(chPassword.equals(chRePassword)){
                    inputRePassword.setBackgroundResource(R.drawable.ok_border_layout);
                    noticeRePassword.setText("비밀번호와 같습니다.");
                    endPassword.setEnabled(true);
                    endPassword.setBackgroundColor(skyBlueButton);
                } else {
                    endPassword.setBackgroundColor(grayButton);
                    endPassword.setEnabled(false);

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    } // end onCreate

    private class FindPasswordTask extends AsyncTask<Void, Void, Integer>{

        String serverUrl = "http://3.38.213.196/userJoin/rePassword.php";

        @Override
        protected Integer doInBackground(Void... voids) {
            OkHttpClient client = new OkHttpClient();

            RequestBody body = new FormBody.Builder()
                    .add("email", email)
                    .add("pass", pass)
                    .build();
            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url(serverUrl)
                    .post(body)
                    .build();

            try{
                okhttp3.Response response = client.newCall(request).execute();
                return response.code();
            } catch (IOException e){
                e.printStackTrace();
                return -1;
            }
        }

        @Override
        protected void onPostExecute(Integer statusCode){
            if(statusCode != null && statusCode.equals(200)){
                System.out.println("비밀번호 수정 성공");
                showToastMessage("비밀번호 찾기 성공");
                Intent intent = new Intent(findPw.this, Login.class);
                startActivity(intent);
            } else {
                System.out.println("비밀번호 수정 실패");
            }
        }
    }

    private void showToastMessage(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    // 비밀번호 형식 확인 메소드
    private boolean isValidPassword(String nick){
        String regex = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
        return nick.matches(regex);
    }
}