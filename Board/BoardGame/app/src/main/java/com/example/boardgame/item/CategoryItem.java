package com.example.boardgame.item;

public class CategoryItem {
    private String name;
    boolean isSelected; // 선택 상태를 저장할 변수
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
