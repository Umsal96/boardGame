package com.example.boardgame.item;

public class CafeListItem {
    private int cafe_seq;
    private String cafe_name;
    private String cafe_content;
    private String cafe_create_date;
    private String cafe_lat;
    private String cafe_lnt;
    private String cafe_address;
    private String image_url;
    private int image_seq;
    private float average_review_grade;

    public int getCafe_seq() {
        return cafe_seq;
    }

    public void setCafe_seq(int cafe_seq) {
        this.cafe_seq = cafe_seq;
    }

    public String getCafe_name() {
        return cafe_name;
    }

    public void setCafe_name(String cafe_name) {
        this.cafe_name = cafe_name;
    }

    public String getCafe_content() {
        return cafe_content;
    }

    public void setCafe_content(String cafe_content) {
        this.cafe_content = cafe_content;
    }

    public String getCafe_create_date() {
        return cafe_create_date;
    }

    public void setCafe_create_date(String cafe_create_date) {
        this.cafe_create_date = cafe_create_date;
    }

    public String getCafe_lat() {
        return cafe_lat;
    }

    public void setCafe_lat(String cafe_lat) {
        this.cafe_lat = cafe_lat;
    }

    public String getCafe_lnt() {
        return cafe_lnt;
    }

    public void setCafe_lnt(String cafe_lnt) {
        this.cafe_lnt = cafe_lnt;
    }

    public String getCafe_address() {
        return cafe_address;
    }

    public void setCafe_address(String cafe_address) {
        this.cafe_address = cafe_address;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public int getImage_seq() {
        return image_seq;
    }

    public void setImage_seq(int image_seq) {
        this.image_seq = image_seq;
    }

    public float getAverage_review_grade() {
        return average_review_grade;
    }

    public void setAverage_review_grade(float average_review_grade) {
        this.average_review_grade = average_review_grade;
    }
}
