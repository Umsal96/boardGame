package com.example.boardgame.Adapter;

import com.bumptech.glide.Glide;
import com.example.boardgame.R;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.boardgame.item.WaitingItem;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class WaitingAdapter extends RecyclerView.Adapter<WaitingAdapter.ViewHolder> {

    private ArrayList<WaitingItem> waitingItems; // 아이템 어레이 리스트 선언
    private OnWaitingAcceptClickListener onWaitingAcceptClickListener;
    private OnWaitingRefuseClickListener onWaitingRefuseClickListener;

    // 수락 버튼을 눌렀을떄
    public interface OnWaitingAcceptClickListener{
        void onWaitingAcceptClick(int userSeq, int meetingSeq, int position);
    }
    // 거절 버튼을 눌렀을떄
    public interface OnWaitingRefuseClickListener{
        void onWaitingRefuseClick(int userSeq, int meetingSeq, int position);
    }

    public void setOnWaitingAcceptClickListener(OnWaitingAcceptClickListener listener){
        onWaitingAcceptClickListener = listener;
    }

    public void setOnWaitingRefuseClickListener(OnWaitingRefuseClickListener listener){
        onWaitingRefuseClickListener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private CircleImageView userUrl; // 유저의 프로필
        private TextView userNick; // 유저의 닉네임
        private Button accept; // 수락 버튼
        private Button refuse; // 거절 버튼
        public ViewHolder(@NonNull View itemView){
            super(itemView);
            userUrl = itemView.findViewById(R.id.userUrl);
            userNick = itemView.findViewById(R.id.userNick);
            accept = itemView.findViewById(R.id.accept);
            refuse = itemView.findViewById(R.id.refuse);
        }
    }

    public WaitingAdapter(ArrayList<WaitingItem> DataSet){
        this.waitingItems = DataSet;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.waiting_item, parent, false);
        WaitingAdapter.ViewHolder viewHolder = new WaitingAdapter.ViewHolder(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull WaitingAdapter.ViewHolder holder, int position){
        WaitingItem item = waitingItems.get(position);

        holder.userNick.setText(item.getUserNick());
        if(item.getUserUrl() != null && !item.getUserUrl().equals("null") && !item.getUserUrl().equals("")){
            Glide.with(holder.itemView.getContext()).load("http://3.38.213.196" + item.getUserUrl()).into(holder.userUrl);
        } else {
            holder.userUrl.setImageResource(R.drawable.img2);
        }

        // 수락 버튼을 클릭했을떄
        holder.accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onWaitingAcceptClickListener != null){
                    int currentAdapterPosition = holder.getBindingAdapterPosition();
                    if(currentAdapterPosition != RecyclerView.NO_POSITION){
                        onWaitingAcceptClickListener.onWaitingAcceptClick(item.getUserSeq(), item.getMeetingSeq(), currentAdapterPosition);
                    }
                }
            }
        });

        // 거절 번튼을 클릭했을때
        holder.refuse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onWaitingRefuseClickListener != null){
                    int currentAdapterPosition = holder.getBindingAdapterPosition();
                    if(currentAdapterPosition != RecyclerView.NO_POSITION){
                        onWaitingRefuseClickListener.onWaitingRefuseClick(item.getUserSeq(), item.getMeetingSeq(), currentAdapterPosition);
                    }
                }
            }
        });
    }
    @Override
    public int getItemCount(){
        if(waitingItems == null){
            return 0;
        }
        return waitingItems.size();
    }


}
