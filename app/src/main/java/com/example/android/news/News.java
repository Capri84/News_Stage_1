package com.example.android.news;

/**
 * Created by Capri on 04.06.2018.
 */

public class News {
    private String mSection;
    private String mDate;
    private String mImageUrl;
    private String mTitle;
    private String mNewsTextPreview;
    private String mAuthor;
    private String mUrl;

    // Create a new News object.
    public News(String section, String date, String imageUrl, String title,
                String newsTextPreview, String author, String url) {
        this.mSection = section;
        this.mDate = date;
        this.mImageUrl = imageUrl;
        this.mTitle = title;
        this.mNewsTextPreview = newsTextPreview;
        this.mAuthor = author;
        this.mUrl = url;
    }

    // Get the section of the news.
    public String getSection() {
        return mSection;
    }

    // Get the date of the news.
    public String getDate() {
        return mDate;
    }

    // Get the picture ID of the news.
    public String getImageUrl() {
        return mImageUrl;
    }

    // Get the title of the news.
    public String getNewsTitle() {
        return mTitle;
    }

    // Get the text of the news for the preview.
    public String getNewsTextPreview() {
        return mNewsTextPreview;
    }

    // Get the author of the news.
    public String getAuthor() {
        return mAuthor;
    }

    // Get the URL of the news.
    public String getUrl() {
        return mUrl;
    }

    @Override
    public String toString() {
        return "News{" +
                "mSection='" + mSection + '\'' +
                ", mDate=" + mDate +
                ", mImageResourceId=" + mImageUrl +
                ", mTitle='" + mTitle + '\'' +
                ", mNewsTextPreview='" + mNewsTextPreview + '\'' +
                ", mAuthor='" + mAuthor + '\'' +
                ", mUrl='" + mUrl + '\'' +
                '}';
    }
}