package com.example.boardgame.service;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.boardgame.R;
import com.example.boardgame.getMeeting;
import com.example.boardgame.utility.NotificationChannelManager;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
public class socketService extends Service {
    private final Handler handler = new Handler(Looper.getMainLooper());
    Socket socket;
    private DataInputStream dis;
    private DataOutputStream dos;
    int userId; // 로그인한 유저
    String CHANNEL_ID = "1001";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 데이터 수신을 위한 별도의 스레드 셍성
        // 알림생성

        if (intent != null) {
            userId = intent.getIntExtra("user_id", 0);

            if (socket == null || !socket.isConnected()) {
                // 소켓 연결 설절 (한 번만 실행)
                Thread connectionThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            socket = new Socket();
                            socket.connect(new InetSocketAddress("3.38.213.196", 9999)); // 서버 IP 주소로 변경
                            dos = new DataOutputStream(socket.getOutputStream());
                            dis = new DataInputStream(socket.getInputStream());
                            // 처음 연결할 때 userId 전송
                            sendUserId(userId);

                            // 데이터를 계속해서 받는 스레드 시작
                            startDataReceivingThread();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                connectionThread.start();
            }

            // 버튼 클릭할 때 정보 전송 (메소드로 구현)
            String action = intent.getStringExtra("action");
            int receiveUserId = intent.getIntExtra("userId", 0);
            int meetingId = intent.getIntExtra("meetingId", 0);
            System.out.println("인텐트로 받아온 행동 정보 : " + action);
            System.out.println("인텐트로 받아온 받아야 하는 사람의 고유 아이디 : " + receiveUserId);
            System.out.println("인텐트로 받아온 이동해야 하는 목표 미팅 고유 아이디 : " + meetingId);

            if (action != null) {
                sendAction(action, receiveUserId, meetingId);
            }

            return START_STICKY;
        }

        return START_NOT_STICKY;
    }

    // userId 전송 메소드
    private void sendUserId(int userId) {
        if (dos != null) {
            try {
                String message = "User ID " + userId;
                System.out.println(message);
                dos.writeInt(userId);
//                byte[] bytes = message.getBytes("UTF-8");

//                // 메시지 길이를 먼저 전성
//                dos.writeInt(bytes.length);
//                // 메시지 내용을 전송
//                dos.write(bytes, 0, bytes.length);
                // 출력 스트림 비우기
                dos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // 정보 전송 메소드
    private void sendAction(final String action, final int receiveUserId, final int meetingId) {
        if (dos != null) {
            Thread sendThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        int TransmitUserSeq = userId; // 보내는 사람
                        int ReceiveUserSeq = receiveUserId; // 받는 사람 (변경 필요)
                        String result = "";

                        if ("accept".equals(action)) {
                            result = "수락";
                        } else if ("refuse".equals(action)) {
                            result = "거절";
                        } else if ("join".equals(action)) {
                            result = "가입";
                        }

                        String message = ReceiveUserSeq + "," + result + "," + meetingId;
                        byte[] bytes = message.getBytes("UTF-8");
                        System.out.println("전송하는 데이터 : " + message);
                        // 메시지 길이를 먼저 전송
                        dos.writeInt(bytes.length);
                        // 메시지 내용을 전송
                        dos.write(bytes, 0, bytes.length);
                        // 출력 스트림 비우기
                        dos.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

            sendThread.start();
        }
    }

    private void startDataReceivingThread() {
        System.out.println("데이터를 받기 시작 바깥");
        Thread receiveThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        System.out.println("데이터를 받기 시작");
                        int receiveLength = dis.readInt();
                        System.out.println("받은 데이터 길이 : " + receiveLength);
                        if (receiveLength > 0) {
                            byte receiveBytes[] = new byte[receiveLength];
                            System.out.println("데이터 받은거 중간");

                            // 입력 스트림을 한 번 비우기
                            while (receiveLength > 0) {
                                int bytesRead = dis.read(receiveBytes, 0, receiveLength);
                                if (bytesRead == -1) {
                                    break; // EOF reached
                                }
                                receiveLength -= bytesRead;
                            }

                            System.out.println("receiveByte : " + receiveBytes);
                            String receiveMessage = new String(receiveBytes, "UTF-8");
                            System.out.println("서버에서 받은 데이터 : " + receiveMessage);
                            // 서버로부터 받은 데이터 처리
                            handleReceivedData(receiveMessage);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        receiveThread.start();
    }

    private void handleReceivedData(String data) {
        // 서버로부터 수신된 데이터 처리
        // 여기에서 UI 업데이트 등을 수행할 수 있습니다.
        System.out.println("받은 데이터: " + data);

        String result = null;
        int meetingId = 0;
        String message = null;
        String parts[] = data.split(",");
        if (parts.length == 2) {
            result = parts[0];
            meetingId = Integer.parseInt(parts[1]);
        }

        NotificationChannelManager channelManager = NotificationChannelManager.getInstance(getApplicationContext());
        NotificationChannel channel = channelManager.getChannel();

        Intent resultIntent = new Intent(this, getMeeting.class);
        resultIntent.putExtra("id", meetingId);
        resultIntent.putExtra("chPage", 1);
        if("가입".equals(result)){
            resultIntent.putExtra("sk", 1); // 1 이면 소켓에서 전송한 것 으로 가입한 유저의 리스트 다이얼로그를 보여주게 함
        } else {
            resultIntent.putExtra("sk", 0); // 0 이면 소켓에서 전송한 것 으로 가입한 유저의 리스트 다이얼로그를 안보여줘도 됨
        }

        if("가입".equals(result)){
            message = result + " 신청이 있습니다..";
        } else if ("수락".equals(result)) {
            message = "가입이 승인 되었습니다.";
        } else if ("거절".equals(result)) {
            message = "가입이 거절 되었습니다.";
        }

        PendingIntent resultPendingIntent = PendingIntent.getActivities(this, 0,
                new Intent[]{resultIntent}, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("알람")
                .setContentText(message)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(resultPendingIntent);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManagerCompat.notify(1001, builder.build());

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        // 서비스 종료 시 소켓 및 데이터 스트림을 닫음
        try {
            if (dos != null) {
                dos.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
