package com.example.boardgame.utility;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import com.example.boardgame.R;

public class NotificationChannelManager {
    String CHANNEL_ID = "1001";
    private static NotificationChannelManager instance;
    private NotificationChannel channel;

    private NotificationChannelManager(Context context){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = context.getString(R.string.channel_name);
            String description = context.getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            channel.enableLights(true);
            channel.enableVibration(true);
            // 노티피케이션 메니저를 사용하여 채널을 생성합니다.
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public static NotificationChannelManager getInstance(Context context){
        if (instance == null){
            instance = new NotificationChannelManager(context);
        }
        return instance;
    }

    public NotificationChannel getChannel(){
        return channel;
    }
}
