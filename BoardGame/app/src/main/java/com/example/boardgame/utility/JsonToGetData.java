package com.example.boardgame.utility;

import com.example.boardgame.item.CafeReviewItem;
import com.example.boardgame.item.GameReviewItem;
import com.example.boardgame.item.GetGameItem;
import com.example.boardgame.item.MeetingBoardDetailItem;
import com.example.boardgame.item.GetCafeItem;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonToGetData {

    // 카페 정보 가져오기
    public GetCafeItem jsonGetToGetCafe(String json){
        try{
            JSONObject jsonObject = new JSONObject(json);

            GetCafeItem getCafeItem = new GetCafeItem();

            getCafeItem.setCafe_seq(Integer.parseInt(jsonObject.getString("cafe_seq")));
            getCafeItem.setCafe_name(jsonObject.getString("cafe_name"));
            getCafeItem.setCafe_content(jsonObject.getString("cafe_content"));
            getCafeItem.setCafe_create_date(jsonObject.getString("cafe_create_date"));
            getCafeItem.setCafe_lat(jsonObject.getString("cafe_lat"));
            getCafeItem.setCafe_lnt(jsonObject.getString("cafe_lnt"));
            getCafeItem.setCafe_address(jsonObject.getString("cafe_address"));
            getCafeItem.setTo_seqs(jsonObject.has("to_seqs") && !jsonObject.isNull("to_seqs") ? (jsonObject.getString("to_seqs")) : "0");
            getCafeItem.setImage_urls(jsonObject.getString("image_urls"));
            getCafeItem.setAverage_review_grade(jsonObject.has("average_review_grade") && !jsonObject.isNull("average_review_grade") ? Float.parseFloat(jsonObject.getString("average_review_grade")) : 0.0f);

            return getCafeItem;
        } catch (JSONException e){
            return null;
        }
    }
    // 게임 리뷰 정보 가져오기
    public CafeReviewItem jsonGetToGetCafeReview(String json){
        try{
            JSONObject jsonObject = new JSONObject(json);

            CafeReviewItem cafeReviewItem = new CafeReviewItem();

            cafeReviewItem.setReview_seq(Integer.parseInt(jsonObject.getString("review_seq")));
            cafeReviewItem.setUser_seq(Integer.parseInt(jsonObject.getString("user_seq")));
            cafeReviewItem.setCafe_seq(Integer.parseInt(jsonObject.getString("cafe_seq")));
            cafeReviewItem.setReview_content(jsonObject.getString("review_content"));
            cafeReviewItem.setReview_grade(jsonObject.has("review_grade") && !jsonObject.isNull("review_grade") ? Float.parseFloat(jsonObject.getString("review_grade")) : 0.0f);
            cafeReviewItem.setReview_type(Integer.parseInt(jsonObject.getString("review_type")));
            cafeReviewItem.setReview_create_date(jsonObject.getString("review_create_date"));
            cafeReviewItem.setTo_seqs(jsonObject.getString("to_seqs"));
            cafeReviewItem.setImage_urls(jsonObject.getString("image_urls"));

            return cafeReviewItem;
        } catch (JSONException e){
            return null;
        }
    }


    // 게임 리뷰 정보 가져오기
    public GameReviewItem jsonGetToGetGameReview(String json){
        try{
            JSONObject jsonObject = new JSONObject(json);

            GameReviewItem gameReviewItem = new GameReviewItem();

            gameReviewItem.setReview_seq(Integer.parseInt(jsonObject.getString("review_seq")));
            gameReviewItem.setUser_seq(Integer.parseInt(jsonObject.getString("user_seq")));
            gameReviewItem.setGame_seq(Integer.parseInt(jsonObject.getString("game_seq")));
            gameReviewItem.setReview_content(jsonObject.getString("review_content"));
            gameReviewItem.setReview_grade(jsonObject.has("review_grade") && !jsonObject.isNull("review_grade") ? Float.parseFloat(jsonObject.getString("review_grade")) : 0.0f);
            gameReviewItem.setReview_type(Integer.parseInt(jsonObject.getString("review_type")));
            gameReviewItem.setReview_create_date(jsonObject.getString("review_create_date"));
            gameReviewItem.setTo_seqs(jsonObject.getString("to_seqs"));
            gameReviewItem.setImage_urls(jsonObject.getString("image_urls"));

            return gameReviewItem;
        } catch (JSONException e){
            return null;
        }
    }

    public GetGameItem jsonGetToGetGame(String json){
        try{

            JSONObject jsonObject = new JSONObject(json);

            GetGameItem getGameItem = new GetGameItem();
            getGameItem.setGame_seq(Integer.parseInt(jsonObject.getString("game_seq")));
            getGameItem.setGame_name(jsonObject.getString("game_name"));
            getGameItem.setGame_summary(jsonObject.getString("game_summary"));
            getGameItem.setGame_min(Integer.parseInt(jsonObject.getString("game_min")));
            getGameItem.setGame_max(Integer.parseInt(jsonObject.getString("game_max")));
            getGameItem.setGame_detail(jsonObject.getString("game_detail"));
            getGameItem.setGame_create_data(jsonObject.getString("game_create_data"));
            getGameItem.setImage_urls(jsonObject.getString("image_urls"));
            getGameItem.setTo_seqs(jsonObject.getString("to_seqs"));
            getGameItem.setAverage_review_grade(jsonObject.has("average_review_grade") && !jsonObject.isNull("average_review_grade") ? Float.parseFloat(jsonObject.getString("average_review_grade")) : 0.0f);
            return getGameItem;

        }catch (JSONException e){
            return null;
        }
    }
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
            meetingBoardDetailItem.setTo_seqs(jsonObject.getString("to_seqs"));

            return meetingBoardDetailItem;

        } catch (JSONException e){
            e.printStackTrace();
            return null;
        }
    }
}
