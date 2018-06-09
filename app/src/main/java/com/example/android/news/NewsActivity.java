package com.example.android.news;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class NewsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<News>> {

    public static final String LOG_TAG = NewsActivity.class.getName();

    // URL for requesting news data
    private static final String NEWS_REQUEST_URL =
            "http://content.guardianapis.com/search?page-size=100&api-key=test&" +
                    "show-fields=thumbnail%2CtrailText%2Cbyline%2Cheadline&order-by=newest";

    // URL for requesting data by search
  /*  private static final String NEWS_REQUEST_SEARCH_URL =
            "http://content.guardianapis.com/search?page-size=100&api-key=test&" +
                    "show-fields=thumbnail%2CtrailText%2Cbyline%2Cheadline&order-by=relevance&q=test";*/
    /**
     * Constant value for the news loader ID. We can choose any integer.
     * This really only comes into play if you're using multiple loaders.
     */
    private static final int NEWS_LOADER_ID = 1;
    private NewsAdapter adapter;
    private RecyclerView recyclerView;
    /**
     * TextView that is displayed when the list is empty
     */
    private TextView emptyStateTextView;
    private ImageView emptyStateImageView;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        progressBar = findViewById(R.id.loading_spinner);
        emptyStateTextView = findViewById(R.id.empty_text_view);
        emptyStateImageView = findViewById(R.id.empty_image_view);

        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        // If there is a network connection, fetch data
        if (activeNetwork != null && activeNetwork.isConnected()) {
            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();
            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(NEWS_LOADER_ID, null, this);
        } else {
            progressBar.setVisibility(View.GONE);
            // Update empty state with no connection error message
            emptyStateTextView.setText(R.string.no_internet_connection);
            emptyStateImageView.setImageResource(R.drawable.ic_launcher_foreground);
        }

        recyclerView = (RecyclerView) findViewById(R.id.news_list);
        /* use this setting to improve performance if you know that changes
           in content do not change the layout size of the RecyclerView */
        //   recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        // specify an adapter
        adapter = new NewsAdapter(getApplicationContext(), new ArrayList<News>());
        recyclerView.setAdapter(adapter);
    }

    public Loader<List<News>> onCreateLoader(int i, Bundle bundle) {
        // Create a new loader for the given URL
        return new NewsLoader(this, NEWS_REQUEST_URL);
    }

    public void onLoadFinished(Loader<List<News>> loader, List<News> newsList) {
        progressBar.setVisibility(View.GONE);
        // Clear the adapter of previous news data
        adapter.clear();
        // If there is a valid list of {@link News}, then add them to the adapter's
        // data set.
        if (newsList != null && !newsList.isEmpty()) {
            adapter.addAll(newsList);
        }
        emptyStateTextView.setText(R.string.no_news);
        emptyStateImageView.setImageResource(R.drawable.ic_launcher_foreground);
    }

    public void onLoaderReset(Loader<List<News>> loader) {
        // Loader reset, so we can clear out our existing data.
        adapter.clear();
    }
}