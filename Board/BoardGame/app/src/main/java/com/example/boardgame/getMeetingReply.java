package com.example.boardgame;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.boardgame.Adapter.CommentReplyAdapter;
import com.example.boardgame.dialog.ModifyDialog;
import com.example.boardgame.item.CommentReplyItem;
import com.example.boardgame.utility.JsonToData;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class getMeetingReply extends AppCompatActivity {

    private ImageButton backPage;
    private EditText inputCommentText;
    private Button sendButton;
    private RecyclerView replyRecyclerView;
    ArrayList<CommentReplyItem> cri = new ArrayList<>();
    CommentReplyAdapter commentReplyAdapter;
    int leaderId;
    int boardId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_meeting_reply);

        backPage = findViewById(R.id.backPage); // 뒤로가는 버튼
        inputCommentText = findViewById(R.id.inputCommentText); // 댓글이 입력되는 칸
        sendButton = findViewById(R.id.sendButton); // 대댓글 입력 버튼
        replyRecyclerView = findViewById(R.id.replyRecyclerView); // 리사이클러뷰

        // 쉐어드 프리퍼런스에 있는 유저의 아이디를 가져옴
        // 1. 쉐어드 프리퍼런스를 사용하기위해 UserData 라는 이름의 파일을 가져옴
        SharedPreferences sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE);
        // 쉐어드 프리퍼런스에 있는 userId 라는 키값을 가지고 있는 값을 가져오고 가져온 값을 int형으로 변환함
        int UserId = Integer.parseInt(sharedPreferences.getString("userId", ""));

        Intent intent = getIntent();
        boardId = intent.getIntExtra("boardId", 0);
        leaderId = intent.getIntExtra("leaderId", 0);
        int replyRef = intent.getIntExtra("replyRef", 0);

        System.out.println("replyRef : " + replyRef);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getMeetingReply.this);
        replyRecyclerView.setLayoutManager(linearLayoutManager);
        commentReplyAdapter = new CommentReplyAdapter(cri, UserId, 2, boardId, leaderId);

        commentReplyAdapter.setOnReplyMoreMenuClickListener(new CommentReplyAdapter.OnReplyMoreMenuClickListener() {
            @Override
            public void onReplyMoreMenuClick(int position) {
                showMenuDialog(cri.get(position), replyRef);
                System.out.println("클릭되었습니다.");
            }
        });

        replyRecyclerView.setAdapter(commentReplyAdapter);

        backPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(getMeetingReply.this, getMeetingBoard.class);
                intent1.putExtra("boardId", boardId);
                intent1.putExtra("leaderId", leaderId);
                startActivity(intent1);
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputReply(boardId, UserId, replyRef);
            }
        });

        getReplyList(replyRef);

    } // end onCreate
    // 대댓글의 더보기 메뉴를 클릭했을떄 보이는 다이어로그
    private void showMenuDialog(CommentReplyItem item, int replyRef){
        String[] items = {"수정하기", "삭제하기"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which == 0){
                    System.out.println("수정하기 클릭");
                    ModifyDialog modifyDialog = new ModifyDialog(getMeetingReply.this, item);
                    modifyDialog.setOnDialogAcceptClickListener(new ModifyDialog.OnDialogAcceptClickListener() {
                        @Override
                        public void onDialogAcceptClick() {
                            String content = modifyDialog.getContent();
                            int replyId = modifyDialog.getReplyId();
                            System.out.println("댓글 내용 : " + content);
                            System.out.println("댓글 아이디 : " + replyId);
                            modifyReply(replyId, content, replyRef);
                            modifyDialog.dismiss();
                        }
                    });
                    modifyDialog.show();
                } else if (which == 1) {
                    System.out.println("삭제하기 클릭");
                    deleteReplyDialog(item.getReply_seq(), replyRef);
                }
            }
        });

        // 다이얼로그 표시
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // 댓글을 삭제 겠냐고 물어보는 다이얼로그 표시
    private void deleteReplyDialog(int replyId, int replyRef){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("댓글을 삭제 하시겠습니까?")
                .setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteReply(replyId, replyRef);
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
    private void deleteReply(int replyId, int replyRef){
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
                            if ("성공".equals(responseData)) {
                                Intent intent = new Intent(getMeetingReply.this, getMeetingBoard.class);
                                intent.putExtra("boardId", boardId);
                                intent.putExtra("leaderId", leaderId);
                                startActivity(intent);
                            }
                            getReplyList(replyRef);
                        }
                    });
                }
            }
        });
    }

    // 대댓글 수정
    public void modifyReply(int replyId, String content, int replyRef){
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
                            // 해당 그룹의 댓글 리스트를 가져옴
                            getReplyList(replyRef);
                        }
                    });
                }
            }
        });
    }

    // 대댓글 입력
    private void inputReply(int boardId, int userId, int replyRef){
        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://3.38.213.196/reply/inputReply.php").newBuilder();
        urlBuilder.addQueryParameter("replyRef", String.valueOf(replyRef));
        urlBuilder.addQueryParameter("userId", String.valueOf(userId));
        urlBuilder.addQueryParameter("boardId", String.valueOf(boardId));
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
                    String responseData = response.body().string();
                    System.out.println(responseData);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            inputCommentText.setText("");
                            getReplyList(replyRef);
                        }
                    });
                }
            }
        });

    }

    // 댓글 리스트를 가져옴
    private void getReplyList(int replyRef){
        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://3.38.213.196/reply/getDetailReplyList.php").newBuilder();
        urlBuilder.addQueryParameter("replyRef", String.valueOf(replyRef));
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
}