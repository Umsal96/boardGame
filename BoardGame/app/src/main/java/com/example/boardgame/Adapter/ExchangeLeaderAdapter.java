package com.example.boardgame.Adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.boardgame.R;
import com.example.boardgame.getMeeting;
import com.example.boardgame.item.UserItem;

import java.io.IOException;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ExchangeLeaderAdapter extends RecyclerView.Adapter<ExchangeLeaderAdapter.ViewHolder> {

    private ArrayList<UserItem> ui;
    private OnItemClickListener itemClickListener;
    int leaderUserId;
    int meetingId; // 모임의 고유 아이디

    // 클릭 이벤트를 위한 코드
    public interface OnItemClickListener{
        void onItemClicked(int position, int userId);
    }

    // 뷰 홀더에 ui와 변수를 연결
    public static class ViewHolder extends RecyclerView.ViewHolder{
        private CircleImageView userUrl;
        private TextView userNick, leader;

        public ViewHolder(@NonNull View itemView){
            super(itemView);

            userUrl = itemView.findViewById(R.id.userUrl);
            userNick = itemView.findViewById(R.id.userNick);
            leader = itemView.findViewById(R.id.leader);
        }
    }

    public ExchangeLeaderAdapter(ArrayList<UserItem> DataSet, int leaderUserId, int meetingId){
        this.ui = DataSet;
        this.leaderUserId = leaderUserId;
        this.meetingId = meetingId;
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        itemClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.meeting_user_item, parent, false);
        ExchangeLeaderAdapter.ViewHolder viewHolder = new ExchangeLeaderAdapter.ViewHolder(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = viewHolder.getLayoutPosition();
                System.out.println("클릭한 유저의 고유 아이디 : " + ui.get(position).getUserSeq());
                System.out.println("클릭한 모임의 리더 고유 아이디 : " + leaderUserId );
                System.out.println("클릭한 모임의 고유 아이디 : " + meetingId);
                changeUser(v, ui.get(position).getUserSeq(), leaderUserId, meetingId);
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ExchangeLeaderAdapter.ViewHolder holder, int position){
        UserItem item = ui.get(position);

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
        if(ui == null){
            return 0;
        }
        return ui.size();
    }

    // 방장을 일반 회원으로 일반 회원을 방장으로 바꾸는 메소드
    private void changeUser(View v, int userSeq, int leaderUserId, int meetingId){
        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://3.38.213.196/meeting/changeMeeting.php").newBuilder();
        urlBuilder.addQueryParameter("leaderId", String.valueOf(leaderUserId));
        urlBuilder.addQueryParameter("userId", String.valueOf(userSeq));
        urlBuilder.addQueryParameter("meetingId", String.valueOf(meetingId));
        String url = urlBuilder.build().toString();

        Request request = new Request.Builder()
                .url(url)
                .build();

        OkHttpClient client = new OkHttpClient();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()){
                    Intent intent = new Intent(v.getContext(), getMeeting.class);
                    intent.putExtra("where", 1);
                    intent.putExtra("id", meetingId);
                    v.getContext().startActivity(intent);
                }
            }
        });
    }
}
