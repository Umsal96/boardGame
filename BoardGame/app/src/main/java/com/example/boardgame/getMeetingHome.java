package com.example.boardgame;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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
import com.example.boardgame.dialog.WaitingDialog;
import com.example.boardgame.item.ScheduleItem;
import com.example.boardgame.item.ScheduleMemberItem;
import com.example.boardgame.item.UserItem;
import com.example.boardgame.item.UserNItem;
import com.example.boardgame.item.WaitingItem;
import com.example.boardgame.network.meeting.NetSchedule;
import com.example.boardgame.service.socketService;
import com.example.boardgame.socket.clientSocket;
import com.example.boardgame.utility.FragToActData;
import com.example.boardgame.utility.JsonToData;
import com.example.boardgame.utility.OnItemClickListener;
import com.example.boardgame.utility.ScheduleDialog;
import com.example.boardgame.utility.UserDialog;
import com.example.boardgame.vo.meetingVO;
import com.skydoves.balloon.ArrowOrientation;
import com.skydoves.balloon.ArrowPositionRules;
import com.skydoves.balloon.Balloon;
import com.skydoves.balloon.BalloonAnimation;
import com.skydoves.balloon.BalloonSizeSpec;

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
    private ImageButton viewPeople; // 모임 신청자와 모임 참여자를 볼수있는 다이얼로그
    private ImageButton viewCafe;
    private ImageButton waitMemberButton; // 대기자 명단을 볼 수 있는 버튼
    private Button button4; // 모임 가입 버튼
    private Button intoSchedule; // 모임일정 만들기 버튼
    private Button intoBoard; // 게시글 작성 버튼
    private Button waitButton; // 현재 가입 대기중인지 표시하는 버튼
    private RecyclerView scheduleRecyclerView;
    private TextView textView14; // 일정이 없습니다 라는 텍스트뷰
    private Balloon balloon;
    ConstraintLayout constraintLayout; // constraintLayout 변수 설정
    ArrayList<ScheduleItem> st = new ArrayList<>();
    ArrayList<ScheduleMemberItem> smt = new ArrayList<>();
    meetingVO vo = new meetingVO();
    int id; // 미팅의 고유 아이디
    int sk; // 유저 대기줄 다이얼로그를 보여줄지 말지 하는 변수
    ArrayList<UserItem> data;
    ArrayList<WaitingItem> waitingItem;
    private LifecycleOwner lifecycleOwner = this;
    private FragToActData fragToActData;
    NetSchedule NetSchedule = new NetSchedule();
    int LeaderSeq; // 미팅 방장의 고유 아이디
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
        waitMemberButton = view.findViewById(R.id.waitMemberButton); // 모임 신청 대기자 확인하는 버튼
        waitButton = view.findViewById(R.id.waitButton);

        fragToActData = (FragToActData) getContext();

        // 번들로 미팅의 고유 아이디를 받아옴
        Bundle bundle = getArguments();
        if(bundle != null){
            id = bundle.getInt("id", 0);
            sk = bundle.getInt("sk", 0);
        }

        // 쉐어드 프리퍼런스에 있는 유저의 아이디를 가져옴
        // 1. 쉐어드 프리퍼런스를 사용하기위해 UserData 라는 이름의 파일을 가져옴
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("UserData", Context.MODE_PRIVATE);
        // 쉐어드 프리퍼런스에 있는 userId 라는 키값을 가지고 있는 값을 가져오고 가져온 값을 int형으로 변환함
        int userId = Integer.parseInt(sharedPreferences.getString("userId", ""));
        getUserWaitingList(id, userId);
        button4.setOnClickListener(new View.OnClickListener() { // 모임 가입 버튼
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                alertDialogBuilder.setTitle("알림");

                alertDialogBuilder.setMessage("가입 신청 하시겠습니까?");
                alertDialogBuilder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 유저가 모임에 가입함
                        System.out.println("버튼을 클릭합니다.");
                        intoWait(userId, id, LeaderSeq);
                    }
                });
                alertDialogBuilder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });
        getScheduleList(id); // 일정 맴버 리스트 받아오기

        // 모임에 참가한 인원들을 보여주는 리사이클러뷰
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        scheduleRecyclerView.setLayoutManager(linearLayoutManager);
        scheduleAdapter = new ScheduleAdapter(st, smt, userId ,new OnItemClickListener() {
            @Override
            public void onItemClick(int ScheduleId) {
                System.out.println("프레그먼트 내의 고유 아이디 : " + ScheduleId);
                getScheduleUserList(ScheduleId);
            }
        });

        // 일정 리스트 아이템 내의 참가 버튼을 눌렀을때 이벤트
        scheduleAdapter.setOnScheduleAttendClickListener(new ScheduleAdapter.OnScheduleAttendClickListener() {
            @Override
            public void onScheduleAttendClick(int scheduleSeq, int position) {
                ScheduleAdapter.ViewHolder holder = (ScheduleAdapter.ViewHolder) scheduleRecyclerView.findViewHolderForAdapterPosition(position);
                // 해당 아이템의 위치를 찾음
                System.out.println("클릭한 아이템의 위치 : " + position);
                holder.scheduleAttend.setVisibility(View.GONE);
                holder.scheduleCancel.setVisibility(View.VISIBLE);
                System.out.println("일정 고유 아이디 : " + scheduleSeq);
                System.out.println("유저 고유 아이디 : " + userId);
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("알림");
                builder.setMessage("일정에 참가 하시겠습니까?");
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        NetSchedule.inputSchedule(id, scheduleSeq, userId, getActivity(), new NetSchedule.ScheduleCallback() {
                            @Override
                            public void onScheduleResponse(ScheduleItem scheduleItem) {
                                getChangeScheduleList(id, position, scheduleItem);
                            }
                        }); // id = 모임 고유 아이디
                    }
                });

                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        holder.scheduleCancel.setVisibility(View.GONE);
                        holder.scheduleAttend.setVisibility(View.VISIBLE);
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        // 일정 리스트 아이템 내의 취소 버튼을 눌렀을때 이벤트
        scheduleAdapter.setOnScheduleCancelClickListener(new ScheduleAdapter.OnScheduleCancelClickListener() {
            @Override
            public void onScheduleCancelClick(int scheduleSeq, int position) {
                ScheduleAdapter.ViewHolder holder = (ScheduleAdapter.ViewHolder) scheduleRecyclerView.findViewHolderForAdapterPosition(position);
                System.out.println("클릭한 아이템의 위치 : " + position);
                holder.scheduleCancel.setVisibility(View.GONE);
                holder.scheduleAttend.setVisibility(View.VISIBLE);
                System.out.println("일정 고유 아이디 : " + scheduleSeq);
                System.out.println("유저 고유 아이디 : " + userId);
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("알림");
                builder.setMessage("일정 참가를 취소 하시겠습니까?");
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        NetSchedule.exitSchedule(scheduleSeq, userId, getActivity(), new NetSchedule.ScheduleCallback() {
                            @Override
                            public void onScheduleResponse(ScheduleItem scheduleItem) {
                                getChangeScheduleList(id, position, scheduleItem);
                            }
                        });
                    }
                });
                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        holder.scheduleAttend.setVisibility(View.GONE);
                        holder.scheduleCancel.setVisibility(View.VISIBLE);
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

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

        // 대기자 명단 다이얼로그 확인용 버튼
        waitMemberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("모임의 방징 고유 아이디 : " + LeaderSeq);
                System.out.println("로그인 유저의 고유 아이디 : " + userId);
                WaitingDialog waitingDialog = new WaitingDialog(getContext(), waitingItem, getActivity(), userId);
                waitingDialog.show();
            }
        });

        // 유저 리스트 다이얼로그 확인용
        viewPeople.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserDialog userDialog = new UserDialog(getContext(), data);
                userDialog.show();
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

        // 가입한 유저의 리스트를 가져옴
        getUserList(id, userId);
        viewCafe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                balloon.showAlignStart(v);
            }
        });

        return view;
    }
    // 해당 모임의 가입 신청 리스트를 가져오는 메소드
    private void getUserWaitingList(int id, int userId){
        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://3.38.213.196/waiting/getWaitingUserList.php").newBuilder();
        urlBuilder.addQueryParameter("id", String.valueOf(id)); // url 쿼리에 id 라는 메개변수 추가 모임 고유 아이디
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
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            waitingItem = js.jsonToWaitingUserList(responseData);
                            if(sk == 1){
                                WaitingDialog waitingDialog = new WaitingDialog(getContext(), waitingItem, getActivity(), userId);
                                waitingDialog.show();
                            }
                        }
                    });
                }
            }
        });
    }

    // 로그인한 유저가 해당 모임에 가입을 신청했는지 확인하는 메소드
    private void getWaitList(int userId, int id){
        System.out.println("getWaitList 실행");
        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://3.38.213.196/waiting/getWaitingList.php").newBuilder();
        urlBuilder.addQueryParameter("userId", String.valueOf(userId)); // 유저의 고유 아이디
        urlBuilder.addQueryParameter("id", String.valueOf(id)); // url 쿼리에 id 라는 메개변수 추가 모임 고유 아이디
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
                if(response.isSuccessful()){
                    System.out.println("응답이 돌아옴");
                    String eq = "[]";
                    String responseData = response.body().string();
                    System.out.println("대기줄 입원 : " + responseData);
                    if(eq.equals(responseData)){
                        // 데이터가 없는 경우
                        System.out.println("데이터가 비어있습니다.");
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                waitButton.setVisibility(View.GONE);
                                button4.setVisibility(View.VISIBLE);
                            }
                        });
                    }else {
                        // 데이터가 있는 경우
                        System.out.println("데이터가 있습니다..");
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                button4.setVisibility(View.GONE);
                                waitButton.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                }// end if(response.isSuccessful)
            } // end onResponse
        }); // end client.newCAll
    } // end getWaitList

    // 모임 신청 대기줄에 정보 입력하는 메소드
    private void intoWait(int userId, int id, int LeaderSeq){
        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://3.38.213.196/waiting/intoWaiting.php").newBuilder();
        urlBuilder.addQueryParameter("userId", String.valueOf(userId)); // 유저의 고유 아이디
        urlBuilder.addQueryParameter("id", String.valueOf(id)); // url 쿼리에 id 라는 메개변수 추가 모임 고유 아이디
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
                if(response.isSuccessful()){
                    String responseData = response.body().string();
                    System.out.println("결과 : " + responseData);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getWaitList(userId, id);
                            Intent serviceIntent = new Intent(getContext(), socketService.class);
                            serviceIntent.putExtra("userId", LeaderSeq);
                            serviceIntent.putExtra("action", "join");
                            serviceIntent.putExtra("meetingId", id);
                            getContext().startService(serviceIntent);
//                            cSocket.waitingClientSocket(userId, LeaderSeq, "신청");
                        }
                    });
                }
            }
        });

    }

    // 일정의 멤버 리스트를 받아오는 메소드
    private void getChangeScheduleList(int id, int position, ScheduleItem scheduleItem){ // id는 미팅 고유 아이디
        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://3.38.213.196/schedule/getScheduleMemberList.php").newBuilder();
        urlBuilder.addQueryParameter("id", String.valueOf(id)); // url 쿼리에 id 라는 메개변수 추가 모임 고유 아이디
        String url = urlBuilder.build().toString();
        JsonToData jt = new JsonToData(); // 받아온 json을 item객체에 담는 함수가 있는 클래스

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
                    System.out.println("이것 : " + responseData);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            smt.clear();
                            smt.addAll(jt.jsonToScheduleMemberList(responseData));

                            st.set(position, scheduleItem);
                            scheduleAdapter.notifyItemChanged(position);
                            if(scheduleItem.getSchedule_member_current() <= 0){
                                System.out.println("맴버가 아무도 없습니다.");
                                NetSchedule.deleteSchedule(scheduleItem.getScheduleSeq(), id, getActivity());
                            }
                        }
                    });
                } // end if
            } // end onResponse
        });
    }

    // 일정의 멤버 리스트를 받아오는 메소드
    private void getScheduleList(int id){ // id는 미팅 고유 아이디
        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://3.38.213.196/schedule/getScheduleMemberList.php").newBuilder();
        urlBuilder.addQueryParameter("id", String.valueOf(id)); // url 쿼리에 id 라는 메개변수 추가 모임 고유 아이디
        String url = urlBuilder.build().toString();
        JsonToData jt = new JsonToData(); // 받아온 json을 item객체에 담는 함수가 있는 클래스

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
                    System.out.println("이것 : " + responseData);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            smt.clear();
                            smt.addAll(jt.jsonToScheduleMemberList(responseData));
                            System.out.println("여기");
                            for (int i = 0; i < smt.size(); i++) {
                                System.out.println(smt.get(i).getUser_seq());
                            }

                        }
                    });

                }
            }
        });
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
                    System.out.println("일정");
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
    } // end getMeeting

    // 모임에 참가한 인원을 가져오는 메소드
    private void getScheduleUserList(int scheduleId){
        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://3.38.213.196/schedule/getScheduleMemberUserList.php").newBuilder();
        urlBuilder.addQueryParameter("scheduleId", String.valueOf(scheduleId));
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
                if(response.isSuccessful()){
                    JsonToData jt = new JsonToData();
                    String responseData = response.body().string();
                    ArrayList<UserNItem> uni = jt.jsonToScheduleMemberUserList(responseData);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ScheduleDialog scheduleDialog = new ScheduleDialog(getContext(), uni);
                            scheduleDialog.show();
                        }
                    });
                }
            }
        });
    }
    // 모임의 가입한 유저의 리스트
    private void getUserList(int id, int userId){
        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://3.38.213.196/meeting/getUserMeeting.php").newBuilder();
        urlBuilder.addQueryParameter("meeting_seq", String.valueOf(id));
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
                if(response.isSuccessful()){
                    String responseData = response.body().string();
                    JsonToData jt = new JsonToData();

                    data = jt.jsonToUserList(responseData);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            boolean isUserJoined = false; // 유저가 모임가입이 됬었는지 확인
                            boolean isLeader = false; // 유저가 방장인지 검사

                            // 모임 인원 리스트중에서 모임장의 유저 고유 아이디를 찾기위한 방복문
                            for (int i = 0; i < data.size(); i++) {
                                if(data.get(i).getLeader() == 1){
                                    LeaderSeq = data.get(i).getUserSeq();
                                    fragToActData.onDataPass(data.get(i).getUserSeq(), data.size());
                                    break;
                                }
                            }

                            // 현재 유저가 방장인지 확인
                            for (int i = 0; i < data.size(); i++) {
                                if(data.get(i).getUserSeq() == userId){ // 가입이 된 유저다
                                    if(data.get(i).getLeader() == 1){ // 방장이다.
                                        isLeader = true;
                                        isUserJoined = true;
                                        System.out.println("리더입니다.");
                                        break;
                                    } else { // 방장은 아니다
                                        isLeader = false;
                                        isUserJoined = true;
                                        System.out.println("리더가 아닙니다1");
                                        break;
                                    }
                                }else {
                                    System.out.println("가입된 유저가 아닙니다.1");
                                }
                            }

                            if(!isUserJoined && !isLeader){ // 모임 가입된 유저가 아님
                                button4.setVisibility(View.VISIBLE); // 회원가입 버튼
                                updateMeeting.setVisibility(View.GONE); // 업데이트 버튼
                                intoSchedule.setEnabled(false);
                                intoBoard.setEnabled(false);
                                waitMemberButton.setVisibility(View.GONE);
                                getWaitList(userId, id); // 해당 모임의 신청자 리스트를 보여줌 userId = 유저의 고유 아이디, id = 모임의 고유 아이디
                                System.out.println("회원가입된 유저가 아님");
                            } else if (isUserJoined && !isLeader) { // 모임가입된 유저인데 모임장이 아님
                                button4.setVisibility(View.GONE);
                                updateMeeting.setVisibility(View.GONE);
                                waitMemberButton.setVisibility(View.GONE);
                                intoSchedule.setEnabled(true);
                                intoBoard.setEnabled(true);

                                System.out.println("회원가입된 유저");
                            } else { // 모임 가입된 유저인데 모임장임

                                button4.setVisibility(View.GONE);
                                updateMeeting.setVisibility(View.VISIBLE);
                                intoSchedule.setEnabled(true);
                                intoBoard.setEnabled(true);
                                System.out.println("모임장");
                            }
                        }
                    });
                }
            }
        });
    } // end getUserList
}