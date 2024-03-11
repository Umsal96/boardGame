package com.example.boardgame.network.meeting;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.boardgame.getMeeting;
import com.example.boardgame.item.ScheduleItem;
import com.example.boardgame.utility.JsonToData;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NetSchedule {

    public interface ScheduleCallback {
        void onScheduleResponse(ScheduleItem scheduleItem);
    }
    // 일정 등록 메소드
    public void inputSchedule(int meetingSeq, int scheduleSeq, int userId, Activity activity, ScheduleCallback callback){

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
                    // UI 스레드에서 작업을 수행하기 위해 runOnUiThread 사용
                    System.out.println("NetSchedule 내의 json");
                    System.out.println(responseData);
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(activity, "참가가 완료 되었습니다.", Toast.LENGTH_SHORT).show();
                            JsonToData jt = new JsonToData();
                            ScheduleItem scheduleItem = jt.jsonToGetSchedule(responseData);

                            callback.onScheduleResponse(scheduleItem);
                        }
                    });
                }
           } // 결과 성공했을때
        });
    } // end inputSchedule

    // 일정 취소 메소드
    public void exitSchedule(int scheduleSeq, int userId, Activity activity, ScheduleCallback callback){
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
                    System.out.println(" 현재 남은 인원수 : " + responseData);
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(activity, "참가가 취소 되었습니다.", Toast.LENGTH_SHORT).show();
                            JsonToData jt = new JsonToData();
                            ScheduleItem scheduleItem = jt.jsonToGetSchedule(responseData);

                            // Pass the ScheduleItem to the callback
                            callback.onScheduleResponse(scheduleItem);
                        }
                    });
                }
            }// endResponse
        });

    }// end exitSchedule

    // 일정 삭제 메소드
    public void deleteSchedule(int scheduleSeq, int meetingSeq, Activity activity){
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
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(activity, "일정이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
                Intent intent = new Intent(activity, getMeeting.class);
                intent.putExtra("where", 1);
                intent.putExtra("id", meetingSeq);
                activity.startActivity(intent);
            }
        });
    } // end deleteSchedule

}
