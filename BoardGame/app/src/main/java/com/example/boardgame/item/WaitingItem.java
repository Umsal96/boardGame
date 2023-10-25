package com.example.boardgame.item;

public class WaitingItem {

    private String userUrl;
    private String userNick;
    private int userSeq;
    private int meetingSeq;
    public String getUserUrl() {
        return userUrl;
    }

    public void setUserUrl(String userUrl) {
        this.userUrl = userUrl;
    }

    public String getUserNick() {
        return userNick;
    }

    public void setUserNick(String userNick) {
        this.userNick = userNick;
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
}
