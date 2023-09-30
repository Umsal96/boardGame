package com.example.boardgame.utility;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;

import com.example.boardgame.R;
import com.example.boardgame.inputMeeting;
import com.example.boardgame.scheduleMeetingInput;
import com.example.boardgame.updateMeeting;

public class LocalDialog extends Dialog {

    private static LocalDialog localDialog;

    private TextView inputPlaceName;
    private TextView inputPlaceAddress;
    private Context context;
    private Activity activity;
    private String placeName;
    private String x;
    private String y;
    private String roadAddressName;
    int id;
    String where; // 어느 엑티비티에서 왔는지 확인용
    String num;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.local_dialog);

        inputPlaceName = findViewById(R.id.inputPlaceName);
        inputPlaceAddress = findViewById(R.id.inputPlaceAddress);

        Button setLocal = findViewById(R.id.setLocal);
        Button cancel = findViewById(R.id.cancel);

        inputPlaceName.setText("가계 이름 : " + placeName);
        inputPlaceAddress.setText("가계 주소 : " + roadAddressName);

        setLocal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "확인", Toast.LENGTH_SHORT).show();

                if(where != null){
                    if(where.equals("schedule")){
                        Intent intent = new Intent(getContext(), scheduleMeetingInput.class);
                        intent.putExtra("cafeName", placeName);
                        intent.putExtra("cafeAddress", roadAddressName);
                        intent.putExtra("x", x);
                        intent.putExtra("y", y);
                        intent.putExtra("maxNum", num);
                        getContext().startActivity(intent);
                        dismiss();
                        activity.finish();
                    }
                } else if (where == null){
                    if (id == 0) {
                        Intent intent = new Intent(getContext(), inputMeeting.class);
                        intent.putExtra("placeName", placeName);
                        intent.putExtra("roadAddressName", roadAddressName);
                        intent.putExtra("x", x);
                        intent.putExtra("y", y);
                        getContext().startActivity(intent);
                        dismiss();
                        activity.finish();
                    }else {
                        Intent intent = new Intent(getContext(), updateMeeting.class);
                        intent.putExtra("placeName", placeName);
                        intent.putExtra("roadAddressName", roadAddressName);
                        intent.putExtra("x", x);
                        intent.putExtra("y", y);
                        intent.putExtra("id", id);
                        getContext().startActivity(intent);
                        dismiss();
                        activity.finish();
                    }
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "취소", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });
    }

    public LocalDialog(Context context, Activity activity, String placeName, String x, String y, String roadAddressName, int id, String where, String num){
        super(context);
        this.context = context;
        this.activity = activity;
        this.placeName = placeName;
        this.x = x;
        this.y = y;
        this.roadAddressName = roadAddressName;
        this.id = id;
        this.where = where;
        this.num = num;
    }

}

