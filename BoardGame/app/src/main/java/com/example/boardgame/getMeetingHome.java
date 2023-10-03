package com.example.boardgame;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.boardgame.Adapter.ScheduleAdapter;
import com.example.boardgame.item.ScheduleItem;
import com.example.boardgame.utility.JsonToData;
import com.example.boardgame.vo.meetingVO;
import com.skydoves.balloon.ArrowOrientation;
import com.skydoves.balloon.ArrowPositionRules;
import com.skydoves.balloon.Balloon;
import com.skydoves.balloon.BalloonAnimation;
import com.skydoves.balloon.BalloonSizeSpec;
import com.skydoves.balloon.IconGravity;
import com.skydoves.balloon.OnBalloonClickListener;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class getMeetingHome extends Fragment {

    ScheduleAdapter scheduleAdapter;
    private ImageView imageView2; // 모임의 대표 이미지
    private TextView titleName2; // 대표 이미지 밑에 표시되는 모임 이름
    private TextView meetingContent; // 모임의 내용이 표시
    private ImageButton updateMeeting; // 모임 수정 페이지로 넘어가는 버튼
    private ImageButton viewPeople; // 모임 신청자와 모임 참여자를 볼수있는 페이지로 넘어가는 버튼
    private ImageButton viewCafe;
    private Button button4; // 모임 가입 버튼
    private Button intoSchedule; // 모임일정 만들기 버튼
    private Button intoBoard; // 게시글 작성 버튼
    private RecyclerView scheduleRecyclerView;
    private TextView textView14; // 일정이 없습니다 라는 텍스트뷰

    private Balloon balloon;
    ConstraintLayout constraintLayout; // constraintLayout 변수 설정
    ArrayList<ScheduleItem> st = new ArrayList<>();
    meetingVO vo = new meetingVO();
    int id; // 미팅의 고유 아이디

    private LifecycleOwner lifecycleOwner = this;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_get_meeting_home, container, false);

        imageView2 = view.findViewById(R.id.imageView2 ); // 모임의 대표 이미지
        titleName2 = view.findViewById(R.id.titleName2); // 대표 이미지 밑에 표시되는 모임 이름
        meetingContent = view.findViewById(R.id.meetingContent); // 모임의 내용이 표시
        updateMeeting = view.findViewById(R.id.updateMeeting); // 모임 수정 페이지로 넘어가는 버튼
        viewPeople = view.findViewById(R.id.viewPeople); // 모임 신청자와 모임 참여자를 볼수있는 페이지로 넘어가는 버튼
        viewCafe = view.findViewById(R.id.viewCafe);
        button4 = view.findViewById(R.id.button4); // 모임 가입 버튼
        intoSchedule = view.findViewById(R.id.intoSchedule); // 모임일정 만들기 버튼
        intoBoard = view.findViewById(R.id.intoBoard); // 게시글 작성 버튼
        scheduleRecyclerView = view.findViewById(R.id.scheduleRecyclerView);
        constraintLayout = view.findViewById(R.id.constraintLayout);
        textView14 = view.findViewById(R.id.textView14);

        // 번들로 미팅의 고유 아이디를 받아옴
        Bundle bundle = getArguments();
        if(bundle != null){
            id = bundle.getInt("id", 0);
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        scheduleRecyclerView.setLayoutManager(linearLayoutManager);

        scheduleAdapter = new ScheduleAdapter(st);

        scheduleRecyclerView.setAdapter(scheduleAdapter);

        getList(id); // 일정 리스트 받아오기

        getMeeting(id); // 정보 요청 함수
        updateMeeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(getContext(), updateMeeting.class);
                intent1.putExtra("id", id);
                startActivity(intent1);
            }
        });

        intoSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(getContext(), scheduleMeetingInput.class);
                intent1.putExtra("id", Integer.parseInt(vo.getMeetingSeq()));
                intent1.putExtra("cafeName", vo.getMeetingPlaceName());
                intent1.putExtra("cafeAddress", vo.getMeetingAddress());
                intent1.putExtra("x", vo.getMeetingLnt());
                intent1.putExtra("y", vo.getMeetingLat());
                intent1.putExtra("maxNum", vo.getMeetingMembers());
                intent1.putExtra("currentNum", vo.getMeetingCurrent());
                startActivity(intent1);
            }
        });

        viewCafe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                balloon.showAlignStart(v);
            }
        });

        return view;
    }

    // 일정 리스트를 가져옴
    private void getList(int id){
        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://3.38.213.196/schedule/getScheduleList.php").newBuilder();
        urlBuilder.addQueryParameter("id", String.valueOf(id)); // url 쿼리에 id 라는 메개변수 추가
        String url = urlBuilder.build().toString();
        JsonToData jt = new JsonToData(); // 받아온 json을 vo객체에 담는 함수가 있는 클래스

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
                    if(jt.jsonSchedule(responseData) != null){
                        st.addAll(jt.jsonSchedule(responseData));
                    }

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            scheduleAdapter.notifyDataSetChanged();
                            if(st.size() == 0){
                                constraintLayout.setVisibility(View.GONE);
                                textView14.setVisibility(View.VISIBLE);
                            } else {
                                textView14.setVisibility(View.GONE);
                                constraintLayout.setVisibility(View.VISIBLE);
                            }
                        }
                    });

                }
            } // end onResponse
        });
    } // end getList

    // 미팅 정보를 가져오는 함수
    private void getMeeting(int id){
        // 요청할 url 등록
        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://3.38.213.196/meeting/getMeeting.php").newBuilder();
        urlBuilder.addQueryParameter("id", String.valueOf(id)); // url 쿼리에 id 라는 메개변수 추가
        String url = urlBuilder.build().toString(); // 최종 url 생성

        // Request 객체 생성
        Request request = new Request.Builder()
                .url(url)
                .build();

        // client 객체 생성
        OkHttpClient client = new OkHttpClient();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.isSuccessful()){
                    String responseData = response.body().string();
                    JsonToData jt = new JsonToData(); // 받아온 json을 vo객체에 담는 함수가 있는 클래스
                    vo = jt.jsonMeetingGet(responseData);

                    // UI 업데이트는 메인 스레드에서 실행해야 함
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            balloon = new Balloon.Builder(getContext())
                                    .setArrowSize(10)
                                    .setArrowOrientation(ArrowOrientation.START)
                                    .setArrowPositionRules(ArrowPositionRules.ALIGN_ANCHOR)
                                    .setArrowPosition(0.5f)
                                    .setWidth(BalloonSizeSpec.WRAP)
                                    .setHeight(65)
                                    .setTextSize(15f)
                                    .setCornerRadius(4f)
                                    .setAlpha(0.9f)
                                    .setText(vo.getMeetingPlaceName() + "<br>" + vo.getMeetingAddress())
                                    .setTextColor(ContextCompat.getColor(getContext(), R.color.black))
                                    .setTextIsHtml(true)
                                    .setBackgroundColor(ContextCompat.getColor(getContext(), R.color.white))
                                    .setBalloonAnimation(BalloonAnimation.FADE)
                                    .setLifecycleOwner(lifecycleOwner)
                                    .build();

                            titleName2.setText(vo.getMeetingName()); // 모임의 이름을 설정함
                            meetingContent.setText(vo.getMeetingContent()); // 모임의 내용을 설정함

                            if (vo.getMeetingUrl() != null && !vo.getMeetingUrl().equals("null") && !vo.getMeetingUrl().equals("")) {
                                System.out.println("정상적인 이미지");
                                System.out.println("url : " + vo.getMeetingUrl());
                                Glide.with(getContext()).load("http://3.38.213.196" + vo.getMeetingUrl()).into(imageView2);
                            } else {
                                System.out.println("url : " + vo.getMeetingUrl());
                                System.out.println("아닌이미지");
                                imageView2.setImageResource(R.drawable.img);
                            }

                            int userSeq = Integer.parseInt(vo.getUserSeq()); // 미팅 테이블에 있는 유저 아이디

                            // 쉐어드 프리퍼런스에 있는 유저의 아이디를 가져옴
                            // 1. 쉐어드 프리퍼런스를 사용하기위해 UserData 라는 이름의 파일을 가져옴
                            SharedPreferences sharedPreferences = getContext().getSharedPreferences("UserData", Context.MODE_PRIVATE);
                            // 쉐어드 프리퍼런스에 있는 userId 라는 키값을 가지고 있는 값을 가져오고 가져온 값을 int형으로 변환함
                            int userId = Integer.parseInt(sharedPreferences.getString("userId", ""));
                            if(userSeq == userId){
                                updateMeeting.setVisibility(View.VISIBLE);
                            }else {
                                updateMeeting.setVisibility(View.GONE);
                            }

                        }
                    });
                } else {
                    Toast.makeText(getContext(), "데이터를 가져오는데 실패했습니다 : " + response.body().string(), Toast.LENGTH_SHORT).show();
                }

            }
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Toast.makeText(getContext(), "데이터를 가져오는데 실패했습니다 : " + call.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}