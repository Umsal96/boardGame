package com.example.boardgame.Adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.boardgame.R;
import com.example.boardgame.getGame;
import com.example.boardgame.item.GameItem;

import java.util.ArrayList;

public class BoardGameListAdapter extends RecyclerView.Adapter<BoardGameListAdapter.ViewHolder> {
    private ArrayList<GameItem> gameItems;

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView gameName, gameSummary, gameArg;
        private ImageView gameImage;
        private RatingBar gameRatingBar;

        public ViewHolder(@NonNull View itemView){
            super(itemView);
            gameName = itemView.findViewById(R.id.gameName);
            gameSummary = itemView.findViewById(R.id.gameSummary);
            gameArg = itemView.findViewById(R.id.gameArg);
            gameImage = itemView.findViewById(R.id.gameImage);
            gameRatingBar = itemView.findViewById(R.id.gameRatingBar);
        }
    }

    public BoardGameListAdapter(ArrayList<GameItem> DataSet){
        this.gameItems = DataSet;
    }

    @NonNull
    @Override
    public BoardGameListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.game_item, parent, false);
        BoardGameListAdapter.ViewHolder viewHolder = new BoardGameListAdapter.ViewHolder(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = viewHolder.getLayoutPosition();
                int gameId = gameItems.get(position).getGame_seq();
                Intent intent = new Intent(v.getContext(), getGame.class);
                intent.putExtra("gameId", gameId);
                v.getContext().startActivity(intent);
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull BoardGameListAdapter.ViewHolder holder, int position){
        GameItem item = gameItems.get(position);
        holder.gameName.setText(item.getGame_name());
        holder.gameSummary.setText(item.getGame_summary());
        holder.gameArg.setText(String.valueOf((float) item.getAverage_review_grade()));
        holder.gameRatingBar.setRating(item.getAverage_review_grade());

        if (item.getImage_url() != null && !item.getImage_url().equals("null") && !item.getImage_url().equals("")) {
            Glide.with(holder.itemView.getContext()).load("http://3.38.213.196" + item.getImage_url()).into(holder.gameImage);
        } else {
            holder.gameImage.setImageResource(R.drawable.img);
        }
    }

    @Override
    public int getItemCount(){
        if(gameItems == null){
            return 0;
        }
        return gameItems.size();
    }
}
