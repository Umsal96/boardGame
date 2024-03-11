package com.example.boardgame.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.boardgame.R;
import com.example.boardgame.item.UserNItem;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ScheduleUserAdapter extends RecyclerView.Adapter<ScheduleUserAdapter.ViewHolder> {
    private ArrayList<UserNItem> ni; // 아이템 어레이 리스트
    // 뷰 홀더에 ui와 변수를 연결
    public static class ViewHolder extends RecyclerView.ViewHolder{

        private CircleImageView userUrl;
        private TextView userNick;

        public ViewHolder(@NonNull View itemView){
            super(itemView);

            userUrl = itemView.findViewById(R.id.userUrl);
            userNick = itemView.findViewById(R.id.userNick);
        }
    }

    public ScheduleUserAdapter(ArrayList<UserNItem> DataSet) {
        this.ni = DataSet;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.schedule_user_item, parent, false);
        ScheduleUserAdapter.ViewHolder viewHolder = new ScheduleUserAdapter.ViewHolder(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleUserAdapter.ViewHolder holder, int position){
        UserNItem item = ni.get(position);

        holder.userNick.setText(item.getUserNick());
        if(item.getUserUrl() != null && !item.getUserUrl().equals("null") && !item.getUserUrl().equals("")){
            Glide.with(holder.itemView.getContext()).load("http://3.38.213.196" + item.getUserUrl()).into(holder.userUrl);
        } else {
            holder.userUrl.setImageResource(R.drawable.img2);
        }
    }

    @Override
    public int getItemCount(){
        if(ni == null){
            return 0;
        }
        return ni.size();
    }
}
