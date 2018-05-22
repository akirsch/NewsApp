package com.example.android.newsapp;
import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.text.ParseException;
import java.util.Date;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class NewsItemAdapter extends
        RecyclerView.Adapter<NewsItemAdapter.ViewHolder> {

    // Store a member variable for the News Items Array
    final private List<NewsItem> mNewsItems;
    final Context mContext;

    // Pass in the news items array into the constructor
    public NewsItemAdapter(Context context, List<NewsItem> newsItems) {
        mNewsItems = newsItems;
        mContext = context;
    }

    @NonNull
    @Override
    public NewsItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View newsItemView = inflater.inflate(R.layout.list_item, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(newsItemView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(@NonNull NewsItemAdapter.ViewHolder viewHolder, final int position) {
        // Get the data model based on position
        final NewsItem currentNewsItem = mNewsItems.get(position);


        // Set item views based on your views and data model
        final ImageView imageView = viewHolder.newsImageView;

        // in order to use centerCrop() we must first write this code in Glide version 4 and up
        RequestOptions options = new RequestOptions();
        options.centerCrop();

        // use Glide to get image from url and put it in image view
        Glide.with(mContext)
                .load(currentNewsItem.getThumbnailUrl())
                .apply(options)
                .into(imageView);


        TextView headlineView = viewHolder.headlineView;
        headlineView.setText(currentNewsItem.getHeadline());

        TextView trailTextView = viewHolder.trailTextView;
        trailTextView.setText(currentNewsItem.getTrailText());

        TextView categoryView = viewHolder.categoryView;
        categoryView.setText(currentNewsItem.getCategory());

        TextView authorView = viewHolder.authorView;
        authorView.setText(currentNewsItem.getAuthor());

        TextView dateView = viewHolder.dateView;

        String dateOfArticle = formatDate(currentNewsItem.getDate());

        dateView.setText(dateOfArticle);


        // create click listener which will open the url of the news story that user clicks on
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = view.getContext();

                // get url of news item
                String webUrl = currentNewsItem.getWebUrl();

                // convert the url into a uri to use in an intent
                Uri webpage = Uri.parse(webUrl);

                // create implicit intent to open a web browser to show the url
                Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
                if (intent.resolveActivity(context.getPackageManager()) != null) {
                    context.startActivity(intent);
                }
            }
        });

    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mNewsItems.size();
    }

    /**
     * This method format the date into a specific pattern.
     *
     * @param dateObj is the web publication date.
     * @return a date formatted's string.
     */
    private String formatDate(String dateObj) {
        String dateFormatted = "";
        SimpleDateFormat inputDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
        SimpleDateFormat outputDate = new SimpleDateFormat("EEEE, dd.MM.yyyy", Locale.getDefault());
        try {
            Date newDate = inputDate.parse(dateObj);
            return outputDate.format(newDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateFormatted;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {


        // Provide a direct reference to each of the views within a data item
        // Used to cache the views within the item layout for fast access
        public final View view;
        public final ImageView newsImageView;
        public final TextView headlineView;
        public final TextView trailTextView;
        public final TextView categoryView;
        public final TextView authorView;
        public final TextView dateView;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);
            view = itemView;
            newsImageView = itemView.findViewById(R.id.news_image);
            headlineView = itemView.findViewById(R.id.headline_text);
            trailTextView = itemView.findViewById(R.id.trail_text);
            categoryView = itemView.findViewById(R.id.category_text);
            authorView = itemView.findViewById(R.id.byline_text);
            dateView = itemView.findViewById(R.id.date_view);
        }

    }


}


