package com.example.boardgame.Adapter;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.boardgame.R;

import java.util.ArrayList;

public class GameImageViewPagerAdapter extends RecyclerView.Adapter<GameImageViewPagerAdapter.ViewHolder> {
    private ArrayList<Uri> imageDataList;
    private ImageViewPagerAdapter.OnCancelClickListener onCancelClickListener;
    public interface OnCancelClickListener{
        void onCancelClick(int position);
    }
    public void setOnCancelClickListener(ImageViewPagerAdapter.OnCancelClickListener listener){
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

    public GameImageViewPagerAdapter(ArrayList<Uri> imageDataList){
        this.imageDataList = imageDataList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.image_viewpager_item, parent, false);

        GameImageViewPagerAdapter.ViewHolder viewHolder = new GameImageViewPagerAdapter.ViewHolder(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull GameImageViewPagerAdapter.ViewHolder holder, int position){
        Uri uri = imageDataList.get(position);
        holder.image.setImageURI(uri);
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
        return imageDataList.size();
    }
}
