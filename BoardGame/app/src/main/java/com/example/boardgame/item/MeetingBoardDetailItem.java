package com.example.boardgame.item;

public class MeetingBoardDetailItem {
    private int board_seq;
    private int user_seq;
    private int meeting_seq;
    private String board_title;
    private String board_content;
    private String board_type;
    private String board_create_date;
    private String user_url;
    private String user_nickname;
    private String image_urls;
    private String to_seqs;

    public String getTo_seqs() {
        return to_seqs;
    }

    public void setTo_seqs(String to_seqs) {
        this.to_seqs = to_seqs;
    }

    public int getBoard_seq() {
        return board_seq;
    }

    public void setBoard_seq(int board_seq) {
        this.board_seq = board_seq;
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

    public String getBoard_title() {
        return board_title;
    }

    public void setBoard_title(String board_title) {
        this.board_title = board_title;
    }

    public String getBoard_content() {
        return board_content;
    }

    public void setBoard_content(String board_content) {
        this.board_content = board_content;
    }

    public String getBoard_type() {
        return board_type;
    }

    public void setBoard_type(String board_type) {
        this.board_type = board_type;
    }

    public String getBoard_create_date() {
        return board_create_date;
    }

    public void setBoard_create_date(String board_create_date) {
        this.board_create_date = board_create_date;
    }

    public String getUser_url() {
        return user_url;
    }

    public void setUser_url(String user_url) {
        this.user_url = user_url;
    }

    public String getUser_nickname() {
        return user_nickname;
    }

    public void setUser_nickname(String user_nickname) {
        this.user_nickname = user_nickname;
    }

    public String getImage_urls() {
        return image_urls;
    }

    public void setImage_urls(String image_urls) {
        this.image_urls = image_urls;
    }
}
