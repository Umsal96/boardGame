package com.example.boardgame.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.boardgame.R;
import com.example.boardgame.item.CommentReplyItem;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentReplyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<CommentReplyItem> commentReplyItems;

    @Override
    public int getItemViewType(int position){
        int order = commentReplyItems.get(position).getReply_order();
        if (order == 0){ // 대댓글이 아닐경우
            return 0;
        } else { // 대댓글일 경우
            return 1;
        }
    }

    public static class ViewHolderComment extends RecyclerView.ViewHolder{
        private TextView userNick, commentDate, commentContent;
        private CircleImageView userImg;

        public ViewHolderComment(@NonNull View itemView){
            super(itemView);

            userNick = itemView.findViewById(R.id.userNick);
            commentDate = itemView.findViewById(R.id.commentDate);
            commentContent = itemView.findViewById(R.id.commentContent);
            userImg = itemView.findViewById(R.id.userImg);
        }
    }

    public static class ViewHolderReply extends RecyclerView.ViewHolder{
        private TextView replyUserNick, replyDate, replyContent;
        private CircleImageView replyUserImg;
        public ViewHolderReply(@NonNull View itemView){
            super(itemView);

            replyUserNick = itemView.findViewById(R.id.replyUserNick);
            replyDate = itemView.findViewById(R.id.replyDate);
            replyContent = itemView.findViewById(R.id.replyContent);
            replyUserImg = itemView.findViewById(R.id.replyUserImg);
        }
    }

    public CommentReplyAdapter(ArrayList<CommentReplyItem> DataSet){
        this.commentReplyItems = DataSet;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view;
        if(viewType == 0){ // 대댓글이 아닐경우
            view = inflater.inflate(R.layout.board_comment_item, parent, false);
            return new ViewHolderComment(view);
        }else {
            view = inflater.inflate(R.layout.board_reply_item, parent, false);
            return new ViewHolderReply(view);
        }
    }

    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position){
        CommentReplyItem item = commentReplyItems.get(position);
        if (holder instanceof ViewHolderComment) {
            ViewHolderComment viewHolderComment = (ViewHolderComment) holder;
            // 데이터 설정 및 댓글 레이아웃 에 대한 처리

            if (item.getUser_url() != null && !item.getUser_url().equals("null") && !item.getUser_url().equals("")) {
                Glide.with(holder.itemView.getContext()).load("http://3.38.213.196" + item.getUser_url()).into(viewHolderComment.userImg);
            } else {
                viewHolderComment.userImg.setImageResource(R.drawable.img);
            }

            viewHolderComment.userNick.setText(item.getUser_nick());
            viewHolderComment.commentDate.setText(item.getReply_create_date());
            viewHolderComment.commentContent.setText(item.getReply_content());

        } else if (holder instanceof ViewHolderReply) {
            ViewHolderReply viewHolderReply = (ViewHolderReply) holder;
            // 데이터 설정 및 대 댓글 레이아웃 B에 대한 처리

            if (item.getUser_url() != null && !item.getUser_url().equals("null") && !item.getUser_url().equals("")) {
                Glide.with(holder.itemView.getContext()).load("http://3.38.213.196" + item.getUser_url()).into(viewHolderReply.replyUserImg);
            } else {
                viewHolderReply.replyUserImg.setImageResource(R.drawable.img);
            }

            viewHolderReply.replyUserNick.setText(item.getUser_nick());
            viewHolderReply.replyDate.setText(item.getReply_create_date());
            viewHolderReply.replyContent.setText(item.getReply_content());

        }
    }

    @Override
    public int getItemCount() {
        if (commentReplyItems == null) {
            return 0;
        }
        return commentReplyItems.size();
    }
}
