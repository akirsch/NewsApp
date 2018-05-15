package com.example.android.newsapp;

import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 * Helper methods related to requesting and receiving news data from Guardian API.
 */
public final class QueryUtils {

    /** Tag for the log messages */
    public static final String LOG_TAG = QueryUtils.class.getSimpleName();

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    /**
     * Query the Guardian API and return a {@link ArrayList<NewsItem>} object to represent an array of news items.
     */
    public static ArrayList<NewsItem> fetchNewsItemData(String requestUrl) {
        Log.v(QueryUtils.class.getName(), "fetchNewsItemData called");

        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error closing input stream", e);
        }

        // Extract relevant fields from the JSON response and create an {@link ArrayList<NewsItem>} object
        ArrayList<NewsItem> latestNewsItems = extractEarthquakes(jsonResponse);

        // Return the {@link Event}
        return latestNewsItems;

    }

    /**
     * Return a list of {@link NewsItem} objects that has been built up from
     * parsing a JSON response.
     */
    public static ArrayList<NewsItem> extractEarthquakes(String jsonResponse) {

        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(jsonResponse)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding news items to
        ArrayList<NewsItem> newsItems = new ArrayList<>();

        // Try to parse the jsonResponse. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {
            // get root JSONObject
            JSONObject jsonRootObject = new JSONObject(jsonResponse);

            // get object with key value "response" which contains array with news item data.
            JSONObject jsonResponseObject = jsonRootObject.getJSONObject("response");

            //Get the instance of JSONArray that contains JSONObjects containing the news item data
            JSONArray jsonArray = jsonResponseObject.getJSONArray("results");

            //Iterate the jsonArray and print the info of JSONObjects
            for(int i=0; i < jsonArray.length(); i++) {

                // get current object in results Array
                JSONObject currentNewsItemJSONObject = jsonArray.getJSONObject(i);

                // get "fields" object from the current news item object in the Array
                JSONObject fieldsJSONObject = currentNewsItemJSONObject.getJSONObject("fields");

                // get data for this news item in order that they are passed into NewsItem constructor

                String category = currentNewsItemJSONObject.optString("sectionName");
                String headline = fieldsJSONObject.optString("headline");
                String trailTextHtml = fieldsJSONObject.optString("trailText");
                // remove html tags from this string
                String trailText = Html.fromHtml(trailTextHtml).toString();
                String author = fieldsJSONObject.optString("byline");
                String date = currentNewsItemJSONObject.optString("webPublicationDate");
                String webUrl = currentNewsItemJSONObject.optString("webUrl");
                String thumbnailUrl = fieldsJSONObject.optString("thumbnail");

                // create new News Item object with these values as parameters and pass it into
                // the earthquakes array
                newsItems.add(new NewsItem(category, headline, trailText, author, date, webUrl, thumbnailUrl));

            }

            // Return the list of news items
            return newsItems;

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
        }

        return null;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }



}
