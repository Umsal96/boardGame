package com.example.boardgame.service;

import android.app.NotificationChannel;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.graphics.drawable.IconCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.boardgame.R;
import com.example.boardgame.getMeeting;
import com.example.boardgame.item.UserItem;
import com.example.boardgame.utility.ChattingNotificationChannelManager;
import com.example.boardgame.utility.JsonToGetData;
import com.example.boardgame.vo.FirstSocket;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ChattingSocketService extends Service {

    private final Handler handler = new Handler(Looper.getMainLooper());
    Socket socket;
    private DataInputStream dis;
    private DataOutputStream dos;
    String CHANNEL_ID = "1002";
    int meetingId = 0;
    Bitmap bitmap;
    public static ArrayList<Integer> enterUser = new ArrayList<>();
    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        // 데이터 수신을 위한 별로듸 스레드 생성
        if(intent != null){
            // 로그인 정보와 가입된 모임들 리스트를 json 형태로 가공된 정보를 받아온것
            String json = intent.getStringExtra("json");
            System.out.println("service json: " + json);

//            System.out.println("서비스 위치 정보 : " + meetingId);
//            getMeetingList(userId);

            if(socket == null || !socket.isConnected()){
                // 소켓 연결 설정 (한 번만 실행)
                Thread connectionThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            socket = new Socket();
                            socket.connect(new InetSocketAddress("192.168.219.106", 9998)); // 서버 IP 주소로 변경
                            dos = new DataOutputStream(socket.getOutputStream());
                            dis = new DataInputStream(socket.getInputStream());
                            System.out.println("통신 시작");
                            // 데이터를 계속해서 받는 스레드 시작
                            sendInfo(json);

                            // 데이터 받는 스레드 시작
                            startDataReceivingThread();
                        } catch (IOException e){
                            e.printStackTrace();
                        }
                    }
                });
                connectionThread.start();
            }
//            int meetingId = intent.getIntExtra("meetingId", 0);
//
//            if(meetingId != 0){
//                System.out.println("서비스 위치 정보 : " + meetingId);
//            }

            String actionJson = intent.getStringExtra("actionJson");
            System.out.println("service actionJson : " + actionJson);
            // 채팅방에 입장했다는 것을 json 형태로 가공해서 받아온것
            String jsonEnter = intent.getStringExtra("jsonEnter");
            if(jsonEnter != null){
                System.out.println("jsonEnter : " + jsonEnter);
                JsonObject jsonObject = JsonParser.parseString(jsonEnter).getAsJsonObject();
                String action = jsonObject.get("action").getAsString();
                if("enter".equals(action)){
                    meetingId = jsonObject.get("meetingSeq").getAsInt();
                    sendEnter(jsonEnter);
                } else if ("out".equals(action)) {
                    meetingId = jsonObject.get("meetingId").getAsInt();
                    sendEnter(jsonEnter);
                }
            }

            if(actionJson != null){
                sendAction(actionJson);
            }
            return START_STICKY;
        } else {
            System.out.println("인텐트가 없습니다.");
        }
        return START_NOT_STICKY;
    }
    // 채팅방에 입장했을때 소켓통신에 전송
    private void sendEnter(String json){
        System.out.println("sendEnter 실행");
        Thread enterThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    dos.writeUTF(json);
                    dos.flush();
                } catch (IOException e){
                    e.printStackTrace();;
                }
            }
        });
        enterThread.start();
    }

    // 메시지 전송 버튼을 눌렀을때, 모임에 가입했을때 모임에서 탈퇴했을때 실행
    private void sendAction(String json){
        System.out.println("sendAction 실행");
        Thread sendThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    dos.writeUTF(json);
                    dos.flush();
                } catch (IOException e){
                    e.printStackTrace();
                }
            }
        });
        sendThread.start();
    }

    // 처음 연결될때 유저의 정보를 보내는 메소드
    private void sendInfo(String json){
        System.out.println("sendInfo 메소드 실행");
        if(dos != null){ // dos -> 송신 스트림이 있을때
            try{
                dos.writeUTF(json);
                dos.flush();
            } catch (IOException e){
                e.printStackTrace();
            }
        } else {
            System.out.println("dos 가 없습니다./");
        }
    }

    private void startDataReceivingThread(){
        System.out.println("startDataReceivingThread 메소드 시작");
        Thread receiveThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (socket.isConnected()){
                        System.out.println("데이터 받기 시작");
                        String actionJson = dis.readUTF();
                        System.out.println("chattingSocketService + 받은 데이터 : " + actionJson);
                        JsonObject jsonObject = JsonParser.parseString(actionJson).getAsJsonObject();
                        Intent intent = new Intent("com.example.boardgame.ACTION_DATA_RECEIVED");
                        intent.putExtra("receivedData", actionJson);
                        LocalBroadcastManager.getInstance(ChattingSocketService.this).sendBroadcast(intent);
                        String action = jsonObject.get("action").getAsString();
                        if("chat".equals(action)){
                            int userSeq = jsonObject.get("userSeq").getAsInt();
                            int meetingSeq = jsonObject.get("meetingSeq").getAsInt();
                            String content = jsonObject.get("content").getAsString();
                            getOtherUserInfo(userSeq, meetingSeq, content);
                        } else if ("enter".equals(action)) {
//                            System.out.println("action enter");
                            int userSeq = jsonObject.get("userSeq").getAsInt();
                            enterUser.add(userSeq);
                        } else if ("out".equals(action)) {
                            int userSeq = jsonObject.get("userSeq").getAsInt();
                            for (int i = 0; i < enterUser.size(); i++) {
                                if(enterUser.get(i) == userSeq){
                                    enterUser.remove(i);
                                    break;
                                }
                            }
                        }

                    }
                    System.out.println("Socket disconnected. Stopping data receiving thread.");
                } catch (IOException e){
                    e.printStackTrace();
                }
            }
        });
        receiveThread.start();
    }

    // 노티피케이션에 들어갈 유저 정보를 가져옴
    private void getOtherUserInfo(int userId, int meetingSeq, String content) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://3.38.213.196/chat/getUser.php").newBuilder();
        urlBuilder.addQueryParameter("userId", String.valueOf(userId)); // url 쿼리에 id 라는 메개변수 추가
        String url = urlBuilder.build().toString();
        JsonToGetData jtg = new JsonToGetData();

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
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    System.out.println(responseData);

                    UserItem item = jtg.jsonGetUserInfo(responseData);
