package com.example.boardgame.Adapter;

import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.boardgame.R;
import com.example.boardgame.item.CategoryItem;

import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
    private ArrayList<CategoryItem> categoryItem;
    private OnButtonItemClickListener onButtonItemClickListener;
    private Context context;
    public interface OnButtonItemClickListener{
        void onButtonItemClickListener (String name);
    }

    public void setOnButtonItemClickListener(OnButtonItemClickListener listener){
        onButtonItemClickListener = listener;
    }
    public static class ViewHolder extends RecyclerView.ViewHolder{
        private Button categoryButton;

        public ViewHolder(@NonNull View itemView){
            super(itemView);
            categoryButton = itemView.findViewById(R.id.categoryButton);
        }
    }

    public CategoryAdapter(Context context, ArrayList<CategoryItem> DataSet) {
        this.categoryItem = DataSet;
        this.context = context;
    }

    @NonNull
    @Override
    public CategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int ViewType){
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.category_item, parent, false);
        CategoryAdapter.ViewHolder viewHolder = new CategoryAdapter.ViewHolder(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryAdapter.ViewHolder holder, int position){
        CategoryItem item = categoryItem.get(position);

        holder.categoryButton.setText(item.getName());

        if (item.isSelected()) {
            holder.categoryButton.setBackgroundColor(ContextCompat.getColor(context, R.color.skyBlue));
        } else {
            holder.categoryButton.setBackgroundColor(ContextCompat.getColor(context, R.color.black));
        }
        holder.categoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onButtonItemClickListener != null){

                    int currentAdapterPosition = holder.getBindingAdapterPosition();
                    if(currentAdapterPosition != RecyclerView.NO_POSITION){
                        onButtonItemClickListener.onButtonItemClickListener(item.getName());
                    }
                }

                for (CategoryItem i : categoryItem){
                    // 모든 아이템 선택 상태 초기화
                    i.setSelected(false);
                }

                // 현재 아이템 선택 상태 설정
                item.setSelected(true);

                // 어댑터 업데이트
                notifyDataSetChanged();

            }
        });
    }
    @Override
    public int getItemCount(){
        if(categoryItem == null){
            return 0;
        }
        return categoryItem.size();
    }
}
