package com.example.android.newsapp;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<NewsItem>>{

    // initialize global variables
    RecyclerView recyclerView;
    ProgressBar progressBar;
    TextView emptyTextView;
    NewsItemAdapter newsItemAdapter;
    ArrayList<NewsItem> mNewsItems;

    /** target url for a Guardian API query */
    private static final String JSON_RESPONSE = "https://content.guardianapis.com/search?show-fields=thumbnail,trailText,headline,byline&api-key=49c021e8-1aba-47fe-887d-e7ff6cd5888b";

    public static final String LOG_TAG = MainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set App Bar title to name of project
        // find Toolbar view in layout
        Toolbar myToolbar = findViewById(R.id.toolbar);
        // Set the Toolbar as Action Bar
        setSupportActionBar(myToolbar);
        // Set title of action bar to appropriate label for this Activity
        getSupportActionBar().setTitle(R.string.app_name);

        // Set the padding to match the Status Bar height (to avoid title being cut off by
        // transparent toolbar


        myToolbar.setPadding(0, 25, 0, 0);


        // find references to views in the layout
        recyclerView = findViewById(R.id.newsItemRecyclerView);

        progressBar = findViewById(R.id.loading_spinner);

        emptyTextView = findViewById(R.id.empty_list_view);

        mNewsItems = new ArrayList<>();
        // Create adapter passing in this ArrayList as the data source

        newsItemAdapter = new NewsItemAdapter(this, mNewsItems);

        // Attach the adapter to the RecyclerView to populate items
        recyclerView.setAdapter(newsItemAdapter);

        // Set layout manager to position the items
        recyclerView.setLayoutManager(new LinearLayoutManager(this));





        // check there is a network connection
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        if(isConnected){
            // If there is a network connection,
            // create new instance of load manager and instantiate new Loader object , or renew existing one.
            getLoaderManager().initLoader(0,null, this);
            Log.v(LOG_TAG, "load manager intialized");
        } else {
            // if no connection, display message explaining the issue to users
            progressBar.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
            emptyTextView.setText(R.string.user_offline);
            Drawable img = getDrawable(R.drawable.ic_signal_wifi_off);
            emptyTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, null, img);
        }

    }

    @Override
    public Loader<ArrayList<NewsItem>> onCreateLoader(int i, Bundle bundle) {
        // Create a new loader for the given URL
        Log.v(LOG_TAG, "onCreateLoader called");
        return new NewsItemLoader(this, JSON_RESPONSE);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<NewsItem>> loader, ArrayList<NewsItem> newsItems) {
        Log.v(LOG_TAG, "onFinishedLoader called");

        // make progress bar disappear when background thread finishes loading
        progressBar.setVisibility(View.GONE);


        if (newsItems == null) {
            return;
        }
        if (newsItems.isEmpty()){
            recyclerView.setVisibility(View.GONE);
            emptyTextView.setVisibility(View.VISIBLE);
            emptyTextView.setText(R.string.no_news_found);
        }
        if (newsItems != null && !newsItems.isEmpty()) {
            emptyTextView.setVisibility(View.GONE);
            mNewsItems.addAll(newsItems);
            newsItemAdapter.notifyDataSetChanged();
        }


    }

    @Override
    public void onLoaderReset(Loader<ArrayList<NewsItem>> loader) {
        Log.v(LOG_TAG, "onResetLoader called");
        // Loader reset, so we can clear out our existing data.
        mNewsItems.clear();
        newsItemAdapter.notifyDataSetChanged();

    }
}
