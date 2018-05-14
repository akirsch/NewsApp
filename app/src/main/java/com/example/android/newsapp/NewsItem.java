package com.example.android.newsapp;


public class NewsItem {
    private String category;
    private String headline;
    private String trailText;
    private String author;
    private String date;
    private String webUrl;
    private String thumbnailUrl;

    /**
     * Constructor to create new NewItem Object
     * @param category category of new item
     * @param headline headline of article
     * @param trailText intro to article
     * @param author name of author
     * @param date date article published
     * @param webUrl web url of article on website
     * @param thumbnailUrl url of image associated with article
     */
    public NewsItem (String category, String headline, String trailText, String author, String date, String webUrl, String thumbnailUrl) {
        this.category = category;
        this.headline = headline;
        this.trailText = trailText;
        this.author = author;
        this.date = date;
        this.webUrl = webUrl;
        this.thumbnailUrl = thumbnailUrl;
    }

    /**
     * Get the category of news item.
     */
    public String getCategory() {
        return category;
    }

    /**
     * Get the headline of news item.
     */
    public String getHeadline() {
        return headline;
    }

    /**
     * Get the trailText of news item.
     */
    public String getTrailText() {
        return trailText;
    }

    /**
     * Get the author of news item.
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Get the date of news item.
     */
    public String getDate() {
        return date;
    }

    /**
     * Get the web url of news item.
     */
    public String getWebUrl() {
        return webUrl;
    }

    /**
     * Get the image url of news item.
     */
    public String getThumbnailUrl() {
        return thumbnailUrl;
    }
}
