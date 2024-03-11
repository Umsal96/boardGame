package com.example.boardgame.utility;

import com.google.gson.annotations.SerializedName;

public class ResponseModel {
    @SerializedName("requestId")
    private String requestId;

    @SerializedName("requestTime")
    private String requestTime;

    @SerializedName("statusCode")
    private String statusCode;

    public String getStatusCode() {
        return statusCode;
    }

    @SerializedName("statusName")
    private String statusName;

    @SerializedName("authCode")
    private String authCode;

    public String getAuthCode() {
        return authCode;
    }
}
