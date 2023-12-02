package com.example.boardgame.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.boardgame.R;
import com.example.boardgame.item.CafeGameItem;
import com.example.boardgame.item.FoodItem;

import java.util.ArrayList;
public class CafeGameAdapter extends RecyclerView.Adapter<CafeGameAdapter.ViewHolder>{
    private ArrayList<CafeGameItem> cafeGameItems;
    private FoodAdapter.OnDeleteClickLister onDeleteClickLister;
    // 아이템의 x 버튼을 눌렀을때
    public interface OnDeleteClickLister{
        void onDeleteClick(int position, int cafeGameId);
    }
    public void setOnDeleteClickLister(FoodAdapter.OnDeleteClickLister lister){
        onDeleteClickLister = lister;
    }
    public static class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView imageView; // 음식 이미지
        private TextView foodName; // 음식 이름
        private TextView foodPrice; // 음식 가격
        private ImageButton cancelButton;

        public ViewHolder(@NonNull View itemView){
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            foodName = itemView.findViewById(R.id.foodName);
            foodPrice = itemView.findViewById(R.id.foodPrice);
            cancelButton = itemView.findViewById(R.id.cancelButton);
        }
    }
    public CafeGameAdapter(ArrayList<CafeGameItem> DataSet){
        this.cafeGameItems = DataSet;
    }
    public void setData(ArrayList<CafeGameItem> DataSet){
        this.cafeGameItems = DataSet;
    }
    @NonNull
    @Override
    public CafeGameAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.food_item, parent, false);
        CafeGameAdapter.ViewHolder viewHolder = new CafeGameAdapter.ViewHolder(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CafeGameAdapter.ViewHolder holder, int position){
        CafeGameItem item = cafeGameItems.get(position);

        holder.foodName.setText(item.getGame_name());

        holder.foodPrice.setVisibility(View.GONE);

        if(item.getImage_url() != null && !item.getImage_url().equals("null") && !item.getImage_url().equals("")){
            Glide.with(holder.itemView.getContext()).load("http://3.38.213.196" + item.getImage_url()).into(holder.imageView);
        } else {
            holder.imageView.setImageResource(R.drawable.img2);
        }

        holder.cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onDeleteClickLister != null){
                    int currentAdapterPosition = holder.getBindingAdapterPosition();
                    if(currentAdapterPosition != RecyclerView.NO_POSITION){
                        onDeleteClickLister.onDeleteClick(currentAdapterPosition, item.getCafe_game_seq());
                    }
                }
            }
        });
    }
    @Override
    public int getItemCount(){
        if(cafeGameItems == null){
            return 0;
        }
        return cafeGameItems.size();
    }
}
