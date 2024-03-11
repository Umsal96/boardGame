package com.example.boardgame.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethod;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.example.boardgame.R;
import com.example.boardgame.getMeetingBoard;
import com.example.boardgame.item.CommentReplyItem;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ModifyDialog extends Dialog {

    private EditText replyCommentContent;
    private Button cancelButton, acceptButton;
    CommentReplyItem item;

    private String content;

    private int replyId;

    // 수정 버튼 클릭 이벤트 리스너
    private OnDialogAcceptClickListener onDialogAcceptClickListener;

    public String getContent() {
        return content;
    }
    public int getReplyId(){
        return replyId;
    }

    public interface OnDialogAcceptClickListener {
        void onDialogAcceptClick();
    }

    public void setOnDialogAcceptClickListener(OnDialogAcceptClickListener listener) {
        onDialogAcceptClickListener = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.modify_dialog);

        replyCommentContent = findViewById(R.id.replyCommentContent);
        cancelButton = findViewById(R.id.cancelButton);
        acceptButton = findViewById(R.id.acceptButton);

        replyCommentContent.setText(item.getReply_content());
        replyCommentContent.requestFocus(); // 포커스를 텍스트뷰로 이동
        replyCommentContent.setSelection(replyCommentContent.getText().length()); // 커서를 텍스트의 끝으로 이동
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(replyCommentContent, InputMethodManager.SHOW_IMPLICIT);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                content = replyCommentContent.getText().toString();
                replyId = item.getReply_seq();
                if (onDialogAcceptClickListener != null) {
                    onDialogAcceptClickListener.onDialogAcceptClick();
                }
            }
        });

    }
    public ModifyDialog(@NonNull Context context, CommentReplyItem item){
        super(context);
        this.item = item;
    }
}
