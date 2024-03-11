package com.example.boardgame.item;

public class FoodItem {

    private int food_seq;
    private int cafe_seq;
    private String food_name;
    private int food_price;
    private String imgUrl;

    public int getFood_seq() {
        return food_seq;
    }

    public void setFood_seq(int food_seq) {
        this.food_seq = food_seq;
    }

    public int getCafe_seq() {
        return cafe_seq;
    }

    public void setCafe_seq(int cafe_seq) {
        this.cafe_seq = cafe_seq;
    }

    public String getFood_name() {
        return food_name;
    }

    public void setFood_name(String food_name) {
        this.food_name = food_name;
    }

    public int getFood_price() {
        return food_price;
    }

    public void setFood_price(int food_price) {
        this.food_price = food_price;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
