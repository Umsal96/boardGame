package com.example.boardgame.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.boardgame.R;
import com.example.boardgame.item.ChattingItem;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChattingAdapter extends  RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private ArrayList<ChattingItem> chattingItems;
    int userId; // 로그인한 유저의 고유 아이디
    @Override
    public int getItemViewType(int position){
        int userSeq = chattingItems.get(position).getUser_seq();
        if(userSeq == userId){ // 내가 한 채팅일 경우
            System.out.println("내가 한 채팅이 맞음");
            return 0;
        } else { // 내가 한 채팅이 아닐경우
            System.out.println("내가 한 채팅이 아님");
            return 1;
        }
    }

    // 내 채팅 뷰 홀더
    public static class ViewHolderMyChat extends RecyclerView.ViewHolder{
        private TextView myChatContent, myChatCheckRead, myChatDate;

        public ViewHolderMyChat(@NonNull View itemView){
            super(itemView);

            myChatContent = itemView.findViewById(R.id.myChatContent);
            myChatCheckRead = itemView.findViewById(R.id.myChatCheckRead);
            myChatDate = itemView.findViewById(R.id.myChatDate);
        }
    }

    // 다른사람 뷰 홀더
    public static class ViewHolderOtherChat extends RecyclerView.ViewHolder{
        private CircleImageView userUrl;
        private TextView otherChatContent, otherChatCheckRead, otherChatDate, otherNick;

        public ViewHolderOtherChat(@NonNull View itemView){
            super(itemView);

            userUrl = itemView.findViewById(R.id.userUrl);
            otherChatContent = itemView.findViewById(R.id.otherChatContent);
            otherChatCheckRead = itemView.findViewById(R.id.otherChatCheckRead);
            otherChatDate = itemView.findViewById(R.id.otherChatDate);
            otherNick = itemView.findViewById(R.id.otherNick);
        }
    }

    public ChattingAdapter(ArrayList<ChattingItem> DataSet, int userId){
        this.chattingItems = DataSet;
        this.userId = userId;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view;
        if(viewType == 0){ // 내 채팅일 경우
            view = inflater.inflate(R.layout.my_chat_item, parent, false);
            return new ViewHolderMyChat(view);
        } else {
            view = inflater.inflate(R.layout.other_chat_item, parent, false);
            return new ViewHolderOtherChat(view);
        }
    }

    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position){
        ChattingItem item = chattingItems.get(position);
        if(holder instanceof ViewHolderMyChat){ // 내 채팅일 경우
            ViewHolderMyChat viewHolderMyChat = (ViewHolderMyChat) holder;
            viewHolderMyChat.myChatDate.setText(item.getMessage_date());
            viewHolderMyChat.myChatContent.setText(item.getMessage_content());
            viewHolderMyChat.myChatCheckRead.setText(Integer.toString(item.getMessage_read()));
        } else if (holder instanceof ViewHolderOtherChat) {
            ViewHolderOtherChat viewHolderOtherChat = (ViewHolderOtherChat) holder;
            viewHolderOtherChat.otherNick.setText(item.getUser_nickname());
            viewHolderOtherChat.otherChatDate.setText(item.getMessage_date());
            viewHolderOtherChat.otherChatContent.setText(item.getMessage_content());
            viewHolderOtherChat.otherChatCheckRead.setText(Integer.toString(item.getMessage_read()));
            if (item.getUser_url() != null && !item.getUser_url().equals("null") && !item.getUser_url().equals("")) {
                Glide.with(holder.itemView.getContext()).load("http://3.38.213.196" + item.getUser_url()).into(viewHolderOtherChat.userUrl);
            } else {
                viewHolderOtherChat.userUrl.setImageResource(R.drawable.img);
            }
        }
    }

    @Override
    public int getItemCount(){
        if(chattingItems == null){
            return 0;
        }
        return chattingItems.size();
    }
}
