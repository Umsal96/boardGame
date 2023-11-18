package com.example.boardgame;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.boardgame.Adapter.MeetingAdapter;
import com.example.boardgame.item.MeetingItem;
import com.example.boardgame.utility.JsonToData;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;

import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class meeting extends Fragment {

    MeetingAdapter meetingAdapter;
    private FloatingActionButton toMeeting; // 새글 작성 페이지로 이동용 버튼
    private RecyclerView meetingRecyclerView; // 리사이클러뷰 변수 등록
    ArrayList<MeetingItem> mt = new ArrayList<>(); // meetingItem 객체의 어레이리스트 생성
    int page = 1, limit = 10;
    int num;
    private ProgressBar progressBar; // 위에 프로세스 바
    private boolean isLoading = false;
    private boolean firstLoad = true;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("onCreate");
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_meeting, container, false);

        progressBar = view.findViewById(R.id.progress_bar);
        System.out.println("onCreateView");

        // 중복 데이터를 가져오는것을 방지
        // 만약 mt 의 데이터가 비여있다면 데이터를 가져왔다는 것이고
        // 만약 mt 의 데이터가 비여있지 않다면 데이터가 없다는것이니까
        // mt의 데이터가 없을때만 데이터를 가져옴
        if(mt.isEmpty()){
            getList(page, limit);
        }

        toMeeting = view.findViewById(R.id.toMeeting);

        meetingRecyclerView = view.findViewById(R.id.meetingRecyclerView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        meetingRecyclerView.setLayoutManager(linearLayoutManager);

        meetingAdapter = new MeetingAdapter(mt);

        meetingRecyclerView.setAdapter(meetingAdapter);

        meetingRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                // 현재 표시된 아이템 수
                int visibleItemCount = layoutManager.getChildCount();

                // 전체 아이템 수
                int totalItemCount = layoutManager.getItemCount();

                // 현재 스크롤된 아이템의 시작 인덱스
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                // 스크롤 방향 체크 (위로 스크롤: dy < 0, 아래로 스크롤: dy > 0)
                if (dy > 0) {
                    // 아래로 스크롤 중

                    // 페이징 처리를 위한 임계값 (여기에서는 마지막 아이템 전까지 스크롤할 경우)
                    int threshold = 5;

                    if (!isLoading && (visibleItemCount + firstVisibleItemPosition) >= (totalItemCount - threshold)) {
                        // 스크롤의 맨 아래 부분에 도달하면 추가 데이터를 가져와서 표시
                        System.out.println("최대 컬럼 : " + num);
                        if (mt.size() < num) { // limit가 num보다 작을 때만 데이터를 가져오도록 수정
                            page++;
                            isLoading = true; // 데이터를 로딩 중임을 표시
                            getList(page, limit);
                        }
                    }
                }
            }
        });

        toMeeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), inputMeeting.class);
                startActivity(intent);
            }
        });

        // Inflate the layout for this fragment
        return view;
    }// end onCreateViews

    @Override
    public void onViewCreated(View view,
                              Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        System.out.println("onViewCreated");
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState){
        super.onViewStateRestored(savedInstanceState);
        System.out.println("onViewStateRestored");
    }

    @Override
    public void onStart(){
        super.onStart();
        System.out.println("onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        System.out.println("onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        System.out.println("onPause");
    }

    @Override
    public void onStop(){
        super.onStop();
        System.out.println("onStop");
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        System.out.println("onDestroyView");
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
        System.out.println("onSaveInstanceState");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("onDestroy");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        System.out.println("onDetach");
    }

    private void getList(int page, int limit){
        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://3.38.213.196/meeting/getMeetingList.php").newBuilder();
        urlBuilder.addQueryParameter("page", String.valueOf(page));
        urlBuilder.addQueryParameter("limit", String.valueOf(limit));
        String url = urlBuilder.build().toString();

        progressBar.setVisibility(View.VISIBLE); // 프로그레스 바를 표시합니다.

        Request request = new Request.Builder()
                .url(url)
                .build();

        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    JsonToData jt = new JsonToData();

                    Pair<Integer, ArrayList<MeetingItem>> data = jt.jsonToMeetingList(responseData);

                    mt.addAll(data.second);

                    num = data.first;

                    System.out.println("최대 1 갯수  : " + num);
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            meetingAdapter.notifyDataSetChanged(); // 데이터 변경 알림
                            isLoading = false;
                            progressBar.setVisibility(View.GONE); // 데이터 로딩이 끝나면 프로그레스 바를 숨깁니다.
                        }
                    }, 1000);
                } else {
                    Toast.makeText(getContext(), response.body().string(), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE); // 데이터 로딩이 끝나면 프로그레스 바를 숨깁니다.
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(getContext(), call.toString(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE); // 데이터 로딩이 끝나면 프로그레스 바를 숨깁니다.
            }
        });
    }

}