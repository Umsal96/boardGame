package com.example.boardgame.utility;

import com.example.boardgame.item.MeetingBoardDetailItem;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonToGetData {

    public MeetingBoardDetailItem jsonGetToGetBoard(String json){
        try{
            // JSON 문자열을 JSONObject 로 파싱
            JSONObject jsonObject = new JSONObject(json);

            MeetingBoardDetailItem meetingBoardDetailItem = new MeetingBoardDetailItem();
            meetingBoardDetailItem.setBoard_seq(Integer.parseInt(jsonObject.getString("board_seq")));
            meetingBoardDetailItem.setUser_seq(Integer.parseInt(jsonObject.getString("user_seq")));
            meetingBoardDetailItem.setMeeting_seq(Integer.parseInt(jsonObject.getString("meeting_seq")));
            meetingBoardDetailItem.setBoard_title(jsonObject.getString("board_title"));
            meetingBoardDetailItem.setBoard_content(jsonObject.getString("board_content"));
            meetingBoardDetailItem.setBoard_type(jsonObject.getString("board_type"));
            meetingBoardDetailItem.setBoard_create_date(jsonObject.getString("board_create_date"));
            meetingBoardDetailItem.setUser_url(jsonObject.getString("user_url"));
            meetingBoardDetailItem.setUser_nickname(jsonObject.getString("user_nickname"));
            meetingBoardDetailItem.setImage_urls(jsonObject.getString("image_urls"));

            return meetingBoardDetailItem;

        } catch (JSONException e){
            e.printStackTrace();
            return null;
        }
    }
}
