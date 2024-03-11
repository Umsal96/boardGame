package com.example.boardgame.item;

public class ReadItem {
    private int read_seq;
    private int user_seq;
    private int meeting_seq;
    private String read_time;
    private int chat_seq;

    public int getChat_seq() {
        return chat_seq;
    }

    public void setChat_seq(int chat_seq) {
        this.chat_seq = chat_seq;
    }

    public int getRead_seq() {
        return read_seq;
    }

    public void setRead_seq(int read_seq) {
        this.read_seq = read_seq;
    }

    public int getUser_seq() {
        return user_seq;
    }

    public void setUser_seq(int user_seq) {
        this.user_seq = user_seq;
    }

    public int getMeeting_seq() {
        return meeting_seq;
    }

    public void setMeeting_seq(int meeting_seq) {
        this.meeting_seq = meeting_seq;
    }

    public String getRead_time() {
        return read_time;
    }

    public void setRead_time(String read_time) {
        this.read_time = read_time;
    }
}
