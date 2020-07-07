package com.example.tinderclonetest1.placeholder;
public class ChatsList {

    private String senderId;
    private String receiverId;
    private String message;
    private String imageUrl;
    private boolean issen;

    public ChatsList() {
    }

    public ChatsList(String senderId, String receiverId, String message, boolean issen, String imageUrl) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.message = message;
        this.issen = issen;
        this.imageUrl = imageUrl;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isIssen() {
        return issen;
    }

    public void setIssen(boolean issen) {
        this.issen = issen;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