//                    System.out.println("유저정보를 가져온 서비스 정보 : " + meetingId);
                    System.out.println("서비스 위치 정보 startData : " + meetingId);
                    // meetingSeq -> json 데이터에서 받아온 meetingId
                    // meetingId -> 현재 위치
                    if(meetingSeq != meetingId){
                        System.out.println("노티피케이션 실행");
                        viewNotify(meetingSeq, content, item);
                    }
                }
            }
        });
    }

    // 노티피 케이션 표시
    private void viewNotify(int meetingId, String message, UserItem item) {
        ChattingNotificationChannelManager channelManager = ChattingNotificationChannelManager.getInstance(this);
        NotificationChannel channel = channelManager.getChannel();

        Intent resultIntent = new Intent(this, getMeeting.class);
        resultIntent.putExtra("id", meetingId);
        resultIntent.putExtra("where", 3);

        PendingIntent resultPendingIntent = PendingIntent.getActivities(this, 0,
                new Intent[]{resultIntent}, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(item.getUserNick())
                .setContentText(message)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(resultPendingIntent);

        // 프로필 사진이 설정 되어 있을 경우
        if (item.getUserUrl() != null && !item.getUserUrl().equals("null") && !item.getUserUrl().equals("")) {
            Glide.with(this)
                    .asBitmap()
                    .load("http://3.38.213.196" + item.getUserUrl())
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            // 노티피케이션에 프로필 이미지 추가
                            builder.setLargeIcon(resource);

                            // 알람 표시
                            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(ChattingSocketService.this);
                            if (ActivityCompat.checkSelfPermission(ChattingSocketService.this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                                // TODO: Consider calling
                                //    ActivityCompat#requestPermissions
                                // here to request the missing permissions, and then overriding
                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                //                                          int[] grantResults)
                                // to handle the case where the user grants the permission. See the documentation
                                // for ActivityCompat#requestPermissions for more details.
                                return;
                            }
                            notificationManagerCompat.notify(1002, builder.build());
                        }
                    });
        } else { // 프로필 사진이 설정 되어있지 않을 경우
            Bitmap defaultBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.img2);
            builder.setLargeIcon(defaultBitmap);

            // 알림을 표시
            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            notificationManagerCompat.notify(1002, builder.build());
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
