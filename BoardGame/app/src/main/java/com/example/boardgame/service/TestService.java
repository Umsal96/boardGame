package com.example.boardgame.service;


import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import androidx.annotation.Nullable;

public class TestService extends Service {
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final int INTERVAL = 5000; // 5초 대기

    // 백그라운드에서 실행되는 동작을이 들어가는곳
    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                System.out.println("서비스로 실행 중입니다.");
                handler.postDelayed(this, INTERVAL);
            }
        }, INTERVAL);

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
