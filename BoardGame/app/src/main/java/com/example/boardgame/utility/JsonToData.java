package com.example.boardgame.utility;

import android.util.Pair;

import com.example.boardgame.item.CafeGameItem;
import com.example.boardgame.item.CafeGameListItem;
import com.example.boardgame.item.CafeListItem;
import com.example.boardgame.item.CafeReviewItem;
import com.example.boardgame.item.CategoryItem;
import com.example.boardgame.item.ChattingItem;
import com.example.boardgame.item.CommentReplyItem;
import com.example.boardgame.item.FoodItem;
import com.example.boardgame.item.GameItem;
import com.example.boardgame.item.GameReviewItem;
import com.example.boardgame.item.MeetingBoardItem;
import com.example.boardgame.item.MeetingItem;
import com.example.boardgame.item.ReadItem;
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
    // 채팅을 현재 누구까지 읽었는지 정보를 json 형태에서 ArrayList에 넣기 위한 메소드
    public ArrayList<ReadItem> jsonToReadList(String json){
        ArrayList<ReadItem> Ri = new ArrayList<>();
        try{
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                ReadItem ri = new ReadItem();

                ri.setRead_seq(Integer.parseInt(jsonObject.getString("read_seq")));
                ri.setUser_seq(Integer.parseInt(jsonObject.getString("user_seq")));
                ri.setMeeting_seq(Integer.parseInt(jsonObject.getString("meeting_seq")));
                ri.setRead_time(jsonObject.getString("read_time"));
                ri.setChat_seq(Integer.parseInt(jsonObject.getString("chat_seq")));

                Ri.add(ri);
            }
        } catch (JSONException e){
            e.printStackTrace();
            return null;
        }
        return Ri;
    }

    // 일정에 참가한 유저의 닉네임 과 프로필을 을 가저온 json 을 ArrayList에 넣기위한 메소드
    public ArrayList<UserItem> jsonUserList(String json){

        ArrayList<UserItem> Ui = new ArrayList<>();

        try{
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                UserItem ui = new UserItem();

                String user_seq = jsonObject.getString("user_seq");
                String user_nickname = jsonObject.getString("user_nickname");
                String user_url = jsonObject.getString("user_url");

                ui.setUserSeq(Integer.parseInt(user_seq));
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
    // 채팅 리스트 가져옴
    public ArrayList<ChattingItem> jsonToChattingList(String json){
        ArrayList<ChattingItem> chit = new ArrayList<>();
        try{
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                ChattingItem ci = new ChattingItem();
                ci.setMessage_seq(Integer.parseInt(jsonObject.getString("message_seq")));
                ci.setMeeting_seq(Integer.parseInt(jsonObject.getString("meeting_seq")));
                ci.setUser_seq(Integer.parseInt(jsonObject.getString("user_seq")));
                ci.setMessage_content(jsonObject.getString("message_content"));
                ci.setMessage_read(Integer.parseInt(jsonObject.getString("message_read")));
                ci.setMessage_date(jsonObject.getString("message_date"));
                ci.setUser_nickname(jsonObject.getString("user_nickname"));
                ci.setUser_url(jsonObject.getString("user_url"));
                chit.add(ci);
            }
        }catch (JSONException e){
            e.printStackTrace();
            return null;
        }
        return chit;
    }

    // 카페 게임 정보 입력 리스트 가져오기
    public ArrayList<CafeGameListItem> jsonToCafeGameList(String json){
        ArrayList<CafeGameListItem> cgli = new ArrayList<>();
        try{
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                CafeGameListItem cai = new CafeGameListItem();
                cai.setGame_seq(Integer.parseInt(jsonObject.getString("game_seq")));
                cai.setGame_name(jsonObject.getString("game_name"));
                cai.setImage_url(jsonObject.getString("image_url"));
                cgli.add(cai);
            }
        } catch (JSONException e){
            e.printStackTrace();
            return null;
        }
        return cgli;
    }

    // 카페 게임 정보 리스트 가져오기
    public ArrayList<CafeGameItem> jsonToCafeGame(String json){
        ArrayList<CafeGameItem> cgi = new ArrayList<>();
        try{
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                CafeGameItem ci = new CafeGameItem();
                ci.setCafe_game_seq(Integer.parseInt(jsonObject.getString("cafe_game_seq")));
                ci.setGame_seq(Integer.parseInt(jsonObject.getString("game_seq")));
                ci.setCafe_seq(Integer.parseInt(jsonObject.getString("cafe_seq")));
                ci.setGame_name(jsonObject.getString("game_name"));
                ci.setImage_url(jsonObject.getString("image_url"));
                cgi.add(ci);
            }
        } catch (JSONException e){
            e.printStackTrace();
            return null;
        }
        return cgi;
    }
    // 음식 정보 리스트 가져오기
    public ArrayList<FoodItem> jsonToFoodList(String json){
        ArrayList<FoodItem> foi = new ArrayList<>();
        try{
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                FoodItem fi = new FoodItem();
                fi.setFood_seq(Integer.parseInt(jsonObject.getString("food_seq")));
                fi.setCafe_seq(Integer.parseInt(jsonObject.getString("cafe_seq")));
                fi.setFood_name(jsonObject.getString("food_name"));
                fi.setFood_price(Integer.parseInt(jsonObject.getString("food_price")));
                fi.setImgUrl(jsonObject.getString("image_url"));
                foi.add(fi);
            }
        } catch (JSONException e){
            e.printStackTrace();
            return null;
        }

        return foi;
    }

    // 카페 리뷰 리스트
    public ArrayList<CafeReviewItem> jsonToCafeReview(String json){
        ArrayList<CafeReviewItem> cri = new ArrayList<>();
        try{
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                CafeReviewItem ci = new CafeReviewItem();
                ci.setReview_seq(Integer.parseInt(jsonObject.getString("review_seq")));
                ci.setUser_seq(Integer.parseInt(jsonObject.getString("user_seq")));
                ci.setCafe_seq(Integer.parseInt(jsonObject.getString("cafe_seq")));
                ci.setReview_content(jsonObject.getString("review_content"));
                ci.setReview_grade(jsonObject.has("review_grade") && !jsonObject.isNull("review_grade") ? Float.parseFloat(jsonObject.getString("review_grade")) : 0.0f);
                ci.setReview_type(Integer.parseInt(jsonObject.getString("review_type")));
                ci.setReview_create_date(jsonObject.getString("review_create_date"));
                ci.setUser_url(jsonObject.getString("user_url"));
                ci.setUser_nickname(jsonObject.getString("user_nickname"));
                ci.setTo_seqs(jsonObject.getString("to_seqs"));
                ci.setImage_urls(jsonObject.getString("image_urls"));

                cri.add(ci);
            }
        } catch (JSONException e){
            e.printStackTrace();
            return null;
        }
        return cri;
    }

    // 카페 리스트
    public ArrayList<CafeListItem> jsonToCafeList(String json){
        ArrayList<CafeListItem> cli = new ArrayList<>();

        try{
            JSONArray jsonArray = new JSONArray(json);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                CafeListItem ci = new CafeListItem();
                ci.setCafe_seq(Integer.parseInt(jsonObject.getString("cafe_seq")));
                ci.setCafe_name(jsonObject.getString("cafe_name"));
                ci.setCafe_content(jsonObject.getString("cafe_content"));
                ci.setCafe_create_date(jsonObject.getString("cafe_create_date"));
                ci.setCafe_lat(jsonObject.getString("cafe_lat"));
                ci.setCafe_lnt(jsonObject.getString("cafe_lnt"));
                ci.setCafe_address(jsonObject.getString("cafe_address"));
                ci.setImage_url(jsonObject.getString("image_url"));
                ci.setImage_seq(jsonObject.has("image_seq") && !jsonObject.isNull("image_seq") ? Integer.parseInt(jsonObject.getString("image_seq")) : 0);
                ci.setAverage_review_grade(jsonObject.has("average_review_grade") && !jsonObject.isNull("average_review_grade") ? Float.parseFloat(jsonObject.getString("average_review_grade")) : 0.0f);

                cli.add(ci);
            }
        }catch (JSONException e){
            e.printStackTrace();
            return null;
        }
        return cli;
    }

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
