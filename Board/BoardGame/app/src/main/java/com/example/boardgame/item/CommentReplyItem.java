package com.example.boardgame.item;

public class CommentReplyItem {

    private int reply_seq; // 댓글 고유 아이디
    private int board_seq; // 게시글 고유 아이디
    private int user_seq; // 유저 고유 아이디
    private String user_nick; // 유저의 닉네임
    private String user_url; // 유저의 프로필
    private String reply_content; // 댓글 내용
    private int reply_ref; // 댓글 그룹 번호
    private int reply_order; // 그룹내의 순서

    public String getUser_nick() {
        return user_nick;
    }

    public void setUser_nick(String user_nick) {
        this.user_nick = user_nick;
    }

    public String getUser_url() {
        return user_url;
    }

    public void setUser_url(String user_url) {
        this.user_url = user_url;
    }

    private int reply_del; // 개시글이 삭제가 됬는지 안됬는지
    private String reply_create_date; // 댓글 생성일

    public int getReply_seq() {
        return reply_seq;
    }

    public void setReply_seq(int reply_seq) {
        this.reply_seq = reply_seq;
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

    public String getReply_content() {
        return reply_content;
    }

    public void setReply_content(String reply_content) {
        this.reply_content = reply_content;
    }

    public int getReply_ref() {
        return reply_ref;
    }

    public void setReply_ref(int reply_ref) {
        this.reply_ref = reply_ref;
    }

    public int getReply_order() {
        return reply_order;
    }

    public void setReply_order(int reply_order) {
        this.reply_order = reply_order;
    }

    public int getReply_del() {
        return reply_del;
    }

    public void setReply_del(int reply_del) {
        this.reply_del = reply_del;
    }

    public String getReply_create_date() {
        return reply_create_date;
    }

    public void setReply_create_date(String reply_create_date) {
        this.reply_create_date = reply_create_date;
    }
}
