package com.example.boardgame.utility;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class NetworkManager {

    private OkHttpClient client;

    public NetworkManager(){
        client = new OkHttpClient();
    }

    public String fetchDataFromServer(String url){
        Request request = new Request.Builder()
                .url(url)
                .build();

        try{
            Response response = client.newCall(request).execute();
            if(response.isSuccessful()){
                return response.body().string();
            }
        } catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }
}
