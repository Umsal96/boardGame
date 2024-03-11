package com.example.boardgame.item;

public class CafeGameListItem { // 다이얼로그에 정보를 넣는것
    private int game_seq;
    private String game_name;
    private String image_url;
    private boolean checked;

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
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

    public String getImage_url() {
        return image_url;
    }
    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }
}
