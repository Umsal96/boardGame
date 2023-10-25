package com.example.boardgame.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.boardgame.R;
import com.example.boardgame.item.UserItem;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private ArrayList<UserItem> userItems; // 아이템 어레이 리스트 선언

    // 뷰 홀더에 ui와 변수를 연결
    public static class ViewHolder extends RecyclerView.ViewHolder{
        private CircleImageView userUrl;
        private TextView userNick, leader;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            userUrl = itemView.findViewById(R.id.userUrl);
            userNick = itemView.findViewById(R.id.userNick);
            leader = itemView.findViewById(R.id.leader);
        }
    }

    public UserAdapter(ArrayList<UserItem> DataSet){
        this.userItems = DataSet;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.meeting_user_item, parent, false);
        UserAdapter.ViewHolder viewHolder = new UserAdapter.ViewHolder(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.ViewHolder holder, int position) {
        UserItem item = userItems.get(position);

        holder.userNick.setText(item.getUserNick());
        if (item.getLeader() == 1){
            holder.leader.setText("모임장");
        } else if (item.getLeader() == 0) {
            holder.leader.setText("일반회원");
        }
        if(item.getUserUrl() != null && !item.getUserUrl().equals("null") && !item.getUserUrl().equals("")){
            Glide.with(holder.itemView.getContext()).load("http://3.38.213.196" + item.getUserUrl()).into(holder.userUrl);
        } else {
            holder.userUrl.setImageResource(R.drawable.img2);
        }
    }

    @Override
    public int getItemCount() {
        if(userItems == null){
            return 0;
        }
        return userItems.size();
    }
}
