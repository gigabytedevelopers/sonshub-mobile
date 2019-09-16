package com.gigabytedevelopersinc.apps.sonshub.models;

public class MusicModel {
    private String title, musicDescription, musicTime, imageUrl, content,link;
    private int musicOption,musicImageResource;

    public MusicModel(String title, String musicDescription, String musicTime, String imageUrl, int musicOption){
        this.title = title;
        this.musicDescription = musicDescription;
        this.musicTime = musicTime;
        this.imageUrl = imageUrl;
        this.musicOption = musicOption;

    }

    public MusicModel(String imageUrl, String title, int musicOption){
        this.imageUrl = imageUrl;
        this.title = title;
        this.musicOption = musicOption;
    }

    public MusicModel(int musicImageResource, String title){
        this.musicImageResource = musicImageResource;
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getMusicImageResource() {
        return musicImageResource;
    }

    public void setMusicImageResource(int musicImageResource) {
        this.musicImageResource = musicImageResource;
    }

    public String getLink() {

        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public MusicModel(String imageUrl, String title, String content, String link){
        this.imageUrl = imageUrl;
        this.title = title;
        this.content = content;
        this.link = link;

    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


    public String getMusicTitle() {
        return title;
    }

    public void setMusicTitle(String musicTitle) {
        this.title = musicTitle;
    }

    public String getMusicDescription() {
        return musicDescription;
    }

    public void setMusicDescription(String musicDescription) {
        this.musicDescription = musicDescription;
    }

    public String getMusicTime() {
        return musicTime;
    }

    public void setMusicTime(String musicTime) {
        this.musicTime = musicTime;
    }


    public int getMusicOption() {
        return musicOption;
    }

    public String getMusicImage() {
        return imageUrl;
    }

    public void setMusicImage(String musicImage) {
        this.imageUrl = musicImage;
    }

    public void setMusicOption(int musicOption) {
        this.musicOption = musicOption;

    }
}
