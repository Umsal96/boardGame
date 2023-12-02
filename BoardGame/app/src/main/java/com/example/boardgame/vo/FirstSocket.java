package com.example.boardgame.vo;

import java.util.ArrayList;
import java.util.List;

public class FirstSocket {
    private int userSeq;
    private List<Integer> chatSeqs;

    public FirstSocket() {
        chatSeqs = new ArrayList<>();
    }
    public int getUserSeq() {
        return userSeq;
    }

    public void setUserSeq(int userSeq) {
        this.userSeq = userSeq;
    }

    public List<Integer> getChatSeqs() {
        return chatSeqs;
    }

    public void setChatSeqs(List<Integer> chatSeqs) {
        this.chatSeqs = chatSeqs;
    }

    public void addChatSeq(Integer chatSeq){
        chatSeqs.add(chatSeq);
    }
}
