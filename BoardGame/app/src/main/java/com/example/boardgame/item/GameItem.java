package com.example.boardgame.item;

public class GameItem {
    private int game_seq;
    private String game_name;
    private String game_summary;
    private int game_min;
    private int game_max;
    private String game_detail;
    private String game_create_data;
    private String image_url;
    private float average_review_grade;
    public float getAverage_review_grade() {
        return average_review_grade;
    }

    public void setAverage_review_grade(float average_review_grade) {
        this.average_review_grade = average_review_grade;
    }

    public int getGame_seq() {
        return game_seq;
    }

    public void setGame_seq(int game_seq) {
        this.game_seq = game_seq;
    }

    public String getGame_name() {
        return game_name;
    }

    public void setGame_name(String game_name) {
        this.game_name = game_name;
    }

    public String getGame_summary() {
        return game_summary;
    }

    public void setGame_summary(String game_summary) {
        this.game_summary = game_summary;
    }

    public int getGame_min() {
        return game_min;
    }

    public void setGame_min(int game_min) {
        this.game_min = game_min;
    }

    public int getGame_max() {
        return game_max;
    }

    public void setGame_max(int game_max) {
        this.game_max = game_max;
    }

    public String getGame_detail() {
        return game_detail;
    }

    public void setGame_detail(String game_detail) {
        this.game_detail = game_detail;
    }

    public String getGame_create_data() {
        return game_create_data;
    }

    public void setGame_create_data(String game_create_data) {
        this.game_create_data = game_create_data;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }
}
