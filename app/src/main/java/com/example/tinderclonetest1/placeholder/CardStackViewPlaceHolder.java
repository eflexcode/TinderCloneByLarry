package com.example.tinderclonetest1.placeholder;

public class CardStackViewPlaceHolder {

    private String userId;
    private String username;
    private String firstImageUrl;
    private String secondImageUrl;
    private String gender;
    private String age;
    private String isOnline;
    private String city;
    private String about;

    //remember this firebase needs a default constructor
    public CardStackViewPlaceHolder() {
    }

    public CardStackViewPlaceHolder(String userId, String username, String firstImageUrl, String secondImageUrl, String gender, String age, String isOnline,String city,String about) {
        this.userId = userId;
        this.username = username;
        this.firstImageUrl = firstImageUrl;
        this.secondImageUrl = secondImageUrl;
        this.gender = gender;
        this.age = age;
        this.isOnline = isOnline;
        this.city = city;
        this.about = about;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstImageUrl() {
        return firstImageUrl;
    }

    public void setFirstImageUrl(String firstImageUrl) {
        this.firstImageUrl = firstImageUrl;
    }

    public String getSecondImageUrl() {
        return secondImageUrl;
    }

    public void setSecondImageUrl(String secondImageUrl) {
        this.secondImageUrl = secondImageUrl;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getIsOnline() {
        return isOnline;
    }

    public void setIsOnline(String isOnline) {
        this.isOnline = isOnline;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }
}
