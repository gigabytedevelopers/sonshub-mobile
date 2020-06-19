package com.gigabytedevelopersinc.apps.sonshub.models;

public class NewsModel {
    private String title, newsDescription, newsTime, imageUrl, content, link;

    public NewsModel(String title, String newsDescription, String newsTime, String newsImage, String content,String link) {
        this.title = title;
        this.newsDescription = newsDescription;
        this.newsTime = newsTime;
        this.imageUrl = newsImage;
        this.content = content;
        this.link = link;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public NewsModel(String newsImage, String title) {
        this.imageUrl = newsImage;
        this.title = title;

    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getNewsTitle() {
        return title;
    }

    public void setNewsTitle(String newsTitle) {
        this.title = newsTitle;
    }

    public String getNewsDescription() {
        return newsDescription;
    }

    public void setNewsDescription(String newsDescription) {
        this.newsDescription = newsDescription;
    }

    public String getNewsTime() {
        return newsTime;
    }

    public void setNewsTime(String newsTime) {
        this.newsTime = newsTime;
    }

    public String getNewsImage() {
        return imageUrl;
    }

    public void setNewsImage(String newsImage) {
        this.imageUrl = newsImage;
    }
}
