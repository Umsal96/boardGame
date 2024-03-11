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
import com.example.boardgame.getCafe;
import com.example.boardgame.item.CafeListItem;

import java.util.ArrayList;

public class CafeListAdapter extends RecyclerView.Adapter<CafeListAdapter.ViewHolder> {

    private ArrayList<CafeListItem> cafeListItems;

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private TextView cafeName, cafeSummary, cafeAddress, cafeArg;
        private ImageView cafeImage;
        private RatingBar cafeRatingBar;

        public ViewHolder(@NonNull View itemView){
            super(itemView);
            cafeName = itemView.findViewById(R.id.cafeName);
            cafeSummary = itemView.findViewById(R.id.cafeSummary);
            cafeAddress = itemView.findViewById(R.id.cafeAddress);
            cafeArg = itemView.findViewById(R.id.cafeArg);
            cafeImage = itemView.findViewById(R.id.cafeImage);
            cafeRatingBar = itemView.findViewById(R.id.cafeRatingBar);
        }
    }

    public CafeListAdapter(ArrayList<CafeListItem> DataSet) {
        this.cafeListItems = DataSet;
    }

    public void setData(ArrayList<CafeListItem> DataSet){
        this.cafeListItems = DataSet;
    }

    @NonNull
    @Override
    public CafeListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cafe_item, parent, false);
        CafeListAdapter.ViewHolder viewHolder = new CafeListAdapter.ViewHolder(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = viewHolder.getLayoutPosition();
                int cafeId = cafeListItems.get(position).getCafe_seq();
                Intent intent = new Intent(v.getContext(), getCafe.class);
                intent.putExtra("cafeId", cafeId);
                v.getContext().startActivity(intent);
            }
        });
        return viewHolder;
    }
    @Override
    public void onBindViewHolder(@NonNull CafeListAdapter.ViewHolder holder, int position){
        CafeListItem item = cafeListItems.get(position);
        holder.cafeName.setText(item.getCafe_name());
        holder.cafeSummary.setText(item.getCafe_content());
        holder.cafeArg.setText(String.valueOf((float) item.getAverage_review_grade()));
        holder.cafeRatingBar.setRating(item.getAverage_review_grade());
        holder.cafeAddress.setText(item.getCafe_address());

        if (item.getImage_url() != null && !item.getImage_url().equals("null") && !item.getImage_url().equals("")) {
            Glide.with(holder.itemView.getContext()).load("http://3.38.213.196" + item.getImage_url()).into(holder.cafeImage);
        } else {
            holder.cafeImage.setImageResource(R.drawable.img);
        }
    }

    @Override
    public int getItemCount(){
        if(cafeListItems == null){
            return 0;
        }
        return cafeListItems.size();
    }
}
