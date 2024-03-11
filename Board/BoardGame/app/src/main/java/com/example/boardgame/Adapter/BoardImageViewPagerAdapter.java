package com.example.boardgame.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.boardgame.R;

public class BoardImageViewPagerAdapter extends RecyclerView.Adapter<BoardImageViewPagerAdapter.ViewHolder> {

    private String[] imageDataList;

    private BoardImageViewPagerAdapter.OnCancelClickListener onCancelClickListener;
    public interface OnCancelClickListener{
        void onCancelClick(int position);
    }

    public void setOnCancelClickListener(BoardImageViewPagerAdapter.OnCancelClickListener listener){
        onCancelClickListener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView image;
        private ImageButton cancel;

        public ViewHolder(@NonNull View itemView){
            super(itemView);
            image = itemView.findViewById(R.id.image);
            cancel = itemView.findViewById(R.id.cancel);
        }
    }

    public BoardImageViewPagerAdapter(String[] imageDataList){
        this.imageDataList = imageDataList;
    }

    @NonNull
    @Override
    public BoardImageViewPagerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.image_viewpager_item, parent, false);

        BoardImageViewPagerAdapter.ViewHolder viewHolder = new BoardImageViewPagerAdapter.ViewHolder(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull BoardImageViewPagerAdapter.ViewHolder holder, int position){
        String uri = imageDataList[position];
        System.out.println("on bind 이미지 : " + uri);
        Glide.with(holder.itemView.getContext()).load("http://3.38.213.196" + uri).into(holder.image);

        holder.cancel.setVisibility(View.GONE);
        holder.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onCancelClickListener != null){
                    int currentAdapterPosition = holder.getBindingAdapterPosition();
                    if(currentAdapterPosition != RecyclerView.NO_POSITION){
                        onCancelClickListener.onCancelClick(currentAdapterPosition);
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount(){
        if(imageDataList == null){
            return 0;
        }
        return imageDataList.length;
    }
}
