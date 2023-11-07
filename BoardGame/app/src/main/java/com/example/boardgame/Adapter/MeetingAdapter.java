package com.example.boardgame.Adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.boardgame.R;
import com.example.boardgame.getMeeting;
import com.example.boardgame.item.MeetingItem;

import java.util.ArrayList;

public class MeetingAdapter extends RecyclerView.Adapter<MeetingAdapter.ViewHolder> {

    private ArrayList<MeetingItem> meetingItems; // 아이템 어레이리스트 선언
    private OnItemClickListener itemClickListener; // 클릭 이벤트 선언

    public interface OnItemClickListener{
        void onItemClickListener(int position, String uri, String name, int members, int maxMembers, String content);
    }

    // 뷰 홀더에 ui와 변수를 연결
    public static class ViewHolder extends RecyclerView.ViewHolder{

        private TextView meetingName, meetingContent, meetingMember, meetingMaxMember; // 텍스트뷰 미팅 이름 미팅 내용 미팅의 인원수
        private ImageView meetingImg; // 모임의 이미지
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            meetingName = itemView.findViewById(R.id.meetingName); // 모임 이름
            meetingContent = itemView.findViewById(R.id.meetingContent); // 모임 내용
            meetingMember = itemView.findViewById(R.id.meetingMember); // 모임 현재 맴버 숫자
            meetingMaxMember = itemView.findViewById(R.id.meetingMaxMember); // 맴버의 최대 숫자
            meetingImg = itemView.findViewById(R.id.meetingImg); // 모임의 이미지 뷰
        }
    }
    public MeetingAdapter(ArrayList<MeetingItem> DataSet){
        this.meetingItems = DataSet;
    }

    @NonNull
    @Override
    public MeetingAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.meeting_list_item, parent, false);
        MeetingAdapter.ViewHolder viewHolder = new MeetingAdapter.ViewHolder(view);

        // 아이템을 클릭했을때 발생되는 이벤트
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = viewHolder.getLayoutPosition();
                int id = meetingItems.get(position).getMeetingId();
                Intent intent = new Intent(v.getContext(), getMeeting.class);
                intent.putExtra("id", id);
                intent.putExtra("where", 1);
                v.getContext().startActivity(intent);
            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MeetingAdapter.ViewHolder holder, int position) {
        MeetingItem item = meetingItems.get(position);

        // 모임 이름의 원본을 받아옴
        String name = item.getMeetingName();
        // 모임 내용의 원본을 받아옴
        String content = item.getMeetingContent();

        String truncatedName;

        String truncatedContent;

        // 만약 모임의 이름이 13글자 이상인 경우 그 뒤 부터는 ......으로 보이게 함
        if(name.length() >= 13){
            truncatedName = name.substring(0, 13) + ".......";
        } else {
            truncatedName = name;
        }

        // 만약 모임의 내용이 20 글자 이상인 경우 그 뒤부터는 ....... 으로 보이게함
        if(content.length() >= 20){
            truncatedContent = content.substring(0, 20) + "......";
        } else {
            truncatedContent = content;
        }

        // 만약 모임의 내용이 20글자 이상인 경우 그 뒤 부터는 ......으로 보이게 함

        holder.meetingName.setText(truncatedName);
        holder.meetingContent.setText(truncatedContent);
        holder.meetingMember.setText(String.valueOf(item.getMeetingCurrent()));
        holder.meetingMaxMember.setText(String.valueOf(item.getMeetingMembers()));
        if (item.getMeetingUrl() != null && !item.getMeetingUrl().equals("null") && !item.getMeetingUrl().equals("")) {
            Glide.with(holder.itemView.getContext()).load("http://3.38.213.196" + item.getMeetingUrl()).into(holder.meetingImg);
        } else {
            holder.meetingImg.setImageResource(R.drawable.img);
        }

    }

    @Override
    public int getItemCount() {
        if(meetingItems == null){
            return 0;
        }
        return meetingItems.size();
    }
}
