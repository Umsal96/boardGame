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

public class GetGameViewPagerAdapter extends RecyclerView.Adapter<GetGameViewPagerAdapter.ViewHolder> {

    private String[] imageDataList;

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView image;
        private ImageButton cancel;

        public ViewHolder(@NonNull View itemView){
            super(itemView);
            image = itemView.findViewById(R.id.image);
            cancel = itemView.findViewById(R.id.cancel);
        }
    }

    public GetGameViewPagerAdapter(String[] imageDataList){
        this.imageDataList = imageDataList;
    }

    public void setImages(String[] imageDataList){
        this.imageDataList = imageDataList;
    }

    @NonNull
    @Override
    public GetGameViewPagerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.image_viewpager_item, parent, false);

        GetGameViewPagerAdapter.ViewHolder viewHolder = new GetGameViewPagerAdapter.ViewHolder(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull GetGameViewPagerAdapter.ViewHolder holder, int position){
        String uri = imageDataList[position];
        System.out.println("on bind 이미지 " + uri);
        Glide.with(holder.itemView.getContext()).load("http://3.38.213.196" + uri).into(holder.image);

        holder.cancel.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount(){
        if(imageDataList == null){
            return 0;
        }
        return imageDataList.length;
    }
}
