package com.example.boardgame.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.boardgame.Adapter.WaitingAdapter;
import com.example.boardgame.R;
import com.example.boardgame.item.WaitingItem;
import com.example.boardgame.utility.JsonToData;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WaitingDialog extends Dialog {

    WaitingAdapter waitingAdapter;
    private RecyclerView waitRecyclerView;
    ArrayList<WaitingItem> wi;
    Activity activity;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.waiting_dialog);

        waitRecyclerView = findViewById(R.id.waitRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        waitRecyclerView.setLayoutManager(linearLayoutManager);
        waitingAdapter = new WaitingAdapter(wi);
        // 수락 버튼을 클릭했을때
        waitingAdapter.setOnWaitingAcceptClickListener(new WaitingAdapter.OnWaitingAcceptClickListener() {
            @Override
            public void onWaitingAcceptClick(int userSeq, int meetingSeq, int position) {
                WaitingAdapter.ViewHolder holder = (WaitingAdapter.ViewHolder) waitRecyclerView.findViewHolderForAdapterPosition(position);
                System.out.println("수락한 아이템 위치 : " + position);
                System.out.println("수락한 유저의 고유 아이디 : " + userSeq);
                System.out.println("수락한 미팅의 고유 아이디 : " + meetingSeq);
                acceptWaiting(meetingSeq, userSeq); // 수락시 인터넷 통신
            }
        });
        // 거절 버튼을 클릭했을때
        waitingAdapter.setOnWaitingRefuseClickListener(new WaitingAdapter.OnWaitingRefuseClickListener() {
            @Override
            public void onWaitingRefuseClick(int userSeq, int meetingSeq, int position) {
                WaitingAdapter.ViewHolder holder = (WaitingAdapter.ViewHolder) waitRecyclerView.findViewHolderForAdapterPosition(position);
                System.out.println("거절된 아이템 위치 : " + position);
                System.out.println("거절한 유저의 고유 아이디 : " + userSeq);
                System.out.println("거절한 미팅의 고유 아이디 : " + meetingSeq);
                refuseWaiting(meetingSeq, userSeq);
            }
        });
        waitRecyclerView.setAdapter(waitingAdapter);
    }

    public WaitingDialog(@NonNull Context context, ArrayList<WaitingItem> wi, FragmentActivity activity){
        super(context);
        this.wi = wi;
        this.activity = activity;
    }

    // 대기자를 수락했을때 실행되는 메소드
    private void acceptWaiting(int meetingSeq, int userSeq){
        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://3.38.213.196/meeting/intoMeeting.php").newBuilder();
        urlBuilder.addQueryParameter("userId", String.valueOf(userSeq)); // url 쿼리에 id 라는 메개변수 추가
        urlBuilder.addQueryParameter("meetingId", String.valueOf(meetingSeq)); // url 쿼리에 id 라는 메개변수 추가
        String url = urlBuilder.build().toString(); // 최종 url 생성

        // Request 객체 생성
        Request request = new Request.Builder()
                .url(url)
                .build();

        // client 객체 생성
        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.isSuccessful()){
                    String responseData = response.body().string();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getUserWaitingList(meetingSeq);
                        }
                    });
                }
            }
        });
    }

    // 대기자를 거절했을때 실행되는 메소드
    private void refuseWaiting(int meetingSeq, int userSeq){
        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://3.38.213.196/waiting/refuseWaiting.php").newBuilder();
        urlBuilder.addQueryParameter("userId", String.valueOf(userSeq)); // url 쿼리에 id 라는 메개변수 추가
        urlBuilder.addQueryParameter("meetingId", String.valueOf(meetingSeq)); // url 쿼리에 id 라는 메개변수 추가
        String url = urlBuilder.build().toString(); // 최종 url 생성

        // Request 객체 생성
        Request request = new Request.Builder()
                .url(url)
                .build();

        // client 객체 생성
        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.isSuccessful()){
                    String responseData = response.body().string();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getUserWaitingList(meetingSeq);
                        }
                    });
                }
            }
        });
    }

    //
    private void getUserWaitingList(int meetingSeq){
        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://3.38.213.196/waiting/getWaitingUserList.php").newBuilder();
        urlBuilder.addQueryParameter("id", String.valueOf(meetingSeq)); // url 쿼리에 id 라는 메개변수 추가 모임 고유 아이디
        String url = urlBuilder.build().toString();
        JsonToData js = new JsonToData();

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
                if (response.isSuccessful()){
                    String responseData = response.body().string();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            wi.clear();
                            wi = js.jsonToWaitingUserList(responseData);
                            waitingAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });
    }
}
