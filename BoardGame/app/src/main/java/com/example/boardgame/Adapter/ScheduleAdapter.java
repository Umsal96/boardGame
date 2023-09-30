package com.example.boardgame.Adapter;

import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.boardgame.R;
import com.example.boardgame.item.MeetingItem;
import com.example.boardgame.item.ScheduleItem;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ViewHolder> {

    private ArrayList<ScheduleItem> scheduleItems; // 아이템 어레이리스트 선언
    private MeetingAdapter.OnItemClickListener itemClickListener; // 클릭 이벤트 선언

    public interface OnItemClickListener{
        void onItemClickListener(int position, String uri, int id);
    }

    // 뷰 홀더에 ui와 변수를 연결
    public static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView scheduleDate, scheduleDateCount, scheduleCafeAddress, scheduleNum, scheduleNumString, scheduleItemTitle, scheduleCafeName;

        private Button scheduleAttend;

        public ViewHolder(@NonNull View itemView){
            super(itemView);
            scheduleDate = itemView.findViewById(R.id.scheduleDate);
            scheduleDateCount = itemView.findViewById(R.id.scheduleDateCount);
            scheduleCafeAddress = itemView.findViewById(R.id.scheduleCafeAddress);
            scheduleNum = itemView.findViewById(R.id.scheduleNum);
            scheduleNumString = itemView.findViewById(R.id.scheduleNumString);
            scheduleItemTitle = itemView.findViewById(R.id.scheduleItemTitle);
            scheduleCafeName = itemView.findViewById(R.id.scheduleCafeName);
            scheduleAttend = itemView.findViewById(R.id.scheduleAttend);
        }

    }
    public ScheduleAdapter(ArrayList<ScheduleItem> DataSet){
        this.scheduleItems = DataSet;
    }

    @NonNull
    @Override
    public ScheduleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.schedule_item, parent, false);
        ScheduleAdapter.ViewHolder viewHolder = new ScheduleAdapter.ViewHolder(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleAdapter.ViewHolder holder, int position){
        ScheduleItem item = scheduleItems.get(position);

        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat("MM 월 d일 (E)", Locale.getDefault());

        SimpleDateFormat TinputFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        SimpleDateFormat ToutputFormat = new SimpleDateFormat("a h:mm", Locale.getDefault());

        String formatted;
        String formatTime;
        try {
            Date time = TinputFormat.parse(item.getSchedule_time());
            formatTime = ToutputFormat.format(time);

            Date date = inputFormat.parse(item.getSchedule_date());
            formatted = outputFormat.format(date);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        holder.scheduleDate.setText(formatted + " " + formatTime);
        holder.scheduleDateCount.setText("1일 남았습니다.");
        holder.scheduleCafeAddress.setText(item.getSchedule_place_address());
        holder.scheduleCafeName.setText(item.getSchedule_place_name());
        int max = item.getSchedule_member_max();
        int current = item.getSchedule_member_current();
        int ue = max - current;
        String smax = String.valueOf(max);
        String scurrent = String.valueOf(current);
        String sue = String.valueOf(ue);
        holder.scheduleNum.setText(scurrent + "/" + smax);
        holder.scheduleNumString.setText(sue + " 자리 남음");
        holder.scheduleItemTitle.setText(item.getScheduleTitle());
    }

    @Override
    public int getItemCount(){
        if(scheduleItems == null){
            return 0;
        }
        return scheduleItems.size();
    }
}
