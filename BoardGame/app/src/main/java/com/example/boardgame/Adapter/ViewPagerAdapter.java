package com.example.boardgame.Adapter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.boardgame.R;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ViewPagerAdapter extends RecyclerView.Adapter<ViewPagerAdapter.ViewHolder>{
    private ArrayList<Uri> uriImageDataList;
    private ArrayList<String> stringImageDataList;

    int boardId;

    private OnCancelClickListener onCancelClickListener;
    public interface OnCancelClickListener{
        void onCancelClick(int position);
    }

    public void setOnCancelClickListener(OnCancelClickListener listener){
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

    public ViewPagerAdapter(ArrayList<String> stringImageDataList, ArrayList<Uri> uriImageDataList, int boardId){
        this.stringImageDataList = stringImageDataList;
        this.uriImageDataList = uriImageDataList;
        this.boardId = boardId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.image_viewpager_item, parent, false);

        ViewPagerAdapter.ViewHolder viewHolder = new ViewPagerAdapter.ViewHolder(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull ViewPagerAdapter.ViewHolder holder, int position){
        int viewType = getItemViewType(position);

        if (viewType == 0) {
            // 문자열 이미지 데이터가 있는 경우
            String sUri = stringImageDataList.get(position);
            String imageUrl = "http://3.38.213.196" + sUri;

            // Glide를 사용하여 이미지를 로드하고 ImageView에 표시
            Glide.with(holder.itemView.getContext())
                    .load(imageUrl)
                    .into(holder.image);
        } else {
            // Uri 이미지 데이터가 있는 경우
            Uri uri = uriImageDataList.get(position - stringImageDataList.size());
            holder.image.setImageURI(uri);
        }

        // 이미지 삭제 버튼 클릭 이벤트 처리
        holder.cancel.setOnClickListener(new View.OnClickListener() {
            int currentAdapterPosition = holder.getBindingAdapterPosition();
            @Override
            public void onClick(View v) {
                if(onCancelClickListener != null){
                    if(currentAdapterPosition != RecyclerView.NO_POSITION){
                        onCancelClickListener.onCancelClick(currentAdapterPosition);
                    }
                }

                if (viewType == 0) {
                    // 문자열 이미지 데이터를 삭제하는 작업

                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    builder.setMessage("이미지를 삭제 하시겠습니까?")
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String removeUri = stringImageDataList.get(currentAdapterPosition); // 삭제 해야하는 uri 가져옴
                                    String[] parts = removeUri.split("_");
                                    String result = parts[1].replaceAll("image", "").replaceAll(".png", "");

                                    int reResult = Integer.parseInt(result) + 1;

                                    System.out.println("삭제해야할 이미지 순서 : " + (reResult));

                                    stringImageDataList.remove(currentAdapterPosition);

                                    removeImg(boardId, reResult);
                                    notifyDataSetChanged();
                                }
                            })
                            .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    AlertDialog dialog = builder.create();
                    dialog.show();


                } else {
                    // Uri 이미지 데이터를 삭제하는 작업
                    uriImageDataList.remove(currentAdapterPosition - stringImageDataList.size());
                    notifyDataSetChanged();
                }
                notifyDataSetChanged(); // RecyclerView 갱신
            }
        });
    }

    @Override
    public int getItemCount(){
        if(uriImageDataList == null && stringImageDataList == null){
            return 0;
        }else{
            return uriImageDataList.size() + stringImageDataList.size();
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (stringImageDataList != null && position < stringImageDataList.size()) {
            return 0; // 문자열 이미지 데이터 유형
        } else {
            return 1; // Uri 이미지 데이터 유형
        }
    }

    private void removeImg(int boardId, int reResult){ // 게시글 고유 아이디, 삭제해야하는 이미지 순서
        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://3.38.213.196/image/deleteImg.php").newBuilder();
        urlBuilder.addQueryParameter("boardId", String.valueOf(boardId));
        urlBuilder.addQueryParameter("imageOrder", String.valueOf(reResult));
        String url = urlBuilder.build().toString();

        Request request = new Request.Builder()
                .url(url)
                .build();

        OkHttpClient client = new OkHttpClient();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.isSuccessful()){
                    String responseDate = response.body().string();
                    System.out.println(responseDate);
                }
            }
        });
    }
}
