package com.gigabytedevelopersinc.apps.sonshub.models;

public class MainListModel {
    private String imageUrl, title,description, time, content, link;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public MainListModel(String imageUrl, String title, String link , String description, String time, String content){
        this.imageUrl = imageUrl;
        this.title = title;
        this.description = description;
        this.time = time;
        this.content = content;
        this.link = link;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
