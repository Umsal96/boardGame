package com.example.boardgame.item;

import java.util.Date;

public class ScheduleItem {


    private int scheduleSeq;
    private int userSeq;
    private int meetingSeq;
    private String scheduleTitle;
    private String schedule_date;
    private String schedule_time;
    private int schedule_member_max;
    private int schedule_member_current;
    private String schedule_place_name;
    private String schedule_place_address;
    private String schedule_lat;
    private String schedule_lnt;
    private String schedule_create_date;

    public int getScheduleSeq() {
        return scheduleSeq;
    }

    public void setScheduleSeq(int scheduleSeq) {
        this.scheduleSeq = scheduleSeq;
    }

    public int getUserSeq() {
        return userSeq;
    }

    public void setUserSeq(int userSeq) {
        this.userSeq = userSeq;
    }

    public int getMeetingSeq() {
        return meetingSeq;
    }

    public void setMeetingSeq(int meetingSeq) {
        this.meetingSeq = meetingSeq;
    }

    public String getScheduleTitle() {
        return scheduleTitle;
    }

    public void setScheduleTitle(String scheduleTitle) {
        this.scheduleTitle = scheduleTitle;
    }

    public String getSchedule_date() {
        return schedule_date;
    }

    public void setSchedule_date(Date schedule_date) {
        this.schedule_date = String.valueOf(schedule_date);
    }

    public String getSchedule_time() {
        return schedule_time;
    }

    public void setSchedule_time(String schedule_time) {
        this.schedule_time = schedule_time;
    }

    public int getSchedule_member_max() {
        return schedule_member_max;
    }

    public void setSchedule_member_max(int schedule_member_max) {
        this.schedule_member_max = schedule_member_max;
    }

    public int getSchedule_member_current() {
        return schedule_member_current;
    }

    public void setSchedule_member_current(int schedule_member_current) {
        this.schedule_member_current = schedule_member_current;
    }

    public String getSchedule_place_name() {
        return schedule_place_name;
    }

    public void setSchedule_place_name(String schedule_place_name) {
        this.schedule_place_name = schedule_place_name;
    }

    public String getSchedule_place_address() {
        return schedule_place_address;
    }

    public void setSchedule_place_address(String schedule_place_address) {
        this.schedule_place_address = schedule_place_address;
    }

    public String getSchedule_lat() {
        return schedule_lat;
    }

    public void setSchedule_lat(String schedule_lat) {
        this.schedule_lat = schedule_lat;
    }

    public String getSchedule_lnt() {
        return schedule_lnt;
    }

    public void setSchedule_lnt(String schedule_lnt) {
        this.schedule_lnt = schedule_lnt;
    }

    public void setSchedule_date(String schedule_date) {
        this.schedule_date = schedule_date;
    }

    public String getSchedule_create_date(String scheduleCreateDate) {
        return schedule_create_date;
    }

    public void setSchedule_create_date(String schedule_create_date) {
        this.schedule_create_date = schedule_create_date;
    }
}
