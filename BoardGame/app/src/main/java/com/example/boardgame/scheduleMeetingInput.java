package com.example.boardgame;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.service.quicksettings.Tile;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class scheduleMeetingInput extends AppCompatActivity {

    private EditText scheduleTitle; // 제목 작성
    private EditText peopleNum; // 인원 설정
    private ImageButton backPage; // 뒤로 가기 버튼
    private TextView scheduleDate; // 날짜를 보여줌
    private TextView scheduleTime; // 시간을 보여줌
    private TextView placeName; // 장소의 이름을 보여줌
    private TextView placeAddress; // 장소의 주소를 보여줌
    private TextView peopleNotion; // 인원수 보여줌
    private ImageView scheduleDateIcon; // 달력 아이콘
    private ImageView scheduleTimeIcon; // 시계 아이콘
    private ImageView placeIcon; // 위치 아이콘
    private Button inputSchedule; // 모임 등록 버튼
    private Calendar calendar;
    private EditText inputSearch; // 장소 입력 창
    private Button input; // 장소 검색용 버튼
    String formattedDate1; // mysql 에 전송용 포맷
    String formattedTime1; // mysql 에 전송용 포맷
    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_meeting_input);

        calendar = Calendar.getInstance();

        Intent intent = getIntent();
        id = intent.getIntExtra("id", 0);
        System.out.println("id : " + id);
        String cafeName = intent.getStringExtra("cafeName");
        String cafeAddress = intent.getStringExtra("cafeAddress");
        String x = intent.getStringExtra("x");
        String y = intent.getStringExtra("y");
        String maxNum = intent.getStringExtra("maxNum");
        String currentNum = intent.getStringExtra("currentNum");

        scheduleTitle = findViewById(R.id.scheduleTitle);
        peopleNum = findViewById(R.id.peopleNum);
        backPage = findViewById(R.id.backPage);
        scheduleDate = findViewById(R.id.scheduleDate);
        scheduleTime = findViewById(R.id.scheduleTime);
        placeName = findViewById(R.id.placeName);
        placeAddress = findViewById(R.id.placeAddress);
        peopleNotion = findViewById(R.id.peopleNotion);
        scheduleDateIcon = findViewById(R.id.scheduleDateIcon);
        scheduleTimeIcon = findViewById(R.id.scheduleTimeIcon);
        placeIcon = findViewById(R.id.placeIcon);
        inputSchedule = findViewById(R.id.inputSchedule);
        inputSearch = findViewById(R.id.inputSearch);
        input = findViewById(R.id.input);

        placeName.setText(cafeName); // 카페 이름 설정
        placeAddress.setText(cafeAddress); // 카페 주소 설정

        peopleNotion.setText("정원 (2 ~ "+ maxNum +")");

        if(savedInstanceState != null){
            String Sid = savedInstanceState.getString("id");
            String Title = savedInstanceState.getString("Title");
            String Date = savedInstanceState.getString("Date");
            String Time = savedInstanceState.getString("Time");
            String current = savedInstanceState.getString("current");
            String register = savedInstanceState.getString("register");

            id = Integer.parseInt(Sid);
            scheduleTitle.setText(Title);
            scheduleDate.setText(Date);
            scheduleTime.setText(Time);
            peopleNotion.setText(current);
            peopleNum.setText(register);
        }

        scheduleDateIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        scheduleTimeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog();
            }
        });

        input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String search = inputSearch.getText().toString();
                Intent intent1 = new Intent(scheduleMeetingInput.this, kakaoMap.class);
                intent1.putExtra("search", search);
                intent1.putExtra("where", "schedule");
                intent1.putExtra("num", maxNum);
                startActivity(intent1);
                finish();
            }
        });

        // 정보를 입력하는 버튼
        inputSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int checkNum = Integer.parseInt(maxNum); // 최대값
                int inputNum = Integer.parseInt(peopleNum.getText().toString());

                if(inputNum < 1 || inputNum > checkNum){
                    System.out.println("인원수를 조정해주세요");
                    AlertDialog.Builder builder = new AlertDialog.Builder(scheduleMeetingInput.this);
                    builder.setMessage("인원수를 조정해 주세요")
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    peopleNum.setText("");
                                    peopleNum.requestFocus();
                                }
                            });
                    // 다이얼로그 표시
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }

                SharedPreferences sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
                String Uid = sharedPreferences.getString("userId", "");

                inputScheduleMeeting(id, Uid, scheduleTitle.getText().toString(), formattedDate1, formattedTime1,
                        maxNum, placeName.getText().toString(), placeAddress.getText().toString(), y, x);
            }
        });
    } // end onCreate

    // onSaveInstanceState 메소드를 사용해서 데이터를 임시 저장함
    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        String Title = scheduleTitle.getText().toString(); // 제목
        String Date = scheduleDate.getText().toString(); // 날짜
        String Time = scheduleTime.getText().toString(); // 시간
        String current = peopleNotion.getText().toString(); // 가능 인원수
        String register = peopleNum.getText().toString(); // 최대 인원수

        Log.d("MyTag", "Title: " + Title);
        Log.d("MyTag", "Date: " + Date);

        String sId = String.valueOf(id);

        outState.putString("id", sId);
        outState.putString("Title", Title);
        outState.putString("Date", Date);
        outState.putString("Time", Time);
        outState.putString("current", current);
        outState.putString("register", register);
    }

    private void inputScheduleMeeting(int id, String uid, String scheduleTitle, String formattedDate1, String formattedTime1,
                                      String maxNum, String placeName, String placeAddress, String y, String x){
        String serverUrl = "http://3.38.213.196/schedule/inputSchedule.php";

        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("meeting_seq", String.valueOf(id))
                .add("user_seq", uid)
                .add("schedule_title", scheduleTitle)
                .add("schedule_date", formattedDate1)
                .add("schedule_time", formattedTime1)
                .add("schedule_member_max", maxNum)
                .add("schedule_place_name", placeName)
                .add("schedule_place_address", placeAddress)
                .add("schedule_lat", y)
                .add("schedule_lnt", x)
                .build();

        Request request = new Request.Builder()
                .url(serverUrl)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String responseBodyString = response.body().string();
                            Toast.makeText(getApplicationContext(), "입력이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                            System.out.println(responseBodyString);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
                Intent intent = new Intent(scheduleMeetingInput.this, getMeeting.class);
                intent.putExtra("id", id);
                startActivity(intent);
            }
        });
    }

    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        // 선택된 날짜 처리
                        // year, month, dayOfMonth 변수를 사용하여 선택된 날짜를 처리합니다.
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        // scheduleDate TextView에 선택된 날짜 표시
                        SimpleDateFormat dateFormat = new SimpleDateFormat("MM 월 d일 (E)", Locale.getDefault());
                        SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        String formattedDate = dateFormat.format(calendar.getTime());
                        formattedDate1 = dateFormat1.format(calendar.getTime());
                        scheduleDate.setText(formattedDate);
                    }
                },
                // 초기 날짜 설정 (현재 날짜)
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        );

        // 오늘 이전의 날짜는 선택 불가능하게 설정
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);

        datePickerDialog.show();
    }
    private void showTimePickerDialog() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);

                        // scheduleTime TextView에 선택된 시간 표시
                        SimpleDateFormat timeFormat = new SimpleDateFormat("a h:mm", Locale.getDefault());
                        SimpleDateFormat timeFormat1 = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                        String formattedTime = timeFormat.format(calendar.getTime());
                        formattedTime1 = timeFormat1.format(calendar.getTime());
                        scheduleTime.setText(formattedTime);
                    }
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                false //(true로 설정하면 AM/PM 형식 사용)
        );

        timePickerDialog.show();
    }

}