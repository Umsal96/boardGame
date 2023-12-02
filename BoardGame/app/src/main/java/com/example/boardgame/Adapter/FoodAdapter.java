package com.example.boardgame.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.boardgame.R;
import com.example.boardgame.item.FoodItem;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.ViewHolder> {
    private ArrayList<FoodItem> foodItems;
    private OnDeleteClickLister onDeleteClickLister;
    // 아이템의 x 버튼을 눌렀을때
    public interface OnDeleteClickLister{
        void onDeleteClick(int position, int foodId);
    }
    public void setOnDeleteClickLister(OnDeleteClickLister lister){
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
    public FoodAdapter(ArrayList<FoodItem> DataSet){
        this.foodItems = DataSet;
    }
    public void setData(ArrayList<FoodItem> DataSet){
        this.foodItems = DataSet;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.food_item, parent, false);
        FoodAdapter.ViewHolder viewHolder = new FoodAdapter.ViewHolder(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull FoodAdapter.ViewHolder holder, int position){
        FoodItem item = foodItems.get(position);

        holder.foodName.setText(item.getFood_name());
        // 숫자 포맷 지정
        DecimalFormat decimalFormat = new DecimalFormat("#,###");

        String price = decimalFormat.format(item.getFood_price());

        holder.foodPrice.setText(price);

        if(item.getImgUrl() != null && !item.getImgUrl().equals("null") && !item.getImgUrl().equals("")){
            Glide.with(holder.itemView.getContext()).load("http://3.38.213.196" + item.getImgUrl()).into(holder.imageView);
        } else {
            holder.imageView.setImageResource(R.drawable.img2);
        }

        holder.cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onDeleteClickLister != null){
                    int currentAdapterPosition = holder.getBindingAdapterPosition();
                    if(currentAdapterPosition != RecyclerView.NO_POSITION){
                        onDeleteClickLister.onDeleteClick(currentAdapterPosition, item.getFood_seq());
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount(){
        if(foodItems == null){
            return 0;
        }
        return foodItems.size();
    }
}
