package com.example.boardgame.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.boardgame.R;
import com.example.boardgame.item.CafeGameListItem;

import java.util.ArrayList;

public class InputGameListAdapter extends RecyclerView.Adapter<InputGameListAdapter.ViewHolder> {
    private ArrayList<CafeGameListItem> cafeGameListItems;

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView imageView;
        private TextView gameName;
        private CheckBox gameCheckBox;
        public ViewHolder(@NonNull View itemView){
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            gameName = itemView.findViewById(R.id.gameName);
            gameCheckBox = itemView.findViewById(R.id.gameCheckBox);
        }
    }
    public InputGameListAdapter(ArrayList<CafeGameListItem> DataSet){
        this.cafeGameListItems = DataSet;
    }
    public void setData(ArrayList<CafeGameListItem> DataSet){
        this.cafeGameListItems = DataSet;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.input_game_cafe_item, parent, false);
        InputGameListAdapter.ViewHolder viewHolder = new InputGameListAdapter.ViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return viewHolder;
    }
    @Override
    public void onBindViewHolder(@NonNull InputGameListAdapter.ViewHolder holder, int position){
        CafeGameListItem item = cafeGameListItems.get(position);
        holder.gameName.setText(item.getGame_name());

        item.setChecked(false);
        System.out.println(item.isChecked());

        holder.gameCheckBox.setChecked(false);
        holder.gameCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                item.setChecked(isChecked);

                if(isChecked){
                    System.out.println("체크되었습니다.");
                    item.setChecked(true);
                } else{
                    System.out.println("체그 되지 않았습니다.");
                    item.setChecked(false);
                }
            }
        });

        if(item.getImage_url() != null && !item.getImage_url().equals("null") && !item.getImage_url().equals("")){
            Glide.with(holder.itemView.getContext()).load("http://3.38.213.196" + item.getImage_url()).into(holder.imageView);
        } else {
            holder.imageView.setImageResource(R.drawable.img2);
        }
    }
    // 체크된 아이템을 해제하는 메서드
    public void uncheckAllItems() {
        for (CafeGameListItem item : cafeGameListItems) {
            item.setChecked(false);
        }
        System.out.println("체크 해제 작동");
    }
    public ArrayList<Integer> getSeqList(){
        ArrayList<Integer> gameSeq = new ArrayList<>();
        for (CafeGameListItem item : cafeGameListItems){
            if(item.isChecked()){
                gameSeq.add(item.getGame_seq());
            }
        }
        return gameSeq;
    }
    @Override
    public int getItemCount(){
        if(cafeGameListItems == null){
            return 0;
        }
        return cafeGameListItems.size();
    }
}
