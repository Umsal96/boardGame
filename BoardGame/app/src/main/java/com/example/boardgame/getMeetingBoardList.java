package com.example.boardgame;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.boardgame.Adapter.CategoryAdapter;
import com.example.boardgame.Adapter.MeetingBoardAdapter;
import com.example.boardgame.item.CategoryItem;
import com.example.boardgame.item.MeetingBoardItem;
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

public class getMeetingBoardList extends Fragment{

    MeetingBoardAdapter meetingBoardAdapter;
    CategoryAdapter categoryAdapter;
    private FloatingActionButton inputBoard;
    int meetingId; // 모임의 고유 아이디
    int LeaderUserId; // 모임의 리더 고유 아이디
    private int size; // 현재 모임의 인원수
    private String categoryType = "전체";
    ArrayList<MeetingBoardItem> mt = new ArrayList<>();
    ArrayList<CategoryItem> ct = new ArrayList<>();
    private final String[] categories = {"전체", "자유글", "모임후기", "가입인사", "공지사항"};
    private RecyclerView boardRecyclerView;
    private RecyclerView categoryRecyclerView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_get_meeting_board_list, container, false);

        inputBoard = view.findViewById(R.id.inputBoard);
        boardRecyclerView = view.findViewById(R.id.boardRecyclerView);
        categoryRecyclerView = view.findViewById(R.id.categoryRecyclerView);

        // 번들로 모임의 고유 아이디를 받음
        Bundle bundle = getArguments();
        if(bundle != null){
            meetingId = bundle.getInt("id", 0);
            LeaderUserId = bundle.getInt("LeaderUserId", 0);
        }

        System.out.println("-----------------------------------------------------");
        System.out.println("getMeetingBoardList 안 : " + meetingId);

        System.out.println("getMeetingBoardList 안 : 모임장 고유 아이디");
        System.out.println("모임장 고유 아이디 : " + LeaderUserId);

        ct.clear();

        for (int i = 0; i < categories.length; i++) {
            CategoryItem item = new CategoryItem();
            item.setName(categories[i]);
            if(i == 0){
                item.setSelected(true);
            }else{
                item.setSelected(false);
            }
            ct.add(item);
        }

        // 게시글 리스트를 가져옴
        getBoardList(categoryType);

        // 카테고리 보여주는 레이아웃
        LinearLayoutManager categoryLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        categoryRecyclerView.setLayoutManager(categoryLayoutManager);
        categoryAdapter = new CategoryAdapter(getContext(), ct);
        categoryAdapter.setOnButtonItemClickListener(new CategoryAdapter.OnButtonItemClickListener() {
            @Override
            public void onButtonItemClickListener(String name) {
                System.out.println("카테고리가 클릭되었습니다.");
                categoryType = name;
                System.out.println("카테고리 : " + categoryType);
                getBoardList(categoryType);
                meetingBoardAdapter.notifyDataSetChanged();
            }
        });

        System.out.println("카테고리 : " + categoryType);

        categoryRecyclerView.setAdapter(categoryAdapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        boardRecyclerView.setLayoutManager(linearLayoutManager);
        meetingBoardAdapter = new MeetingBoardAdapter(mt, LeaderUserId, meetingId, getContext());
        meetingBoardAdapter.setOnItemMoreMenuClickListener(new MeetingBoardAdapter.OnItemMoreMenuClickListener() {
            @Override
            public void OnItemMoreMenuClickListener(int position, int meetingId, int userId) {

            }
        });

        boardRecyclerView.setAdapter(meetingBoardAdapter);
        inputBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("getMeetingBoardList 안 : 모임장 고유 아이디 : " + LeaderUserId);
                Intent intent = new Intent(getContext(), inputMeetingBoard.class);
                intent.putExtra("meetingId", meetingId); // 모임의 고유 아이디
                intent.putExtra("leaderId", LeaderUserId); // 모임의 모임장 고유 아이디
                startActivity(intent);
            }
        });

        return view;
    }

    private void getBoardList(String categoryType){
        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://3.38.213.196/meetingBoard/getMeetingBoardList.php").newBuilder();
        urlBuilder.addQueryParameter("type", categoryType);
        String url = urlBuilder.build().toString();

        System.out.println("categoryType : " + categoryType);
        JsonToData jt = new JsonToData();

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

                    if (getActivity() != null && !getActivity().isFinishing()){
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mt.clear();
                                mt.addAll(jt.jsonToMeetingBoard(responseData));
                                meetingBoardAdapter.notifyDataSetChanged();
                            }
                        });
                    }


                }
            }
        });
    }


}