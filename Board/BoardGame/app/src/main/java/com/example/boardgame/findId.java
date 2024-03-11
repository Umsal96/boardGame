package com.example.boardgame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class findId extends AppCompatActivity {

    private TextView textView8;
    private Button button;
    String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_id);

        textView8 = findViewById(R.id.textView8);
        button = findViewById(R.id.button);

        Intent intent = getIntent();
        phone = intent.getStringExtra("phone");
        System.out.println("phone : " + phone);
        new NetworkTask().execute();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(findId.this, Login.class);
                startActivity(intent1);
            }
        });
    }

    // 아이디를 찾기를 하기위한 통신
    private class NetworkTask extends AsyncTask<Void, Void, String>{

        @Override
        protected String doInBackground(Void... voids) {
            OkHttpClient client = new OkHttpClient();

            String url = "http://3.38.213.196/userJoin/findId.php?phone="+phone;

            System.out.println(url);
            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .build();

            try{
                Response response = client.newCall(request).execute();
                return response.body().string();

            } catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result){
            System.out.println(result);
            if(result != null){
                try{
                    JSONObject jsonObject = new JSONObject(result);
                    String status = jsonObject.getString("status");
                    System.out.println("status : " + status);
                    if(status.equals("1")){
                        String email = jsonObject.getString("email");
                        textView8.setText("이메일은 : " + email + " 입니다.");
                    } else if(status.equals("2")){
                        textView8.setText("해당하는 사용자가 없습니다.");
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }
    }
}