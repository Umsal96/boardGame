package com.example.boardgame;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.boardgame.Adapter.BoardImageViewPagerAdapter;
import com.example.boardgame.Adapter.CommentReplyAdapter;
import com.example.boardgame.dialog.ModifyDialog;
import com.example.boardgame.item.CommentReplyItem;
import com.example.boardgame.item.MeetingBoardDetailItem;
import com.example.boardgame.utility.JsonToData;
import com.example.boardgame.utility.JsonToGetData;

import java.io.IOException;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class getMeetingBoard extends AppCompatActivity {
    private ImageButton backPage; // 뒤로 가기 버튼
    private ImageButton moreMenu; // 메뉴를 더 보여주는 버튼
    private CircleImageView userImg; // 작성자 유저의 프로필
    private TextView userNick; // 작성자의 닉네임
    private TextView boardDate; // 작성 날짜
    private TextView boardType; // 글의 종류
    private TextView boardTitle; // 작성 제목
    private TextView boardContent; // 작성 내용
    private ViewPager2 imageViewPager; // 이미지 뷰페이져
    private TextView currentPage;
    private TextView totalPage;
    private Button inputComment; // 댓글 입력 칸으로 포커스 이동 용 버튼
    ArrayList<CommentReplyItem> cri = new ArrayList<>();
    private EditText inputCommentText; // 댓글입력칸
    private Button sendButton; // 댓글 입력 버튼
    private RecyclerView commentCommentRecyclerView; // 댓글 보여지는 리스트
    MeetingBoardDetailItem item;
    CommentReplyAdapter commentReplyAdapter;
    BoardImageViewPagerAdapter imageViewPagerAdapter;
    String[] parts;
    int meetingId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_meeting_board);

        Intent intent = getIntent();
        int boardId = intent.getIntExtra("boardId", 0);
        int leaderId = intent.getIntExtra("leaderId", 0);

        System.out.println("getMeetingBoard boardId : " + boardId);

        backPage = findViewById(R.id.backPage); // 뒤로 가기 버튼
        moreMenu = findViewById(R.id.moreMenu); // 메뉴를 더 보여주는 버튼
        boardType = findViewById(R.id.boardType); // 글의 종류
        userImg = findViewById(R.id.userImg); // 작성자 유저의 프로필
        userNick = findViewById(R.id.userNick); // 작성자의 닉네임
        boardDate = findViewById(R.id.boardDate); // 작성 날짜
        boardTitle = findViewById(R.id.boardTitle); // 작성 제목
        boardContent = findViewById(R.id.boardContent); // 작성 내용
        imageViewPager = findViewById(R.id.imageViewPager); // 이미지 뷰페이져
        inputComment = findViewById(R.id.inputComment); // 댓글 입력 칸으로 포커스 이동 용 버튼
        inputCommentText = findViewById(R.id.inputCommentText); // 댓글입력칸
        sendButton = findViewById(R.id.sendButton); // 댓글 입력 버튼
        commentCommentRecyclerView = findViewById(R.id.commentCommentRecyclerView); // 댓글 보여지는 리스트
        currentPage = findViewById(R.id.currentPage); // 현재 페이지
        totalPage = findViewById(R.id.totalPage); // 전체 페이지

        // 쉐어드 프리퍼런스에 있는 유저의 아이디를 가져옴
        // 1. 쉐어드 프리퍼런스를 사용하기위해 UserData 라는 이름의 파일을 가져옴
        SharedPreferences sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE);
        // 쉐어드 프리퍼런스에 있는 userId 라는 키값을 가지고 있는 값을 가져오고 가져온 값을 int형으로 변환함
        int UserId = Integer.parseInt(sharedPreferences.getString("userId", ""));

        // 댓글 목록을 보여주는 리사이클러뷰
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getMeetingBoard.this);
        commentCommentRecyclerView.setLayoutManager(linearLayoutManager);
        commentReplyAdapter = new CommentReplyAdapter(cri, UserId, 1, boardId, leaderId);

        // 댓글 더보기
        commentReplyAdapter.setOnMoreMenuClickListener(new CommentReplyAdapter.OnMoreMenuClickListener() {
            @Override
            public void onMoreMenuClick(int position) {
                showMenuDialog(cri.get(position), boardId);
            }
        });
        // 대댓글 더보기
        commentReplyAdapter.setOnReplyMoreMenuClickListener(new CommentReplyAdapter.OnReplyMoreMenuClickListener() {
            @Override
            public void onReplyMoreMenuClick(int position) {

            }
        });

        commentCommentRecyclerView.setAdapter(commentReplyAdapter);
        inputComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputCommentText.requestFocus();
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputReply(boardId, UserId);
            }
        });

        // 뒤로 가기 버튼
        backPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(getMeetingBoard.this, getMeeting.class);
                System.out.println("getMeetingBoard 전송 : id : " + meetingId);
                intent1.putExtra("id", meetingId);
                intent1.putExtra("where", 2);
                startActivity(intent1);
                finish();
            }
        });

        imageViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                currentPage.setText(String.valueOf(position + 1));
            }
        });
        moreMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(getMeetingBoard.this, v); // 팝업 메뉴 등록
                popupMenu.getMenuInflater().inflate(R.menu.meeting_board_select_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.action_board_modify:
                                Intent intent1 = new Intent(getMeetingBoard.this, getMeetingBoardModify.class);
                                intent1.putExtra("meetingId", meetingId);
                                intent1.putExtra("boardId", boardId);
                                intent1.putExtra("leaderId", leaderId);
                                startActivity(intent1);
                                break;
                            case R.id.action_board_delete:
                                deleteDialog(boardId, meetingId);
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });

        getMeetingBoard(boardId, UserId);
        getBoardCommentList(boardId);

    }
    // 댓글과 대댓글에서 더보기 메뉴를 클릭했을때 보여주는 다이얼로그
    private void showMenuDialog(CommentReplyItem item, int boardId){
        String[] items = {"수정하기", "삭제하기"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which == 0){
                    System.out.println("수정하기 클릭");
                    ModifyDialog modifyDialog = new ModifyDialog(getMeetingBoard.this, item);
                    modifyDialog.setOnDialogAcceptClickListener(new ModifyDialog.OnDialogAcceptClickListener() {
                        @Override
                        public void onDialogAcceptClick() {
                            String content = modifyDialog.getContent();
                            int replyId = modifyDialog.getReplyId();
                            System.out.println("댓글 내용 : " + content);
                            System.out.println("댓글 아이디 : " + replyId);
                            modifyReply(replyId, content, boardId);
                            modifyDialog.dismiss();
                        }
                    });
                    modifyDialog.show();
                } else if (which == 1) {
                    System.out.println("삭제하기 클릭");
                    deleteCommentDialog(item.getReply_seq(), boardId, item.getReply_ref());
                }
            }
        });

        // 다이얼로그 표시
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // 댓글을 삭제 겠냐고 물어보는 다이얼로그 표시
    private void deleteCommentDialog(int replyId, int boardId, int replyRef){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("댓글을 삭제 하시겠습니까?")
                .setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteComment(replyId, boardId, replyRef);
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // 댓글 삭제
    private void deleteComment(int replyId, int boardId, int replyRef){
        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://3.38.213.196/reply/deleteReply.php").newBuilder();
        urlBuilder.addQueryParameter("replyId", String.valueOf(replyId)); // url 쿼리에 id 라는 메개변수 추가
        urlBuilder.addQueryParameter("replyRef", String.valueOf(replyRef));
        String url = urlBuilder.build().toString(); // 최종 url 생성

        // Request 객체 생성
        Request request = new Request.Builder()
                .url(url)
                .build();

        // client 객체 생성
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
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getBoardCommentList(boardId);
                        }
                    });
                }
            }
        });
    }

    // 댓글 수정
    private void modifyReply(int replyId, String content, int boardId){
        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://3.38.213.196/reply/updateReply.php").newBuilder();
        urlBuilder.addQueryParameter("replyId", String.valueOf(replyId)); // url 쿼리에 id 라는 메개변수 추가
        urlBuilder.addQueryParameter("content", content);
        String url = urlBuilder.build().toString(); // 최종 url 생성

        // Request 객체 생성
        Request request = new Request.Builder()
                .url(url)
                .build();

        // client 객체 생성
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
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getBoardCommentList(boardId);
                        }
                    });
                }
            }
        });
    }

    // 댓글 목록 가져오는 메소드
    private void getBoardCommentList(int boardId){
        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://3.38.213.196/reply/getReplyList.php").newBuilder();
        urlBuilder.addQueryParameter("boardId", String.valueOf(boardId));
        String url = urlBuilder.build().toString();
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

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            cri.clear();
                            cri.addAll(jt.jsonToBoardComment(responseData));
                            commentReplyAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });
    }

    // 댓글 입력 메소드
    private void inputReply(int boardId, int UserId){
        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://3.38.213.196/reply/inputComment.php").newBuilder();
        urlBuilder.addQueryParameter("boardId", String.valueOf(boardId));
        urlBuilder.addQueryParameter("userId", String.valueOf(UserId));
        urlBuilder.addQueryParameter("content", inputCommentText.getText().toString());
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
                    String responseData =  response.body().string();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println(responseData);
                            System.out.println("입력 완료");
                            inputCommentText.setText("");
                            getBoardCommentList(boardId);
                        }
                    });
                }
            }
        });

    }
    // 정말 삭제 할건지 물어보는 메소드
    private void deleteDialog(int boardId, int meetingId){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("게시글 삭제하시겠습니까? : ")
                .setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteBoard(boardId, meetingId);
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // 게시글 삭제 메소드
    private void deleteBoard(int boardId, int meetingId){
        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://3.38.213.196/meetingBoard/deleteMeetingBoard.php").newBuilder();
        urlBuilder.addQueryParameter("boardId", String.valueOf(boardId));
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
                    System.out.println(responseData);
                    Intent intent = new Intent(getMeetingBoard.this, getMeeting.class);
                    intent.putExtra("where", 2);
                    intent.putExtra("id", meetingId);
                    startActivity(intent);
                }
            }
        });
    }
    // 모임 정보 게시글 정보 가져오기
    private void getMeetingBoard(int boardId, int UserId){
        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://3.38.213.196/meetingBoard/getMeetingBoard.php").newBuilder();
        urlBuilder.addQueryParameter("boardId", String.valueOf(boardId));
        String url = urlBuilder.build().toString();
        JsonToGetData jtg = new JsonToGetData();

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
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            item = jtg.jsonGetToGetBoard(responseData);

                            // 유저의 이미지가 있는지 없는지 확인
                            if (item.getUser_url() != null && !item.getUser_url().equals("null") && !item.getUser_url().equals("")) {
                                Glide.with(getApplicationContext()).load("http://3.38.213.196" + item.getUser_url()).into(userImg);
                            } else {
                                userImg.setImageResource(R.drawable.img);
                            }

                            // 닉네임 설정
                            userNick.setText(item.getUser_nickname());
                            // 날짜 설정
                            boardDate.setText(item.getBoard_create_date());
                            // 글의 종류 설정
                            boardType.setText(item.getBoard_type());
                            // 글의 제목
                            boardTitle.setText(item.getBoard_title());
                            // 글의 내용
                            boardContent.setText(item.getBoard_content());

                            String img = item.getImage_urls();
                            meetingId = item.getMeeting_seq();
                            // 이미지가 있는지 확인
                            if (img != null && !img.equals("null") && !img.equals("")) {
                                // 이미지가 있다면
                                parts = img.split(",");
                                int lang = parts.length;
                                for (int i = 0; i < parts.length; i++) {
                                    System.out.println("이미지 uri : " + parts[i]);
                                }
                                imageViewPagerAdapter = new BoardImageViewPagerAdapter(parts);

                                imageViewPager.setAdapter(imageViewPagerAdapter);
                                System.out.println("이미지의 갯수 : " + lang);
                                imageViewPagerAdapter.notifyDataSetChanged();
                                imageViewPager.setVisibility(View.VISIBLE);
                                totalPage.setText(String.valueOf(lang));

                            } else {
                                System.out.println("이미지가 없습니다.");
                                imageViewPager.setVisibility(View.GONE);
                            }

                            System.out.println("게시글 작성자의 유저 아이디 : " + item.getUser_seq());
                            System.out.println("접속한 유저의 아이디 : " + UserId);

                            if(item.getUser_seq() == UserId){
                                moreMenu.setEnabled(true);
                            }else {
                                moreMenu.setEnabled(false);
                            }
                        }
                    });
                }
            }
        });
    }
}