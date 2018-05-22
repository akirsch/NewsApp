package com.example.android.newsapp;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.util.ArrayList;

public class NewsItemLoader extends AsyncTaskLoader<ArrayList<NewsItem>> {

    /**
     * Query URL
     */
    String mUrl;

    /**
     * Constructs a new {@link NewsItemLoader}.
     *
     * @param context of the activity
     * @param url     to load data from
     */
    public NewsItemLoader(Context context, String url) {
        super(context);
        this.mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        Log.v(NewsItemLoader.class.getName(), "onStartLoader called");
        forceLoad();
    }

    /**
     * This is on a background thread.
     */
    @Override
    public ArrayList<NewsItem> loadInBackground() {
        Log.v(NewsItemLoader.class.getName(), "onLoadInBackground called");
        // Don't perform the request if there are no URLs, or the first URL is null.
        if (this.mUrl == null) {
            return null;
        }
        // Perform the HTTP request for earthquake data and process the response.
        ArrayList<NewsItem> latestNewsItems = QueryUtils.fetchNewsItemData(this.mUrl);
        return latestNewsItems;
    }


}
