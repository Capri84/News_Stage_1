package com.example.android.news;

import android.widget.ImageView;

/**
 * Created by Capri on 04.06.2018.
 */

public class News {
    private String mSection;
    private long mDate;
    private int mImageResourceId;
    private String mTitle;
    private String mNewsTextPreview;
    private String mAuthor;
    private String mUrl;

    // Create a new News object.
    public News(String section, long date, int imageResourceId, String title,
                String newsTextPreview, String author, String url) {
        this.mSection = section;
        this.mDate = date;
        this.mImageResourceId = imageResourceId;
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
    public long getDate() {
        return mDate;
    }
    // Get the picture ID of the news.
    public int getImageResourceId() {
        return mImageResourceId;
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
                ", mImageResourceId=" + mImageResourceId +
                ", mTitle='" + mTitle + '\'' +
                ", mNewsTextPreview='" + mNewsTextPreview + '\'' +
                ", mAuthor='" + mAuthor + '\'' +
                ", mUrl='" + mUrl + '\'' +
                '}';
    }
}
