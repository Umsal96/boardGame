package com.example.boardgame;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.boardgame.Adapter.ChattingAdapter;
import com.example.boardgame.item.ChattingItem;
import com.example.boardgame.item.UserItem;
import com.example.boardgame.service.ChattingSocketService;
import com.example.boardgame.utility.JsonToData;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class getMeetingCheat extends Fragment {

    private EditText chatText;
    private Button sendChat;
    private RecyclerView chatRecyclerView;
    ArrayList<UserItem> ui = new ArrayList<>();
    String receivedData;
    ArrayList<ChattingItem> ci = new ArrayList<>();
    int meetingId = 0;
    UserItem my = new UserItem();
    ChattingAdapter chattingAdapter;
    private final BroadcastReceiver dataReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            receivedData = intent.getStringExtra("receivedData");
            System.out.println("테스트 : " + my.getUserNick() + " : " + receivedData);
            JsonObject jsonObject = JsonParser.parseString(receivedData).getAsJsonObject();
            int userSeq = jsonObject.get("userSeq").getAsInt();
            String content = jsonObject.get("content").getAsString();
            String date = jsonObject.get("chatTime").getAsString();
            int meetingSeq = jsonObject.get("meetingSeq").getAsInt();
            System.out.println("time : " + date);
//            chattingItem.setUser_seq();
            System.out.println("meetingId = " + meetingId);
            if(meetingId == meetingSeq){
                ChattingItem chattingItem = new ChattingItem();
                chattingItem.setUser_seq(userSeq);
                chattingItem.setMeeting_seq(meetingSeq);
                chattingItem.setMessage_content(content);
                chattingItem.setMessage_read(ui.size());
                chattingItem.setMessage_date(date);
                for (int i = 0; i < ui.size(); i++) {
                    if(ui.get(i).getUserSeq() == userSeq){
                        chattingItem.setUser_url(ui.get(i).getUserUrl());
                        chattingItem.setUser_nickname(ui.get(i).getUserNick());
                        break;
                    }
                }
                System.out.println("유저 고유 아이디 : " + chattingItem.getUser_seq());
                System.out.println("미팅 고유 아이디 : " + chattingItem.getMeeting_seq());
                System.out.println("닉네임 : " + chattingItem.getUser_nickname());
                System.out.println("프로필 : " + chattingItem.getUser_url());
                System.out.println("날짜 : " + chattingItem.getMessage_date());
                int newPosition = ci.size();
                ci.add(chattingItem);
                chattingAdapter.notifyItemInserted(newPosition);
            }
        }
    };
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_get_meeting_cheat, container, false);

        chatText = view.findViewById(R.id.chatText);
        sendChat = view.findViewById(R.id.sendChat);
        chatRecyclerView = view.findViewById(R.id.chatRecyclerView);

        // 데이터를 수신하기 위해 BroadcastReceiver를 등록
        IntentFilter intentFilter = new IntentFilter("com.example.boardgame.ACTION_DATA_RECEIVED");
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(dataReceiver, intentFilter);

        // 쉐어드 프리퍼런스에 있는 유저의 아이디를 가져옴
        // 1. 쉐어드 프리퍼런스를 사용하기위해 UserData 라는 이름의 파일을 가져옴
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("UserData", Context.MODE_PRIVATE);
        // 쉐어드 프리퍼런스에 있는 userId 라는 키값을 가지고 있는 값을 가져오고 가져온 값을 int형으로 변환함
        int userId = Integer.parseInt(sharedPreferences.getString("userId", ""));

        Bundle bundle = getArguments();

        if(bundle != null){
             meetingId = bundle.getInt("id", 0);
        }

        // 같은 채팅방의 닉네임과 프로필을 가져옴
        getUsersInfo(meetingId, userId);

        System.out.println("getMeetingCheat meetingId: "+ meetingId);

        getChattingList(meetingId);

        sendChat.setEnabled(false);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        chatRecyclerView.setLayoutManager(linearLayoutManager);
        chattingAdapter = new ChattingAdapter(ci, userId);
        chatRecyclerView.setAdapter(chattingAdapter);
        chatText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            // 에딧텍스트가 수정되는 이벤트
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String content = chatText.getText().toString();
                int contentLength = content.length();
                if (contentLength == 0){
                    sendChat.setEnabled(false);
                } if (contentLength >= 1){
                    sendChat.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        int jMeetingSeq = meetingId;
        sendChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Gson gson = new Gson();
                Map<String, Object> map = new HashMap<>();
                ChattingItem chattingItem = new ChattingItem();
                String content = chatText.getText().toString();
                map.put("userSeq", userId);
                map.put("action", "chat");
                map.put("meetingSeq", jMeetingSeq);
                String chatTime = getCurrentDateTime();
                map.put("chatTime", chatTime);
                map.put("content", content);
                String json = gson.toJson(map);

                Intent serviceIntent = new Intent(getContext(), ChattingSocketService.class);
                serviceIntent.putExtra("actionJson", json);
                getContext().startService(serviceIntent);
                chattingItem.setMeeting_seq(jMeetingSeq);
                chattingItem.setUser_seq(userId);
                chattingItem.setMessage_content(content);
                chattingItem.setMessage_read(ui.size());
                chattingItem.setMessage_date(chatTime);
                chattingItem.setUser_nickname(my.getUserNick());
                chattingItem.setUser_url(my.getUserUrl());
                int newPosition = ci.size();
                ci.add(chattingItem);
                chattingAdapter.notifyItemInserted(newPosition);
                chatText.setText("");
            }
        });

        return view;
    }

    @Override
    public void onDestroy(){
        // 메모리 누수를 방지하기 위해 BroadcastReceiver를 등록 해제
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(dataReceiver);
        super.onDestroy();
    }

    // 현재 시간을 가져오기
    private String getCurrentDateTime(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // 현재 시간을 가져옴
        Date currentDate = new Date();
        return dateFormat.format(currentDate);
    }
    // 채팅방 유저의 닉네임과 url 가져옴
    private void getUsersInfo(int meetingId, int userId){
        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://3.38.213.196/chat/getUsersInfo.php").newBuilder();
        urlBuilder.addQueryParameter("meetingId", String.valueOf(meetingId)); // url 쿼리에 id 라는 메개변수 추가
        String url = urlBuilder.build().toString();
        JsonToData jt = new JsonToData();

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
                    ui.clear();
                    ui.addAll(jt.jsonUserList(responseData));
                    for (int i = 0; i < ui.size(); i++) {
                        if(ui.get(i).getUserSeq() == userId){
                            my.setUserUrl(ui.get(i).getUserUrl());
                            my.setUserNick(ui.get(i).getUserNick());
                            ui.remove(i);
                            break;
                        }
                    }
                }
            }
        });
    }

    // 채팅 리스트 가져오기
    private void getChattingList(int meetingId){
        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://3.38.213.196/chat/getChattingList.php").newBuilder();
        urlBuilder.addQueryParameter("meetingId", String.valueOf(meetingId)); // url 쿼리에 id 라는 메개변수 추가
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
                if (response.isSuccessful()){
                    String responseData = response.body().string();
                    System.out.println(responseData);
                }
            }
        });

    }
}