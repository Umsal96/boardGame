package com.example.boardgame.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.boardgame.R;
import com.example.boardgame.getMeetingBoard;
import com.example.boardgame.item.MeetingBoardItem;


import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MeetingBoardAdapter extends RecyclerView.Adapter<MeetingBoardAdapter.ViewHolder> {
    private ArrayList<MeetingBoardItem> boardItem;
    private OnItemMoreMenuClickListener onItemMoreMenuClickListener;
    public interface OnItemMoreMenuClickListener{
        void OnItemMoreMenuClickListener(int position, int meetingId, int userId);
    }
    public void setOnItemMoreMenuClickListener(OnItemMoreMenuClickListener listener){
        onItemMoreMenuClickListener = listener;
    }
    int LeaderUserid;
    int meetingId;
    private Context context;

    // 뷰 홀더에 ui와 변수를 연결
    public static class ViewHolder extends RecyclerView.ViewHolder{
        private CircleImageView userImg; // 작성자의 프로필
        private TextView userNick; // 작성자의 닉네임
        private TextView boardTime; // 작성 시간
        private TextView boardTitle; // 작성 제목
        private TextView boardContent; // 작성 내용
        private ImageButton moreMenu; // 추가 선택지
        private ImageView imageView4; // 대표 이미지
        private TextView boardType;

        public ViewHolder(@NonNull View itemView){
            super(itemView);
            userImg = itemView.findViewById(R.id.userImg);
            userNick = itemView.findViewById(R.id.userNick);
            boardTime = itemView.findViewById(R.id.boardTime);
            boardTitle = itemView.findViewById(R.id.boardTitle);
            boardContent = itemView.findViewById(R.id.boardContent);
            moreMenu = itemView.findViewById(R.id.moreMenu);
            imageView4 = itemView.findViewById(R.id.imageView4);
            boardType = itemView.findViewById(R.id.boardType);
        }
    }
    public MeetingBoardAdapter(ArrayList<MeetingBoardItem> DataSet, int LearUserId, int meetingId, Context context){
        this.boardItem = DataSet;
        this.LeaderUserid = LearUserId;
        this.meetingId = meetingId;
        this.context = context;
    }

    @NonNull
    @Override
    public MeetingBoardAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int ViewType){
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.meeting_board_item, parent, false);
        MeetingBoardAdapter.ViewHolder viewHolder = new MeetingBoardAdapter.ViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = viewHolder.getLayoutPosition();
                System.out.println("클릭한 뷰의 보드 고유 아이디 : " + boardItem.get(position).getBoardId());
                Intent intent = new Intent(context, getMeetingBoard.class);
                intent.putExtra("leaderId", LeaderUserid);
                intent.putExtra("boardId", boardItem.get(position).getBoardId());
                context.startActivity(intent);
            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MeetingBoardAdapter.ViewHolder holder, int position){

        MeetingBoardItem item = boardItem.get(position);

        holder.userNick.setText(item.getUserNick());
        holder.boardTitle.setText(item.getBoardTitle());
        holder.boardContent.setText(item.getBoardContent());
        holder.boardType.setText(item.getBoardType());
        holder.boardTime.setText(item.getCreateDate());

        holder.moreMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        if (item.getImgUrl() != null && !item.getImgUrl().equals("null") && !item.getImgUrl().equals("")) {
            System.out.println("이미지가 존재 합니다.");
            holder.imageView4.setVisibility(View.VISIBLE);
            Glide.with(holder.itemView.getContext()).load("http://3.38.213.196" + item.getImgUrl()).into(holder.imageView4);
        } else {
            System.out.println("이미지가 없습니다.");
            holder.imageView4.setVisibility(View.GONE);
        }

        if (item.getUserUrl() != null && !item.getUserUrl().equals("null") && !item.getUserUrl().equals("")) {
            Glide.with(holder.itemView.getContext()).load("http://3.38.213.196" + item.getUserUrl()).into(holder.userImg);
        } else {
            holder.userImg.setImageResource(R.drawable.img);
        }
    }

    @Override
    public int getItemCount(){
        if(boardItem == null){
            return 0;
        }
        return boardItem.size();
    }

}
