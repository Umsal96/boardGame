package com.example.boardgame.item;

import java.util.Date;

public class MeetingItem {

    private int meetingId;
    private String meetingName;
    private String meetingContent;
    private int meetingMembers; // 최대 인원수
    private Date meetingCreateDate;
    private String meetingUrl;
    private int meetingCurrent; // 현재 인원수

    public int getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(int meetingId) {
        this.meetingId = meetingId;
    }

    public String getMeetingName() {
        return meetingName;
    }

    public void setMeetingName(String meetingName) {
        this.meetingName = meetingName;
    }

    public String getMeetingContent() {
        return meetingContent;
    }

    public void setMeetingContent(String meetingContent) {
        this.meetingContent = meetingContent;
    }

    public int getMeetingMembers() {
        return meetingMembers;
    }

    public void setMeetingMembers(int meetingMembers) {
        this.meetingMembers = meetingMembers;
    }

    public Date getMeetingCreateDate() {
        return meetingCreateDate;
    }

    public void setMeetingCreateDate(Date meetingCreateDate) {
        this.meetingCreateDate = meetingCreateDate;
    }

    public String getMeetingUrl() {
        return meetingUrl;
    }

    public void setMeetingUrl(String meetingUrl) {
        this.meetingUrl = meetingUrl;
    }

    public int getMeetingCurrent() {
        return meetingCurrent;
    }

    public void setMeetingCurrent(int meetingCurrent) {
        this.meetingCurrent = meetingCurrent;
    }
}
