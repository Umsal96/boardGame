package com.example.boardgame.item;

public class ScheduleMemberItem {
    private int s_member_seq;
    private int schedule_seq;
    private int user_seq;
    private String schedule_date;
    private String schedule_time;
    public int getS_member_seq() {
        return s_member_seq;
    }

    public void setS_member_seq(int s_member_seq) {
        this.s_member_seq = s_member_seq;
    }

    public int getSchedule_seq() {
        return schedule_seq;
    }

    public void setSchedule_seq(int schedule_seq) {
        this.schedule_seq = schedule_seq;
    }

    public int getUser_seq() {
        return user_seq;
    }

    public void setUser_seq(int user_seq) {
        this.user_seq = user_seq;
    }

    public String getSchedule_date() {
        return schedule_date;
    }

    public void setSchedule_date(String schedule_date) {
        this.schedule_date = schedule_date;
    }

    public String getSchedule_time() {
        return schedule_time;
    }

    public void setSchedule_time(String schedule_time) {
        this.schedule_time = schedule_time;
    }
}
