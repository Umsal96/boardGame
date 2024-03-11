package com.example.boardgame.item;

import java.util.List;

public class ChattingItem {
    private int message_seq;
    private int meeting_seq;
    private int user_seq;
    private String message_content;
    private int message_read;
    private String message_date;
    private String user_nickname;
    private String user_url;

    private List<Integer> readUserId;

    public void readChatting(int userId){
        readUserId.add(userId);
    }
    public int returnReadSize(){
        return readUserId.size();
    }
    public int getMessage_seq() {
        return message_seq;
    }

    public void setMessage_seq(int message_seq) {
        this.message_seq = message_seq;
    }

    public int getMeeting_seq() {
        return meeting_seq;
    }

    public void setMeeting_seq(int meeting_seq) {
        this.meeting_seq = meeting_seq;
    }

    public int getUser_seq() {
        return user_seq;
    }

    public void setUser_seq(int user_seq) {
        this.user_seq = user_seq;
    }

    public String getMessage_content() {
        return message_content;
    }

    public void setMessage_content(String message_content) {
        this.message_content = message_content;
    }

    public int getMessage_read() {
        return message_read;
    }

    public void setMessage_read(int message_read) {
        this.message_read = message_read;
    }

    public String getMessage_date() {
        return message_date;
    }

    public void setMessage_date(String message_date) {
        this.message_date = message_date;
    }

    public String getUser_nickname() {
        return user_nickname;
    }

    public void setUser_nickname(String user_nickname) {
        this.user_nickname = user_nickname;
    }

    public String getUser_url() {
        return user_url;
    }

    public void setUser_url(String user_url) {
        this.user_url = user_url;
    }
}
