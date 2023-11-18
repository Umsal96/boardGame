package com.example.boardgame.utility;

import android.util.Pair;

import com.example.boardgame.item.CommentReplyItem;
import com.example.boardgame.item.GameItem;
import com.example.boardgame.item.GameReviewItem;
import com.example.boardgame.item.MeetingBoardItem;
import com.example.boardgame.item.MeetingItem;
import com.example.boardgame.item.ScheduleItem;
import com.example.boardgame.item.ScheduleMemberItem;
import com.example.boardgame.item.UserItem;
import com.example.boardgame.item.UserNItem;
import com.example.boardgame.item.WaitingItem;
import com.example.boardgame.vo.meetingVO;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class JsonToData {
    int num; // jsonToMeeting에서 사용함
    // 일정에 참가한 유저의 닉네임 과 프로필을 을 가저온 json 을 ArrayList에 넣기위한 메소드

    // 보드게임 리뷰 리스트
    public ArrayList<GameReviewItem> jsonToGameReview(String json){
        ArrayList<GameReviewItem> gri = new ArrayList<>();
        try{
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                GameReviewItem gi = new GameReviewItem();
                gi.setReview_seq(Integer.parseInt(jsonObject.getString("review_seq")));
                gi.setUser_seq(Integer.parseInt(jsonObject.getString("user_seq")));
                gi.setGame_seq(Integer.parseInt(jsonObject.getString("game_seq")));
                gi.setReview_content(jsonObject.getString("review_content"));
                gi.setReview_grade(jsonObject.has("review_grade") && !jsonObject.isNull("review_grade") ? Float.parseFloat(jsonObject.getString("review_grade")) : 0.0f);
                gi.setReview_type(Integer.parseInt(jsonObject.getString("review_type")));
                gi.setReview_create_date(jsonObject.getString("review_create_date"));
                gi.setUser_url(jsonObject.getString("user_url"));
                gi.setUser_nickname(jsonObject.getString("user_nickname"));
                gi.setTo_seqs(jsonObject.getString("to_seqs"));
                gi.setImage_urls(jsonObject.getString("image_urls"));

                gri.add(gi);
            }
        } catch (JSONException e){
            e.printStackTrace();
            return null;
        }
        return gri;
    }

    // 보드게임 리스트를 가져옴
    public ArrayList<GameItem> jsonToGameList(String json){
        ArrayList<GameItem> gii = new ArrayList<>();
        try{
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                GameItem gi = new GameItem();
                gi.setGame_seq(Integer.parseInt(jsonObject.getString("game_seq")));
                gi.setGame_name(jsonObject.getString("game_name"));
                gi.setGame_summary(jsonObject.getString("game_summary"));
                gi.setGame_min(Integer.parseInt(jsonObject.getString("game_min")));
                gi.setGame_max(Integer.parseInt(jsonObject.getString("game_max")));
                gi.setGame_detail(jsonObject.getString("game_detail"));
                gi.setGame_create_data(jsonObject.getString("game_create_data"));
                gi.setImage_url(jsonObject.getString("image_url"));
                gi.setAverage_review_grade(jsonObject.has("average_review_grade") && !jsonObject.isNull("average_review_grade") ? Float.parseFloat(jsonObject.getString("average_review_grade")) : 0.0f);
                gii.add(gi);
            }
        }catch (JSONException e){
            e.printStackTrace();
            return null;
        }
        return gii;
    }

    // 게시글의 댓글 리스트를 가져옴
    public ArrayList<CommentReplyItem> jsonToBoardComment(String json){
        ArrayList<CommentReplyItem> cri = new ArrayList<>();
        try{
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                CommentReplyItem ci = new CommentReplyItem();

                ci.setReply_seq(Integer.parseInt(jsonObject.getString("reply_seq")));
                ci.setBoard_seq(Integer.parseInt(jsonObject.getString("board_seq")));
                ci.setUser_seq(Integer.parseInt(jsonObject.getString("user_seq")));
                ci.setUser_nick(jsonObject.getString("user_nickname"));
                ci.setUser_url(jsonObject.getString("user_url"));
                ci.setReply_content(jsonObject.getString("reply_content"));
                ci.setReply_ref(Integer.parseInt(jsonObject.getString("reply_ref")));
                ci.setReply_order(Integer.parseInt(jsonObject.getString("reply_order")));
                ci.setReply_del(Integer.parseInt(jsonObject.getString("reply_del")));
                ci.setReply_create_date(jsonObject.getString("reply_create_date"));

                cri.add(ci);
            }

        } catch (JSONException e){
            e.printStackTrace();
            return null;
        }

        return cri;
    }

    // 모임의 게시글 리스트를 가져오는 코드
    public ArrayList<MeetingBoardItem> jsonToMeetingBoard(String json){
        ArrayList<MeetingBoardItem> mbi = new ArrayList<>();
        try{
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                MeetingBoardItem mi = new MeetingBoardItem();

                mi.setUserId(Integer.parseInt(jsonObject.getString("user_seq")));
                mi.setMeetingId(Integer.parseInt(jsonObject.getString("meeting_seq")));
                mi.setBoardId(Integer.parseInt(jsonObject.getString("board_seq")));
                mi.setBoardTitle(jsonObject.getString("board_title"));
                mi.setBoardContent(jsonObject.getString("board_content"));
                mi.setBoardType(jsonObject.getString("board_type"));
                mi.setCreateDate(jsonObject.getString("board_create_date"));
                mi.setUserNick(jsonObject.getString("user_nickname"));
                mi.setUserUrl(jsonObject.getString("user_url"));
                mi.setImgUrl(jsonObject.getString("image_url"));

                mbi.add(mi);
            }
        } catch (JSONException e){
            e.printStackTrace();
            return null;
        }

        return mbi;
    }

    public ArrayList<WaitingItem> jsonToWaitingUserList(String json){
        ArrayList<WaitingItem> wai = new ArrayList<>();
        try{
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                WaitingItem wi = new WaitingItem();

                wi.setUserSeq(Integer.parseInt(jsonObject.getString("user_seq")));
                wi.setMeetingSeq(Integer.parseInt(jsonObject.getString("meeting_seq")));
                wi.setUserNick(jsonObject.getString("user_nickname"));
                wi.setUserUrl(jsonObject.getString("user_url"));

                wai.add(wi);

            }
        } catch (JSONException e){
            e.printStackTrace();
            return null;
        }

        return wai;
    }
    public ScheduleItem jsonToGetSchedule(String json){
        try{
            // JSON 문자열을 JSONObject 로 파싱
            JSONObject jsonObject = new JSONObject(json);

            // JSON 데이터에서 필요한 정보를 추출하여 ScheduleItem 객체에 설정
            ScheduleItem scheduleItem = new ScheduleItem();
            scheduleItem.setScheduleSeq(Integer.parseInt(jsonObject.getString("schedule_seq")));
            scheduleItem.setUserSeq(Integer.parseInt(jsonObject.getString("user_seq")));
            scheduleItem.setMeetingSeq(Integer.parseInt(jsonObject.getString("meeting_seq")));
            scheduleItem.setScheduleTitle(jsonObject.getString("schedule_title"));
            scheduleItem.setSchedule_date(jsonObject.getString("schedule_date"));
            scheduleItem.setSchedule_time(jsonObject.getString("schedule_time"));
            scheduleItem.setSchedule_member_max(Integer.parseInt(jsonObject.getString("schedule_member_max")));
            scheduleItem.setSchedule_member_current(Integer.parseInt(jsonObject.getString("schedule_member_current")));
            scheduleItem.setSchedule_place_name(jsonObject.getString("schedule_place_name"));
            scheduleItem.setSchedule_place_address(jsonObject.getString("schedule_place_address"));
            scheduleItem.setSchedule_lat(jsonObject.getString("schedule_lat"));
            scheduleItem.setSchedule_lnt(jsonObject.getString("schedule_lnt"));
            scheduleItem.setSchedule_create_date(jsonObject.getString("schedule_create_date"));
            return scheduleItem;
        } catch (JSONException e){
            e.printStackTrace();
            return null;
        }
    }
    public ArrayList<UserNItem> jsonToScheduleMemberUserList(String json){
        ArrayList<UserNItem> uni = new ArrayList<>();

        try{
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                UserNItem us = new UserNItem();

                String userUrl = jsonObject.getString("user_url");
                String userNick = jsonObject.getString("user_nickname");

                us.setUserUrl(userUrl);
                us.setUserNick(userNick);

                uni.add(us);

            }
        } catch (JSONException e){
            e.printStackTrace();
            return null;
        }

        return uni;
    }
    public ArrayList<ScheduleMemberItem> jsonToScheduleMemberList(String json){
        ArrayList<ScheduleMemberItem> SMi = new ArrayList<>();

        try{
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length() ; i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                ScheduleMemberItem smi = new ScheduleMemberItem();

                String s_member_seq = jsonObject.getString("s_member_seq");
                String schedule_seq = jsonObject.getString("schedule_seq");
                String user_seq = jsonObject.getString("user_seq");
                String schedule_date = jsonObject.getString("schedule_date");
                String schedule_time = jsonObject.getString("schedule_time");

                smi.setS_member_seq(Integer.parseInt(s_member_seq));
                smi.setSchedule_seq(Integer.parseInt(schedule_seq));
                smi.setUser_seq(Integer.parseInt(user_seq));
                smi.setSchedule_date(schedule_date);
                smi.setSchedule_time(schedule_time);

                SMi.add(smi);
            }
        }catch(JSONException e){
            e.printStackTrace();
            return null;
        }

        return SMi;
    }
    public ArrayList<UserItem> jsonToUserList(String json){

        ArrayList<UserItem> Ui = new ArrayList<>();

        try{
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                UserItem ui = new UserItem();

                String user_seq = jsonObject.getString("user_seq");
                String member_leader = jsonObject.getString("member_leader");
                String user_nickname = jsonObject.getString("user_nickname");
                String user_url = jsonObject.getString("user_url");

                ui.setUserSeq(Integer.parseInt(user_seq));
                ui.setLeader(Integer.parseInt(member_leader));
                ui.setUserNick(user_nickname);
                ui.setUserUrl(user_url);

                Ui.add(ui);
            }

        } catch (JSONException e){
            e.printStackTrace();
            return null;
        }

        return Ui;
    }
    public ArrayList<ScheduleItem> jsonSchedule(String json){

        ArrayList<ScheduleItem> Si = new ArrayList<>();

        try{
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                ScheduleItem si = new ScheduleItem();

                // 필요한 작업 수행
                String scheduleSeq = jsonObject.getString("schedule_seq");
                String userSeq = jsonObject.getString("user_seq");
                String meetingSeq = jsonObject.getString("meeting_seq");
                String scheduleTitle = jsonObject.getString("schedule_title");
                String scheduleDate = jsonObject.getString("schedule_date");
                String scheduleTime = jsonObject.getString("schedule_time");
                String scheduleMemberMax = jsonObject.getString("schedule_member_max");
                String scheduleMemberCurrent = jsonObject.getString("schedule_member_current");
                String schedulePlaceName = jsonObject.getString("schedule_place_name");
                String schedulePlaceAddress = jsonObject.getString("schedule_place_address");
                String scheduleLat = jsonObject.getString("schedule_lat");
                String scheduleLnt = jsonObject.getString("schedule_lnt");
                String scheduleCreateDate = jsonObject.getString("schedule_create_date");

                si.setScheduleSeq(Integer.parseInt(scheduleSeq));
                si.setUserSeq(Integer.parseInt(userSeq));
                si.setMeetingSeq(Integer.parseInt(meetingSeq));
                si.setScheduleTitle(scheduleTitle);
                si.setSchedule_date(scheduleDate);
                si.setSchedule_time(scheduleTime);
                si.setSchedule_member_max(Integer.parseInt(scheduleMemberMax));
                si.setSchedule_member_current(Integer.parseInt(scheduleMemberCurrent));
                si.setSchedule_place_name(schedulePlaceName);
                si.setSchedule_place_address(schedulePlaceAddress);
                si.setSchedule_lat(scheduleLat);
                si.setSchedule_lnt(scheduleLnt);
                si.getSchedule_create_date(scheduleCreateDate);

                Si.add(si);

            }
        } catch (JSONException e){
            e.printStackTrace();
            return null;
        }

        return Si;
    }

    public meetingVO jsonMeetingGet(String json){

        try{
            // JSON 문자열을 JSONObject 로 파싱
            JSONObject jsonObject = new JSONObject(json);

            // JSON 데이터에서 필요한 정보를 추출하여 meetingVO 객체에 설정
            meetingVO vo = new meetingVO();
            vo.setMeetingSeq(jsonObject.getString("meeting_seq"));
            vo.setUserSeq(jsonObject.getString("user_seq"));
            vo.setMeetingName(jsonObject.getString("meeting_name"));
            vo.setMeetingPlaceName(jsonObject.getString("meeting_place_name"));
            vo.setMeetingContent(jsonObject.getString("meeting_content"));
            vo.setMeetingLat(jsonObject.getString("meeting_lat"));
            vo.setMeetingLnt(jsonObject.getString("meeting_lnt"));
            vo.setMeetingAddress(jsonObject.getString("meeting_address"));
            vo.setMeetingMembers(jsonObject.getString("meeting_members"));
            vo.setMeetingCreateDate(jsonObject.getString("meeting_create_date"));
            vo.setMeetingModifiedDate(jsonObject.getString("meeting_modified_date"));
            vo.setMeetingUrl(jsonObject.getString("meeting_url"));
            vo.setMeetingCurrent(jsonObject.getString("meeting_current"));
            return vo;
        } catch (JSONException e){
            e.printStackTrace();
            return null;
        }
    }

    public Pair<Integer, ArrayList<MeetingItem>> jsonToMeetingList(String Array){

        // meetingItem 객체의 어레이리스트 생성
        ArrayList<MeetingItem> mi = new ArrayList<>();

        if(Array != null){
            try{
                JSONObject jsonResponse = new JSONObject(Array); // 결과를 JSONArray 로 받아옴
                num = jsonResponse.getInt("num");

                JSONArray jsonArray = jsonResponse.getJSONArray("data");
                for (int i = 0; i < jsonArray.length(); i++){ // jsonArray의 길이만큼 반복문을 실행
                    JSONObject jsonObject = jsonArray.getJSONObject(i); // jsonArray 의 i 번째 개열을 jsonObject 에 넣음
                    MeetingItem me = new MeetingItem();
                    // JSON 데이터에서 필요한 정보 추출
                    // jsonObject 중 meeting_seq 데이터를 추출한뒤 변수에 저장
                    // meeting 테이블을 고유 아이디
                    String meetingSeq = jsonObject.getString("meeting_seq");

                    // jsonObject 중 meeting_name 데이터를 추출한뒤 변수에 저장
                    // 모임의 이름
                    String meetingName = jsonObject.getString("meeting_name");

                    // jsonObject 중 meeting_content 데이터를 추출한뒤 변수에 저장
                    // 모임의 내용
                    String meetingContent = jsonObject.getString("meeting_content");

                    // jsonObject 중 meeting_members 데이터를 추출한뒤 변수에 저장
                    // 모임의 최대 인원수
                    String meetingMembers = jsonObject.getString("meeting_members");

                    // jsonObject 중 meeting_create_date 데이터를 추출한뒤 변수에 저장
                    // 모임의 생성일
                    String meetingCreateDate = jsonObject.getString("meeting_create_date");

                    // jsonObject 중 meeting_url 데이터를 추출한뒤 변수에 저장
                    // 모임의 이미지 url
                    String meetingUrl = jsonObject.getString("meeting_url");

                    // jsonObject 중 meeting_current 데이터를 추출한뒤 변수에 저장
                    // 모임의 현재 인원수
                    String meetingCurrent = jsonObject.getString("meeting_current");

                    // 문자열로 받아온 미팅 고유 아이디를 정수형으로 형변환
                    int meeting_seq = Integer.parseInt(meetingSeq);

                    // 문자열로 받아온 최대 인원수를 정수형으로 형변환
                    int meeting_members = Integer.parseInt(meetingMembers);

                    // 문자열로 받아온 현재 인원수를 정수형으로 형변환
                    int meeting_current = Integer.parseInt(meetingCurrent);

                    // String 으로 받은 날짜 데이터를 Date 형식으로 바꾸기위한 포맷
                    // T 는 날짜와 시간을 분리하기 위한 코드 Z는 UTC와 차이를 나타내는 코드 입니다.
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    // 받아온 날짜 문자열을 Date 포맷으로 변경
                    Date date = dateFormat.parse(meetingCreateDate);

                    me.setMeetingId(meeting_seq);
                    me.setMeetingName(meetingName);
                    me.setMeetingContent(meetingContent);
                    me.setMeetingMembers(meeting_members);
                    me.setMeetingCreateDate(date);
                    me.setMeetingUrl(meetingUrl);
                    me.setMeetingCurrent(meeting_current);

                    mi.add(me);
                }

            } catch (JSONException e){
                e.printStackTrace();
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }

        return Pair.create(num, mi);
    }


}
