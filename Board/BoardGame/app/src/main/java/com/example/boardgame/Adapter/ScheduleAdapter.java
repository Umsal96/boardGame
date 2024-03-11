package com.example.boardgame.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.boardgame.R;
import com.example.boardgame.item.ScheduleItem;
import com.example.boardgame.item.ScheduleMemberItem;
import com.example.boardgame.utility.OnItemClickListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Date;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ViewHolder> {
    private ArrayList<ScheduleItem> scheduleItems; // 아이템 어레이리스트 선언
    private ArrayList<ScheduleMemberItem> scheduleMemberItems;
    private int userId; // 유저 고유 아이디
    private OnItemClickListener listener; // 일정에 참가한 유저의 목록을 보기 위한 클릭 리스터
    private OnScheduleCancelClickListener onScheduleCancelClickListener;
    private OnScheduleAttendClickListener onScheduleAttendClickListener;
    ArrayList<ScheduleMemberItem> sm = new ArrayList<>();

    // 일정의 취소 버튼을 눌렀을때 이벤트
    public interface OnScheduleCancelClickListener{
        void onScheduleCancelClick(int scheduleSeq, int position);
    }

    // 일정의 참가 벝튼을 눌렀을때 이벤트
    public interface OnScheduleAttendClickListener{
        void onScheduleAttendClick(int scheduleSeq, int position);
    }

    public void setOnScheduleAttendClickListener(OnScheduleAttendClickListener listener){
        onScheduleAttendClickListener = listener;
    }
    public void setOnScheduleCancelClickListener(OnScheduleCancelClickListener listener){
        onScheduleCancelClickListener = listener;
    }

    // 뷰 홀더에 ui와 변수를 연결
    public static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView scheduleDate, scheduleDateCount, scheduleCafeAddress, scheduleNum, scheduleNumString, scheduleItemTitle, scheduleCafeName;
        public Button scheduleAttend;
        public Button scheduleCancel;
        private Button scheduleUser;
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
            scheduleCancel = itemView.findViewById(R.id.scheduleCancel);
            scheduleUser = itemView.findViewById(R.id.scheduleUser);

        }
    }
    public ScheduleAdapter(ArrayList<ScheduleItem> DataSet, ArrayList<ScheduleMemberItem> smt, int userId, OnItemClickListener listener){
        this.scheduleItems = DataSet;
        this.scheduleMemberItems = smt;
        this.listener = listener;
        this.userId = userId;
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
//        System.out.println("유저의 고유 아이디 : " + userId);

        // 해당 아이템의 고유 일정 아이디와 일정멤버리스트에서 해당 일정 고유 아이디가 같으면 1차적으로 다른 리스트로 옮김
        // 해당 일정과 맴버를 거르기 위해서함
        sm.clear();
        for (int i = 0; i < scheduleMemberItems.size(); i++) {
            if(scheduleMemberItems.get(i).getSchedule_seq() == item.getScheduleSeq()) {
                sm.add(scheduleMemberItems.get(i));
            }
        }
        boolean flag = false;
        System.out.println("내부");
        for (int i = 0; i < sm.size(); i++) {
            System.out.println(sm.get(i).getUser_seq());
            if(sm.get(i).getUser_seq() == userId){
                System.out.println("모임에 참가가되어있습니다.");
                holder.scheduleAttend.setVisibility(View.GONE);
                holder.scheduleCancel.setVisibility(View.VISIBLE);
                flag = true;
                break;
            }
        }
        if(!flag){
            holder.scheduleCancel.setVisibility(View.GONE);
            holder.scheduleAttend.setVisibility(View.VISIBLE);
        }

        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat("MM 월 d일 (E)", Locale.getDefault());

        SimpleDateFormat timeInputFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        SimpleDateFormat timeOutputFormat = new SimpleDateFormat("a h:mm", Locale.getDefault());

        String formatted;
        String formatTime;

        SimpleDateFormat inputFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date scheduleDateTime = null;

        try {
            Date scheduleTime = timeInputFormat.parse(item.getSchedule_time());
            formatTime = timeOutputFormat.format(scheduleTime);

            Date scheduleDate = inputFormat.parse(item.getSchedule_date());
            formatted = outputFormat.format(scheduleDate);

            scheduleDateTime = inputFormat1.parse(item.getSchedule_date() + " " + item.getSchedule_time());

            // 현재 날짜와 시간을 가져오기
            Calendar currentDate = Calendar.getInstance();

            // 주어진 시간과 날짜를 Calendar 객체로 변환
            Calendar scheduleCalendar = Calendar.getInstance();
            scheduleCalendar.setTime(scheduleDateTime);

            // 날짜 차이 계산
            long timeDifferenceMillis = scheduleCalendar.getTimeInMillis() - currentDate.getTimeInMillis();

            if(timeDifferenceMillis >= 24 * 60 * 60 * 1000){
                // 1일 이상 남았을 경우
                long dayDifference = timeDifferenceMillis / (24 * 60 * 60 * 1000);
                holder.scheduleDateCount.setText(dayDifference + " 일 남았습니다.");
            }else if (timeDifferenceMillis >= 60 * 60 * 1000) {
                // 1시간 이상 남았을 경우
                long hoursDifference = timeDifferenceMillis / (60 * 60 * 1000);
                holder.scheduleDateCount.setText(hoursDifference + " 시간 남았습니다.");
            } else if (timeDifferenceMillis >= 60 * 1000) {
                // 1분 이상 남았을 경우
                long minutesDifference = timeDifferenceMillis / (60 * 1000);
                holder.scheduleDateCount.setText(minutesDifference + " 분 남았습니다.");
            } else {
                // 예약 시간이 현재 시간보다 이전인 경우
                holder.scheduleDateCount.setText("이미 시작한 일정입니다.");
            }

        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        holder.scheduleDate.setText(formatted + " " + formatTime);
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

        // 일정에 참가한 유저를 확인하기 위한 이벤트 클릭 리스너
        holder.scheduleUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(item.getScheduleSeq());
            }
        });

        // 일정의 참가 버튼을 클릭했을때 실행되는 이벤트
        holder.scheduleAttend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onScheduleAttendClickListener != null){
                    int currentAdapterPosition = holder.getBindingAdapterPosition();
                    if (currentAdapterPosition != RecyclerView.NO_POSITION) {
                        onScheduleAttendClickListener.onScheduleAttendClick(item.getScheduleSeq(), currentAdapterPosition);
                    }

                }
            }
        });

        // 일정의 참가 취소 버튼을 클릭했을때 실행되는 이벤트
        holder.scheduleCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onScheduleCancelClickListener != null){
                    int currentAdapterPosition = holder.getBindingAdapterPosition();
                    if (currentAdapterPosition != RecyclerView.NO_POSITION) {
                        onScheduleCancelClickListener.onScheduleCancelClick(item.getScheduleSeq(), currentAdapterPosition);
                    }
                }
            }
        });
    }
    @Override
    public int getItemCount(){
        if(scheduleItems == null){
            return 0;
        }
        return scheduleItems.size();
    }
}
