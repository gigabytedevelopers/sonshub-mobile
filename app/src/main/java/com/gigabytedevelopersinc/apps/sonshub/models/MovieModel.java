package com.gigabytedevelopersinc.apps.sonshub.models;

public class MovieModel {

    private String title;
    private String movieDescription;
    private String movieTime;
    private String imageUrl;
    private String link;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDownloadLink() {
        return downloadLink;
    }

    public void setDownloadLink(String downloadLink) {
        this.downloadLink = downloadLink;
    }

    private String content;
    private String downloadLink;
    private int movieOptions, movieImageResource;


    public MovieModel(String title, String movieDescription, String movieTime, String movieImage, int movieOptions){
        this.title = title;
        this.movieDescription = movieDescription;
        this.movieTime = movieTime;
        this.imageUrl = movieImage;
        this.movieOptions = movieOptions;
    }

    public MovieModel(String movieImage, String title, int movieOptions){
        this.imageUrl = movieImage;
        this.title = title;
        this.movieOptions = movieOptions;

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

    public String getLink() {

        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public int getMovieImageResource() {
        return movieImageResource;
    }

    public void setMovieImageResource(int movieImageResource) {
        this.movieImageResource = movieImageResource;
    }

    public MovieModel(int movieImageResource, String title){
        this.movieImageResource = movieImageResource;
        this.title = title;
    }
    public MovieModel(String movieImage, String title, String content,String link){
        this.imageUrl = movieImage;
        this.title = title;
        this.content = content;
        this.link = link;
    }

    public String getMovieTitle() {
        return title;
    }

    public void setMovieTitle(String movieTitle) {
        this.title = movieTitle;
    }

    public String getMovieDescription() {
        return movieDescription;
    }

    public void setMovieDescription(String movieDescription) {
        this.movieDescription = movieDescription;
    }

    public String getMovieTime() {
        return movieTime;
    }

    public void setMovieTime(String movieTime) {
        this.movieTime = movieTime;
    }

    public String getMovieImage() {
        return imageUrl;
    }

    public void setMovieImage(String movieImage) {
        this.imageUrl = movieImage;
    }

    public int getMovieOptions() {
        return movieOptions;
    }

    public void setMovieOptions(int movieOptions) {
        this.movieOptions = movieOptions;
    }


}
