package com.example.boardgame.utility;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {
    @GET("/userJoin/sendSMS.php")
    Call<ResponseModel> sendSMS(@Query("phone") String phone);
}
