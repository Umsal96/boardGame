package com.example.boardgame.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.boardgame.Adapter.InputGameListAdapter;
import com.example.boardgame.R;
import com.example.boardgame.item.CafeGameListItem;
import com.example.boardgame.utility.JsonToData;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class InputGameListDialog extends Dialog {
    private InputGameListAdapter inputGameListAdapter;
    private RecyclerView gameRecyclerView;
    private ArrayList<CafeGameListItem> cgli = new ArrayList<>();
    private Activity activity;
    ArrayList<Integer> gameSeq;
    int cafeId;
    private Button inputGameButton11;
    ArrayList<CafeGameListItem> cgll = new ArrayList<>();
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.input_game_list_dialog);
        inputGameButton11 = findViewById(R.id.inputGameButton11);
        gameRecyclerView = findViewById(R.id.gameRecyclerView);

        inputGameButton11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameSeq = inputGameListAdapter.getSeqList();
                inputGameList(gameSeq, cafeId);
//                inputGameListAdapter.uncheckAllItems();
//                inputGameListAdapter.notifyDataSetChanged();
            }
        });


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        gameRecyclerView.setLayoutManager(linearLayoutManager);
        inputGameListAdapter = new InputGameListAdapter(cgli);
        gameRecyclerView.setAdapter(inputGameListAdapter);

        getGameSeqList(cafeId);
    }
    public InputGameListDialog(@NonNull Context context, Activity activity, int cafeId){
        super(context);
        this.activity = activity;
        this.cafeId = cafeId;
    }

    private void inputGameList(ArrayList<Integer> gameSeq, int cafeId){
        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://3.38.213.196/cafeGame/inputCafeGame.php").newBuilder();
        String url = urlBuilder.build().toString();

        RequestBody requestBody = new FormBody.Builder()
                .add("gameSeq", arrayToString(gameSeq))
                .add("cafeId", String.valueOf(cafeId))
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        OkHttpClient client = new OkHttpClient();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.isSuccessful()){
                    String responseData = response.body().string();
                    System.out.println(responseData);
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            inputGameListAdapter.uncheckAllItems();
                            inputGameListAdapter.notifyDataSetChanged();
                            dismiss();
                        }
                    });

                }
            }
        });
    }
    @Override
    public void dismiss(){
        super.dismiss();
        System.out.println("dissmiss");
        inputGameListAdapter.uncheckAllItems();
        inputGameListAdapter.notifyDataSetChanged();
    }
    private String arrayToString(ArrayList<Integer> array) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < array.size(); i++) {
            result.append(array.get(i));
            if (i < array.size() - 1) {
                result.append(",");
            }
        }
        return result.toString();
    }

    private void getGameSeqList(int cafeId){
        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://3.38.213.196/cafeGame/getGameSeqList.php").newBuilder();
        urlBuilder.addQueryParameter("cafeId", String.valueOf(cafeId)); // url 쿼리에 id 라는 메개변수 추가
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
                    String responseData = response.body().string();
                    System.out.println("등록된 게임 아이디 가져오기");
                    System.out.println(responseData);
                    gameSeq = new ArrayList<>();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try{
                                JSONArray jsonArray = new JSONArray(responseData);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    int gameSeqI = jsonArray.getInt(i);
                                    gameSeq.add(gameSeqI);
                                }
                            }catch(JSONException e){
                                e.printStackTrace();
                            }

                            getGameList();
                        }
                    });
                }
            }
        });
    }
    private void getGameList(){
        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://3.38.213.196/game/getGameList.php").newBuilder();
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
                    String responseData = response.body().string();
                    System.out.println(responseData);
                    JsonToData jt = new JsonToData();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            cgli.clear();
                            cgli.addAll(jt.jsonToCafeGameList(responseData));

                            cgll.clear();
                            // cgi - 이미 저장된 리스트 cgli - 모든 게임 리스트
                            for (int i = 0; i < cgli.size(); i++) {
                                boolean found = false;
                                for (int j = 0; j < gameSeq.size(); j++) {
                                    if(gameSeq.get(j) == cgli.get(i).getGame_seq()){
                                        found = true;
                                        break;
                                    }
                                }
                                if(!found){
                                    cgll.add(cgli.get(i));
                                }
                            }
                            inputGameListAdapter.setData(cgll);
                            inputGameListAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });
    }
}
