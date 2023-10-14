package com.example.boardgame.Adapter;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.boardgame.R;
import com.example.boardgame.getMeeting;
import com.example.boardgame.item.ScheduleItem;
import com.example.boardgame.item.ScheduleMemberItem;
import com.example.boardgame.utility.OnItemClickListener;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ViewHolder> {
    private ArrayList<ScheduleItem> scheduleItems; // 아이템 어레이리스트 선언
    private ArrayList<ScheduleMemberItem> scheduleMemberItems;
    private int userId; // 유저 고유 아이디
    private OnItemClickListener listener;
    ArrayList<ScheduleMemberItem> sm = new ArrayList<>();

    // 뷰 홀더에 ui와 변수를 연결
    public static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView scheduleDate, scheduleDateCount, scheduleCafeAddress, scheduleNum, scheduleNumString, scheduleItemTitle, scheduleCafeName;
        private Button scheduleAttend, scheduleCancel, scheduleUser;
        public ViewHolder(@NonNull View itemView){
            super(itemView);
            scheduleDate = itemView.findViewById(R.id.scheduleDate);
            scheduleDateCount = itemView.findViewById(R.id.scheduleDateCount);
            scheduleCafeAddress = itemView.findViewById(R.id.scheduleCafeAddress);
            scheduleNum = itemView.findViewById(R.id.scheduleNum);
            scheduleNumString = itemView.findViewById(R.id.scheduleNumString);
            scheduleItemTitle = itemView.findViewById(R.id.scheduleItemTitle);
            scheduleCafeName = itemView.findViewById(R.id.scheduleCafeName);
            scheduleAttend = itemView.findViewById(R.id.scheduleAttend);
            scheduleCancel = itemView.findViewById(R.id.scheduleCancel);
            scheduleUser = itemView.findViewById(R.id.scheduleUser);

        }

    }
    public ScheduleAdapter(ArrayList<ScheduleItem> DataSet, ArrayList<ScheduleMemberItem> smt, int userId, OnItemClickListener listener){
        this.scheduleItems = DataSet;
        this.scheduleMemberItems = smt;
        this.userId = userId;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ScheduleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.schedule_item, parent, false);
        ScheduleAdapter.ViewHolder viewHolder = new ScheduleAdapter.ViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleAdapter.ViewHolder holder, int position){
        ScheduleItem item = scheduleItems.get(position);

//        System.out.println("유저의 고유 아이디 : " + userId);

        // 해당 아이템의 고유 일정 아이디와 일정멤버리스트에서 해당 일정 고유 아이디가 같으면 1차적으로 다른 리스트로 옮김
        // 해당 일정과 맴버를 거르기 위해서함
        for (int i = 0; i < scheduleMemberItems.size(); i++) {
            if(scheduleMemberItems.get(i).getSchedule_seq() == item.getScheduleSeq()) {
                sm.add(scheduleMemberItems.get(i));
            }
        }

        for (int i = 0; i < sm.size(); i++) {
            if(sm.get(i).getUser_seq() == userId){
                System.out.println("모임에 참가가되어있습니다.");
                holder.scheduleAttend.setVisibility(View.GONE);
                holder.scheduleCancel.setVisibility(View.VISIBLE);
            }
        }


        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat("MM 월 d일 (E)", Locale.getDefault());

        SimpleDateFormat timeInputFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        SimpleDateFormat timeOutputFormat = new SimpleDateFormat("a h:mm", Locale.getDefault());

        String formatted;
        String formatTime;

        SimpleDateFormat inputFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date scheduleDateTime = null;

        try {
            Date scheduleTime = timeInputFormat.parse(item.getSchedule_time());
            formatTime = timeOutputFormat.format(scheduleTime);

            Date scheduleDate = inputFormat.parse(item.getSchedule_date());
            formatted = outputFormat.format(scheduleDate);

            scheduleDateTime = inputFormat1.parse(item.getSchedule_date() + " " + item.getSchedule_time());

            // 현재 날짜와 시간을 가져오기
            Calendar currentDate = Calendar.getInstance();

            // 주어진 시간과 날짜를 Calendar 객체로 변환
            Calendar scheduleCalendar = Calendar.getInstance();
            scheduleCalendar.setTime(scheduleDateTime);

            // 날짜 차이 계산
            long timeDifferenceMillis = scheduleCalendar.getTimeInMillis() - currentDate.getTimeInMillis();

            if(timeDifferenceMillis >= 24 * 60 * 60 * 1000){
                // 1일 이상 남았을 경우
                long dayDifference = timeDifferenceMillis / (24 * 60 * 60 * 1000);
                holder.scheduleDateCount.setText(dayDifference + " 일 남았습니다.");
            }else if (timeDifferenceMillis >= 60 * 60 * 1000) {
                // 1시간 이상 남았을 경우
                long hoursDifference = timeDifferenceMillis / (60 * 60 * 1000);
                holder.scheduleDateCount.setText(hoursDifference + " 시간 남았습니다.");
            } else if (timeDifferenceMillis >= 60 * 1000) {
                // 1분 이상 남았을 경우
                long minutesDifference = timeDifferenceMillis / (60 * 1000);
                holder.scheduleDateCount.setText(minutesDifference + " 분 남았습니다.");
            } else {
                // 예약 시간이 현재 시간보다 이전인 경우
                holder.scheduleDateCount.setText("이미 시작한 일정입니다.");
            }

        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        holder.scheduleDate.setText(formatted + " " + formatTime);
        holder.scheduleCafeAddress.setText(item.getSchedule_place_address());
        holder.scheduleCafeName.setText(item.getSchedule_place_name());
        int max = item.getSchedule_member_max();
        int current = item.getSchedule_member_current();
        int ue = max - current;
        String smax = String.valueOf(max);
        String scurrent = String.valueOf(current);
        String sue = String.valueOf(ue);
        holder.scheduleNum.setText(scurrent + "/" + smax);
        holder.scheduleNumString.setText(sue + " 자리 남음");
        holder.scheduleItemTitle.setText(item.getScheduleTitle());

        holder.scheduleUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(item.getScheduleSeq());
            }
        });
        holder.scheduleCancel.setOnClickListener(new View.OnClickListener() { // 취소 버튼 클릭
            @Override
            public void onClick(View v) {
                System.out.println("클릭한 일정 고유 아이디: " + item.getScheduleSeq());
                System.out.println("클릭한 유저의 고유 아이디 : " + userId);

                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("알림");
                builder.setMessage("일정 참가를 취소하시겠습니까?");
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        exitSchedule(v, item.getScheduleSeq());
                        String count = holder.scheduleNum.getText().toString(); // 1/20
                        String[] parts = count.split("/");
                        if(parts.length == 2){
                            int currentValue = Integer.parseInt(parts[0]); // 첫 번째 부분을 정수로 변환
                            currentValue--; // 1을 뺌
                            if(currentValue <= 0){
                                deleteSchedule(v, item.getScheduleSeq(), item.getMeetingSeq());
                            }
                            String updateCount = currentValue + "/" + parts[1]; // 새로운 문자열 생성
                            holder.scheduleNum.setText(updateCount);
                        }
                        String sn = holder.scheduleNumString.getText().toString();
                        String[] snParts = sn.split(" ");
                        if(snParts.length > 1){
                            int currentValue = Integer.parseInt(snParts[0]);
                            currentValue++;
                            String updateText = currentValue + " " + snParts[1] + " " + snParts[2];
                            holder.scheduleNumString.setText(updateText);
                        }
                        holder.scheduleCancel.setVisibility(View.GONE);
                        holder.scheduleAttend.setVisibility(View.VISIBLE);
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        // 일정에 참가함
        holder.scheduleAttend.setOnClickListener(new View.OnClickListener() { // 참가 버튼
            @Override
            public void onClick(View v) {

                System.out.println("모임 고유 아이디 : " + item.getMeetingSeq());
                System.out.println("일정 고유 아이디 : " + item.getScheduleSeq());
                System.out.println("유저 고유 아이디 : " + userId);

                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("알림");
                builder.setMessage("일정에 참가 하시겠습니까?");
                builder.setPositiveButton("참가", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        inputSchedule(v, item.getMeetingSeq(), item.getScheduleSeq());
                        String count = holder.scheduleNum.getText().toString(); // 1/20
                        String[] parts = count.split("/");
                        if(parts.length == 2){
                            int currentValue = Integer.parseInt(parts[0]); // 첫 번째 부분을 정수로 변환
                            currentValue++; // 1을 더함
                            String updateCount = currentValue + "/" + parts[1]; // 새로운 문자열 생성
                            holder.scheduleNum.setText(updateCount);
                        }
                        String sn = holder.scheduleNumString.getText().toString();
                        String[] snParts = sn.split(" ");
                        if(snParts.length > 1){
                            int currentValue = Integer.parseInt(snParts[0]);
                            currentValue--;
                            String updateText = currentValue + " " + snParts[1] + " " + snParts[2];
                            holder.scheduleNumString.setText(updateText);
                        }
                        holder.scheduleAttend.setVisibility(View.GONE);
                        holder.scheduleCancel.setVisibility(View.VISIBLE);
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();

            }
        }); // endClick event
    }

    @Override
    public int getItemCount(){
        if(scheduleItems == null){
            return 0;
        }
        return scheduleItems.size();
    }

    // 일정 삭제 메소드
    private void deleteSchedule(View v, int scheduleSeq, int meetingSeq){
        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://3.38.213.196/schedule/deleteSchedule.php").newBuilder();
        urlBuilder.addQueryParameter("schedule", String.valueOf(scheduleSeq)); // url 쿼리에 schedule 라는 메개변수 추가 일정 고유 아이디
        String url = urlBuilder.build().toString();

        Request request = new Request.Builder()
                .url(url)
                .build();

        OkHttpClient client = new OkHttpClient();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String responseData = response.body().string();
                System.out.println(responseData);
                Intent intent = new Intent(v.getContext(), getMeeting.class);
                intent.putExtra("where", 1);
                intent.putExtra("id", meetingSeq);
                v.getContext().startActivity(intent);
            }
        });
    }

    // 일정에서 나가는 메소드
    private void exitSchedule(View v, int scheduleSeq){
        // 모임에서 탈퇴하기 위해 db 에서 데이터를 삭제
        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://3.38.213.196/schedule/deleteScheduleMember.php").newBuilder();
        urlBuilder.addQueryParameter("schedule", String.valueOf(scheduleSeq)); // url 쿼리에 schedule 라는 메개변수 추가 일정 고유 아이디
        urlBuilder.addQueryParameter("userId", String.valueOf(userId)); // url 쿼리에 userId 라는 메개변수 추가 유저 고유 아이디
        String url = urlBuilder.build().toString();

        Request request = new Request.Builder()
                .url(url)
                .build();

        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.isSuccessful()){
                    String responseData = response.body().string();
                    System.out.println(responseData);
                    ((Activity) v.getContext()).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(v.getContext(), "참가가 취소되었습니다..", Toast.LENGTH_SHORT).show();

                        }
                    });
                }
            }// endResponse
        });
    }

    // 일정에 참가하는 메소드
    private void inputSchedule(View v, int meetingSeq, int scheduleSeq){
        // 모임에 참가하기위해 db 에 정보를 저장함
        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://3.38.213.196/schedule/inputScheduleMember.php").newBuilder();
        urlBuilder.addQueryParameter("meeting", String.valueOf(meetingSeq)); // url 쿼리에 meeting 라는 메개변수 추가 모임 고유 아이디
        urlBuilder.addQueryParameter("schedule", String.valueOf(scheduleSeq)); // url 쿼리에 schedule 라는 메개변수 추가 일정 고유 아이디
        urlBuilder.addQueryParameter("userId", String.valueOf(userId)); // url 쿼리에 userId 라는 메개변수 추가 유저 고유 아이디
        String url = urlBuilder.build().toString();

        Request request = new Request.Builder()
                .url(url)
                .build();

        OkHttpClient client = new OkHttpClient();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.isSuccessful()){
                    String responseData = response.body().string();
                    System.out.println(responseData);
                    ((Activity) v.getContext()).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(v.getContext(), "참가가 완료되었습니다.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } // 결과 성공했을때
        });
    }

}
