package com.example.boardgame.item;

public class CafeGameItem { // 게임정보를 getCafe에서 보는것
    private int cafe_game_seq;
    private int game_seq;
    private int cafe_seq;
    private String game_name;
    private String image_url;

    public String getGame_name() {
        return game_name;
    }

    public void setGame_name(String game_name) {
        this.game_name = game_name;
    }

    public int getCafe_game_seq() {
        return cafe_game_seq;
    }

    public void setCafe_game_seq(int cafe_game_seq) {
        this.cafe_game_seq = cafe_game_seq;
    }

    public int getGame_seq() {
        return game_seq;
    }

    public void setGame_seq(int game_seq) {
        this.game_seq = game_seq;
    }

    public int getCafe_seq() {
        return cafe_seq;
    }

    public void setCafe_seq(int cafe_seq) {
        this.cafe_seq = cafe_seq;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }
}
