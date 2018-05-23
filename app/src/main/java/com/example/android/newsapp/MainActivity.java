package com.example.android.newsapp;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<NewsItem>> {

    private static final String LOG_TAG = MainActivity.class.getName();
    /**
     * target url for a Guardian API query
     */
    private static final String JSON_RESPONSE = "https://content.guardianapis.com/search?";

    // initialize String constant to store value of private api key the the Guardian Api
    private static final String API_KEY = "49c021e8-1aba-47fe-887d-e7ff6cd5888b";

    // initialize global variables
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView emptyTextView;
    private NewsItemAdapter newsItemAdapter;
    private ArrayList<NewsItem> mNewsItems;

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
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.app_name);

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

        NetworkInfo activeNetwork = Objects.requireNonNull(cm).getActiveNetworkInfo();

        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        if (isConnected) {
            // If there is a network connection,
            // create new instance of load manager and instantiate new Loader object , or renew existing one.
            getLoaderManager().initLoader(0, null, this);
            Log.v(LOG_TAG, "load manager initialized");
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
    // This method initialize the contents of the Activity's options menu.
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the Options Menu we specified in XML
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    // This method opens Settings Activity when the settings option is selected from the menu
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<ArrayList<NewsItem>> onCreateLoader(int i, Bundle bundle) {
        // Create a new loader for the given URL
        Log.v(LOG_TAG, "onCreateLoader called");

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        String topic = sharedPrefs.getString(
                getString(R.string.settings_display_by_topic_key),
                getString(R.string.settings_display_by_topic_default));

        String dateOrder = sharedPrefs.getString(
                getString(R.string.settings_order_by_date_key),
                getString(R.string.settings_order_by_date_default));

        // parse breaks apart the URI string that's passed into its parameter
        Uri baseUri = Uri.parse(JSON_RESPONSE);

        // buildUpon prepares the baseUri that we just parsed so we can add query parameters to it
        Uri.Builder uriBuilder = baseUri.buildUpon();

        // Append query parameter and its value. For example, the `format=geojson`
        uriBuilder.appendQueryParameter("format", "json");
        uriBuilder.appendQueryParameter("section", topic);
        uriBuilder.appendQueryParameter("show-fields", getResources().getString(R.string.show_fields_values));
        uriBuilder.appendQueryParameter("order-by", dateOrder);
        uriBuilder.appendQueryParameter("api-key", API_KEY);

        Log.v(LOG_TAG, uriBuilder.toString());

        return new NewsItemLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<NewsItem>> loader, ArrayList<NewsItem> newsItems) {
        Log.v(LOG_TAG, "onFinishedLoader called");

        // make progress bar disappear when background thread finishes loading
        progressBar.setVisibility(View.GONE);


        if (newsItems == null) {
            return;
        }
        if (newsItems.isEmpty()) {
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
