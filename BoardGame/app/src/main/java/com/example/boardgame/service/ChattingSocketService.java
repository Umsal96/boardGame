package com.example.boardgame.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.boardgame.vo.FirstSocket;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
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

    private boolean sendCheck = false; // 처음 보내는 정보가 보내졌는지 확인
    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        // 데이터 수신을 위한 별로듸 스레드 생성
        if(intent != null){
            String json = intent.getStringExtra("json");
            System.out.println("service json: " + json);
//            getMeetingList(userId);

            if(socket == null || !socket.isConnected()){
                // 소켓 연결 설정 (한 번만 실행)
                Thread connectionThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            socket = new Socket();
                            socket.connect(new InetSocketAddress("3.38.213.196", 9998)); // 서버 IP 주소로 변경
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

            String actionJson = intent.getStringExtra("actionJson");
            System.out.println("service actionJson : " + actionJson);

            if(actionJson != null){
                sendAction(actionJson);
            }
            return START_STICKY;
        } else {
            System.out.println("인텐트가 없습니다.");
        }
        return START_NOT_STICKY;
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
                        Intent intent = new Intent("com.example.boardgame.ACTION_DATA_RECEIVED");
                        intent.putExtra("receivedData", actionJson);
                        LocalBroadcastManager.getInstance(ChattingSocketService.this).sendBroadcast(intent);
                    }
                    System.out.println("Socket disconnected. Stopping data receiving thread.");
                } catch (IOException e){
                    e.printStackTrace();
                }
            }
        });
        receiveThread.start();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
