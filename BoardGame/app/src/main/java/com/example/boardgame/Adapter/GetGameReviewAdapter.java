package com.example.boardgame.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.boardgame.R;
import com.example.boardgame.item.GameReviewItem;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class GetGameReviewAdapter extends RecyclerView.Adapter<GetGameReviewAdapter.ViewHolder>{

    private ArrayList<GameReviewItem> gameItems;
    private String[] images;
    private int userId;

    private OnMoreMenuClickListener onMoreMenuClickListener;

    public interface OnMoreMenuClickListener{
        void onMoreMenuClick(int position, View v);
    }

    public void setOnMoreMenuClickListener(OnMoreMenuClickListener listener){
        onMoreMenuClickListener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private CircleImageView userProfile;
        private TextView userNick, reviewDate, reviewContent, reviewGrade;
        private RatingBar reviewGradeBar;
        private ImageButton moreMenu;
        private ViewPager2 reviewViewPager;

        public ViewHolder(@NonNull View itemView){
            super(itemView);
            userProfile = itemView.findViewById(R.id.userProfile);
            userNick = itemView.findViewById(R.id.userNick);
            reviewDate = itemView.findViewById(R.id.reviewDate);
            reviewContent = itemView.findViewById(R.id.reviewContent);
            reviewGradeBar = itemView.findViewById(R.id.reviewGradeBar);
            moreMenu = itemView.findViewById(R.id.moreMenu);
            reviewViewPager = itemView.findViewById(R.id.reviewViewPager);
            reviewGrade = itemView.findViewById(R.id.reviewGrade);

        }
    }

    public GetGameReviewAdapter(ArrayList<GameReviewItem> DataSet, int userId){
        this.gameItems = DataSet;
        this.userId = userId;
    }

    public void setData(ArrayList<GameReviewItem> DataSet){
        this.gameItems = DataSet;
    }

    @NonNull
    @Override
    public GetGameReviewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.game_review_item, parent, false);
        GetGameReviewAdapter.ViewHolder viewHolder = new GetGameReviewAdapter.ViewHolder(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull GetGameReviewAdapter.ViewHolder holder, int position){
        GameReviewItem item = gameItems.get(position);
        holder.userNick.setText(item.getUser_nickname());
        holder.reviewContent.setText(item.getReview_content());
        holder.reviewGrade.setText(String.valueOf((float) item.getReview_grade()));
        holder.reviewGradeBar.setRating(item.getReview_grade());
        holder.reviewDate.setText(item.getReview_create_date());

        if(item.getUser_seq() == userId){
            // 작성자와 로그인 유저와 같은때
            holder.moreMenu.setVisibility(View.VISIBLE);
        }else {
            // 작성자와 로그인 유저가 다를때
            holder.moreMenu.setVisibility(View.GONE);
        }

        if(item.getImage_urls() != null && !item.getImage_urls().equals("null") && !item.getImage_urls().equals("")){
            holder.reviewViewPager.setVisibility(View.VISIBLE);
            images = item.getImage_urls().split(",");
            GetGameViewPagerAdapter getGameViewPagerAdapter = new GetGameViewPagerAdapter(images);
            holder.reviewViewPager.setAdapter(getGameViewPagerAdapter);
            getGameViewPagerAdapter.notifyDataSetChanged();
        } else {
            holder.reviewViewPager.setVisibility(View.GONE);
        }

        if(item.getUser_url() != null && !item.getUser_url().equals("null") && !item.getUser_url().equals("")){
            Glide.with(holder.itemView.getContext()).load("http://3.38.213.196" + item.getUser_url()).into(holder.userProfile);
        } else {
            holder.userProfile.setImageResource(R.drawable.img2);
        }

        holder.moreMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onMoreMenuClickListener != null){
                    int currentAdapterPosition = holder.getBindingAdapterPosition();
                    if(currentAdapterPosition != RecyclerView.NO_POSITION){
                        onMoreMenuClickListener.onMoreMenuClick(currentAdapterPosition, v);
                    }
                }
            }
        });

    }

    @Override
    public int getItemCount(){
        if(gameItems == null){
            return 0;
        }
        return gameItems.size();
    }
}
