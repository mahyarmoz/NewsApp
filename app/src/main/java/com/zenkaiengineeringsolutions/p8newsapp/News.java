package com.zenkaiengineeringsolutions.p8newsapp;

/**
 * Created by mahyar on 2017-07-20.
 */

public class News {

    private String mTitle;
    private String mSection;
    private String mDate;
    private String mAuthor;
    private String mUrl;

    public News (String title, String section, String date, String url, String author){
        mTitle = title;
        mSection = section;
        mDate = date;
        mUrl = url;
        mAuthor = author;
    }

    public String getUrl() {
        return mUrl;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getSection() {
        return mSection;
    }

    public String getDate() {
        return mDate;
    }

    public String getAuthor() {return mAuthor;}

}
