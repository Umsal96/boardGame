package com.example.boardgame.item;

public class GameReviewItem {
    private int review_seq;
    private int user_seq;
    private int game_seq;
    private String review_content;
    private float review_grade;
    private int review_type;
    private String review_create_date;
    private String user_url;
    private String user_nickname;
    private String to_seqs;
    private String image_urls;
    public int getReview_seq() {
        return review_seq;
    }

    public void setReview_seq(int review_seq) {
        this.review_seq = review_seq;
    }

    public int getUser_seq() {
        return user_seq;
    }

    public void setUser_seq(int user_seq) {
        this.user_seq = user_seq;
    }

    public int getGame_seq() {
        return game_seq;
    }

    public void setGame_seq(int game_seq) {
        this.game_seq = game_seq;
    }

    public String getReview_content() {
        return review_content;
    }

    public void setReview_content(String review_content) {
        this.review_content = review_content;
    }

    public float getReview_grade() {
        return review_grade;
    }

    public void setReview_grade(float review_grade) {
        this.review_grade = review_grade;
    }

    public int getReview_type() {
        return review_type;
    }

    public void setReview_type(int review_type) {
        this.review_type = review_type;
    }

    public String getReview_create_date() {
        return review_create_date;
    }

    public void setReview_create_date(String review_create_date) {
        this.review_create_date = review_create_date;
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

    public String getTo_seqs() {
        return to_seqs;
    }

    public void setTo_seqs(String to_seqs) {
        this.to_seqs = to_seqs;
    }

    public String getImage_urls() {
        return image_urls;
    }

    public void setImage_urls(String image_urls) {
        this.image_urls = image_urls;
    }
}
