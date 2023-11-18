package com.example.boardgame.Adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.boardgame.R;
import com.example.boardgame.getMeetingReply;
import com.example.boardgame.item.CommentReplyItem;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentReplyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<CommentReplyItem> commentReplyItems;
    int userId; // 로그인한 유저의 고유 아이디
    int where; // 어디서 어뎁터를 사용했는지
    int boardId;
    int leaderId;

    // 댓글의 더보기 버튼
    private OnMoreMenuClickListener onMoreMenuClickListener;
    // 대댓글의 더보기 버튼
    private OnReplyMoreMenuClickListener onReplyMoreMenuClickListener;

    // 댓글의 더보기 버튼 인터페이스
    public interface OnMoreMenuClickListener{
        void onMoreMenuClick(int position);
    }
    // 대댓글의 더보기 버튼 인터페이스
    public interface OnReplyMoreMenuClickListener{
        void onReplyMoreMenuClick(int position);
    }

    // 댓글의 더보기 버튼 클릭 리스너
    public void setOnMoreMenuClickListener(OnMoreMenuClickListener listener){
        onMoreMenuClickListener = listener;
    }

    // 대댓글 더보기 버튼 클릭 리스너
    public void setOnReplyMoreMenuClickListener(OnReplyMoreMenuClickListener listener){
        onReplyMoreMenuClickListener = listener;
    }
    @Override
    public int getItemViewType(int position){
        int order = commentReplyItems.get(position).getReply_order();
        if (order == 0){ // 대댓글이 아닐경우
            return 0;
        } else { // 대댓글일 경우
            return 1;
        }
    }

    // 일반댓글 뷰 홀더
    public static class ViewHolderComment extends RecyclerView.ViewHolder{
        private TextView userNick, commentDate, commentContent, toReply;
        private CircleImageView userImg;
        private ImageButton imageButton;
        public ViewHolderComment(@NonNull View itemView){
            super(itemView);

            userNick = itemView.findViewById(R.id.userNick);
            commentDate = itemView.findViewById(R.id.commentDate);
            commentContent = itemView.findViewById(R.id.commentContent);
            userImg = itemView.findViewById(R.id.userImg);
            imageButton = itemView.findViewById(R.id.imageButton);
            toReply = itemView.findViewById(R.id.toReply);
        }
    }

    // 대댓글 뷰홀더
    public static class ViewHolderReply extends RecyclerView.ViewHolder{
        private TextView replyUserNick, replyDate, replyContent;
        private CircleImageView replyUserImg;
        private ImageButton imageButton2; // 메뉴 더 보기 버튼

        public ViewHolderReply(@NonNull View itemView){
            super(itemView);

            replyUserNick = itemView.findViewById(R.id.replyUserNick);
            replyDate = itemView.findViewById(R.id.replyDate);
            replyContent = itemView.findViewById(R.id.replyContent);
            replyUserImg = itemView.findViewById(R.id.replyUserImg);
            imageButton2 = itemView.findViewById(R.id.imageButton2);
        }
    }

    public CommentReplyAdapter(ArrayList<CommentReplyItem> DataSet, int userId, int where, int boardId, int leaderId){
        this.commentReplyItems = DataSet;
        this.userId = userId;
        this.where = where; // 1 이면 그냥 댓글 2면 대댓글
        this.boardId = boardId;
        this.leaderId = leaderId;
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
        if (holder instanceof ViewHolderComment) { // 일반 댓글일 경우
            ViewHolderComment viewHolderComment = (ViewHolderComment) holder;
            // 데이터 설정 및 댓글 레이아웃 에 대한 처리

            // 로그인한 유저가 댓글을 작성한 유저가 같을경우
            // 더보기 버튼을 보이게함
            // 다를경우 보이지 않게함
            if(where == 1){ // 댓글 페이지 에서 이용
                if(item.getUser_seq() == userId){ // 작성자와 로그인 유저가 같을때
                    if(item.getReply_del() == 1){ // 삭제된 댓글일때
                        viewHolderComment.imageButton.setVisibility(View.GONE);
                    } else { // 삭제되지 않은 댓글일때
                        viewHolderComment.imageButton.setVisibility(View.VISIBLE);
                    }
                }else{ // 작성자와 로그인 유저가 다를 때
                    viewHolderComment.imageButton.setVisibility(View.GONE);
                }
                viewHolderComment.toReply.setVisibility(View.VISIBLE);
            } else { // 대댓글 페이지 에서 이용
                viewHolderComment.imageButton.setVisibility(View.GONE);
                viewHolderComment.toReply.setVisibility(View.GONE);
            }

            if (item.getUser_url() != null && !item.getUser_url().equals("null") && !item.getUser_url().equals("")) {
                Glide.with(holder.itemView.getContext()).load("http://3.38.213.196" + item.getUser_url()).into(viewHolderComment.userImg);
            } else {
                viewHolderComment.userImg.setImageResource(R.drawable.img);
            }

            viewHolderComment.userNick.setText(item.getUser_nick());
            viewHolderComment.commentDate.setText(item.getReply_create_date());
            viewHolderComment.commentContent.setText(item.getReply_content());

            if (item.getReply_del() == 1){
                int grayColor = holder.itemView.getContext().getColor(R.color.gray);
                viewHolderComment.commentContent.setTextColor(grayColor);
                viewHolderComment.commentContent.setText("삭제된 댓글입니다.");
            } else{
                int black = holder.itemView.getContext().getColor(R.color.black);
                viewHolderComment.commentContent.setTextColor(black);
            }

            viewHolderComment.toReply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), getMeetingReply.class);
                    intent.putExtra("boardId", boardId);
                    intent.putExtra("leaderId", leaderId);
                    intent.putExtra("replyRef", item.getReply_ref());
                    v.getContext().startActivity(intent);
                }
            });
            viewHolderComment.imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(onMoreMenuClickListener != null){
                        int currentAdapterPosition = viewHolderComment.getBindingAdapterPosition();
                        if(currentAdapterPosition  != RecyclerView.NO_POSITION){
                            onMoreMenuClickListener.onMoreMenuClick(currentAdapterPosition);
                        }
                    }
                }
            });

        } else if (holder instanceof ViewHolderReply) { // 대댓글 인경우
            ViewHolderReply viewHolderReply = (ViewHolderReply) holder;
            // 데이터 설정 및 대 댓글 레이아웃 B에 대한 처리

            // 로그인한 유저가 댓글을 작성한 유저가 같을경우
            // 더보기 버튼을 보이게함
            // 다를경우 보이지 않게함
            if(where == 2){
                if(item.getUser_seq() == userId){ // 댓글 작성자와 로그인한 유저가 같을때
                    if(item.getReply_del() == 1){ // 댓글이 삭제되었을경우
                        viewHolderReply.imageButton2.setVisibility(View.GONE);
                    }else { // 댓글이 삭제되지 않았을 경우
                        viewHolderReply.imageButton2.setVisibility(View.VISIBLE);
                    }
                }else{
                    viewHolderReply.imageButton2.setVisibility(View.GONE);
                }
            } else {
                viewHolderReply.imageButton2.setVisibility(View.GONE);
            }


            if (item.getUser_url() != null && !item.getUser_url().equals("null") && !item.getUser_url().equals("")) {
                Glide.with(holder.itemView.getContext()).load("http://3.38.213.196" + item.getUser_url()).into(viewHolderReply.replyUserImg);
            } else {
                viewHolderReply.replyUserImg.setImageResource(R.drawable.img);
            }

            viewHolderReply.replyUserNick.setText(item.getUser_nick());
            viewHolderReply.replyDate.setText(item.getReply_create_date());
            viewHolderReply.replyContent.setText(item.getReply_content());

            if (item.getReply_del() == 1){
                int grayColor = holder.itemView.getContext().getColor(R.color.gray);
                viewHolderReply.replyContent.setTextColor(grayColor);
                viewHolderReply.replyContent.setText("삭제된 댓글입니다.");
            } else{
                int black = holder.itemView.getContext().getColor(R.color.black);
                viewHolderReply.replyContent.setTextColor(black);
            }
            viewHolderReply.imageButton2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(onReplyMoreMenuClickListener != null){
                        int currentAdapterPosition = viewHolderReply.getBindingAdapterPosition();
                        if(currentAdapterPosition  != RecyclerView.NO_POSITION){
                            onReplyMoreMenuClickListener.onReplyMoreMenuClick(currentAdapterPosition);
                        }
                    }
                }
            });

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
