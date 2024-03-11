package com.example.boardgame;


import static android.content.Context.MODE_PRIVATE;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class profile extends Fragment {

    private TextView textView2;
    private Button logout; // 로그아웃 기능이 있는 버튼
    private Button modifyProfile; // 프로필 수정 페이지로 이동하는 버튼
    private CircleImageView circle; // 프로필 이미지

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        textView2 = view.findViewById(R.id.textView2);
        logout = view.findViewById(R.id.logout);
        modifyProfile = view.findViewById(R.id.modifyProfile);
        circle = view.findViewById(R.id.circle);

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("UserData", MODE_PRIVATE);
        String nick = sharedPreferences.getString("nickname", "");
        String uri = sharedPreferences.getString("url", null);

        System.out.println("uri 바로전 : " + uri);

        // uri 가 null 이 아닐경우 만 실행
        if (uri != null && !uri.equals("null") && !uri.equals("")) {
            System.out.println("실행합니다.");
            // 이미지 url 설정
            String url = "http://3.38.213.196" + uri;
            System.out.println("url : " + url);
            // Glide 라이브러리를 이용해서 이미지를 가져옴
            Glide.with(this).load(url).into(circle);
        } else {
            System.out.println("이미지가 없습니다.");
            circle.setImageResource(R.drawable.img2);
        }

        textView2.setText(nick);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = requireContext().getSharedPreferences("UserData", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear(); // 쉐어드 프리퍼런스의 모든 데이터 삭제
                editor.apply();

                // 로그인 화면으로 이동 또는 필요한 다은 작업 수행
                Intent intent = new Intent(getActivity(), Login.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        modifyProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), profileModify.class);
                startActivity(intent);
            }
        });

        // Inflate the layout for this fragment
        return view;
    }



}