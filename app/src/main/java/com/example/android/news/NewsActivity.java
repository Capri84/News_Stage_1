package com.example.android.news;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class NewsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<News>> {

    // URL for requesting news data
    private static final String NEWS_REQUEST_URL =
            "http://content.guardianapis.com/search";

    private static final String PAGE_SIZE = "page-size";
    private static final String API_KEY = "api-key";
    private static final String KEY = "19d64fde-9a83-435e-9378-a43e6778b553";
    private static final String SHOW_FIELDS = "show-fields";
    private static final String THUMBNAIL_TRAIL_TEXT_HEADLINE = "thumbnail,trailText,headline";
    private static final String SHOW_TAGS = "show-tags";
    private static final String CONTRIBUTOR = "contributor";
    private static final String ORDER_BY = "order-by";

    /**
     * Constant value for the news loader ID. We can choose any integer.
     * This really only comes into play if you're using multiple loaders.
     */
    private static final int NEWS_LOADER_ID = 1;
    private NewsAdapter adapter;
    private NewsLoader loader;
    private TextView emptyStateTextView;
    private ImageView emptyStateImageView;
    private SwipeRefreshLayout refreshLayout;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private ConnectivityManager cm;
    private NetworkInfo activeNetwork;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        initViews();

        // Get a reference to the LoaderManager, in order to interact with loaders.
        LoaderManager loaderManager = getLoaderManager();
        // Initialize the loader. Pass in the int ID constant defined above and pass in null for
        // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
        // because this activity implements the LoaderCallbacks interface).
        loader = (NewsLoader) loaderManager.initLoader(NEWS_LOADER_ID, null, this);

        specifyAdapter();
        refresh();

        //RefreshLayout
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        getNetworkState();
        if (activeNetwork != null && activeNetwork.isConnected()) {
            //do nothing
        } else {
            Toast.makeText(getApplicationContext(), R.string.no_internet_connection,
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public Loader<List<News>> onCreateLoader(int i, Bundle bundle) {
        // Create a new loader for the given URL
        return new NewsLoader(this, buildQuery());
    }

    public void onLoadFinished(Loader<List<News>> loader, List<News> newsList) {
        progressBar.setVisibility(View.GONE);
        // Clear the adapter of previous news data
        adapter.clear();
        // If there is a valid list of {@link News}, then add them to the adapter's data set.
        if (newsList != null && !newsList.isEmpty()) {
            adapter.addAll(newsList);
            setViewsInvisible();
        } else {
            if (activeNetwork != null && activeNetwork.isConnected()) {
                setNoDataState();
            } else {
                setNoConnectionState();
            }
        }
        refreshLayout.setRefreshing(false);
    }

    public void onLoaderReset(Loader<List<News>> loader) {
        // Loader reset, so we can clear out our existing data.
        adapter.clear();
    }

    private void initViews() {
        progressBar = findViewById(R.id.loading_spinner);
        emptyStateTextView = findViewById(R.id.empty_text_view);
        emptyStateImageView = findViewById(R.id.empty_image_view);
        refreshLayout = findViewById(R.id.swipe);
        refreshLayout.setActivated(true);
        refreshLayout.setColorSchemeColors(Color.RED, Color.GREEN, Color.BLUE, Color.CYAN);
        recyclerView = findViewById(R.id.news_list);
        setViewsInvisible();
    }

    private void setNoConnectionState() {
        emptyStateTextView.setText(R.string.no_internet_connection);
        emptyStateImageView.setImageResource(R.drawable.no_internet_access);
        setViewsVisible();
    }

    private void setNoDataState() {
        emptyStateTextView.setText(R.string.no_news);
        emptyStateImageView.setImageResource(R.drawable.no_data_found);
        setViewsVisible();
    }

    private void setViewsInvisible() {
        emptyStateTextView.setVisibility(View.INVISIBLE);
        emptyStateImageView.setVisibility(View.INVISIBLE);
    }

    private void setViewsVisible() {
        emptyStateTextView.setVisibility(View.VISIBLE);
        emptyStateImageView.setVisibility(View.VISIBLE);
    }

    private void specifyAdapter() {
        // use a linear layout manager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        // specify an adapter
        adapter = new NewsAdapter(getApplicationContext(), new ArrayList<News>());
        recyclerView.setAdapter(adapter);
    }

    private void getNetworkState() {
        // Get a reference to the ConnectivityManager to check state of network connectivity
        cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        // Get details on the currently active default data network
        activeNetwork = cm.getActiveNetworkInfo();
    }

    private void refresh() {
        getNetworkState();
        // If there is a network connection, fetch data
        if (activeNetwork != null && activeNetwork.isConnected()) {
            adapter.clear();
            loader.setUrl(buildQuery());
            loader.forceLoad();
            recyclerView.setVisibility(View.VISIBLE);
        } else {
            refreshLayout.setRefreshing(false);
            if (emptyStateTextView.getVisibility() == View.VISIBLE &&
                    emptyStateTextView.getText().equals(getString(R.string.no_internet_connection))) {
                //do nothing
            } else {
                Toast.makeText(getApplicationContext(), R.string.no_internet_connection,
                        Toast.LENGTH_SHORT).show();
                if (emptyStateImageView.isShown()) {
                    setNoConnectionState();
                }
            }
        }
    }

    private String buildQuery() {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        // parse breaks apart the URI string that's passed into its parameter
        Uri baseUri = Uri.parse(NEWS_REQUEST_URL);
        // buildUpon prepares the baseUri that we just parsed so we can add query parameters to it
        Uri.Builder uriBuilder = baseUri.buildUpon();

        /* getString retrieves a String value from the preferences. The second parameter is
           the default value for this preference.*/
        String pageSize = sharedPrefs.getString(
                getString(R.string.settings_page_size_key),
                getString(R.string.settings_page_size_default));
        if (TextUtils.isEmpty(pageSize)) {
            pageSize = "0";
        }

        String orderBy = sharedPrefs.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default)
        );

        // Append query parameter and its value.
        uriBuilder.appendQueryParameter(PAGE_SIZE, pageSize);
        uriBuilder.appendQueryParameter(API_KEY, KEY);
        uriBuilder.appendQueryParameter(SHOW_FIELDS, THUMBNAIL_TRAIL_TEXT_HEADLINE);
        uriBuilder.appendQueryParameter(SHOW_TAGS, CONTRIBUTOR);
        uriBuilder.appendQueryParameter(ORDER_BY, orderBy);

        return uriBuilder.toString();
    }

    @Override
    // This method initialize the contents of the Activity's options menu.
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the Options Menu we specified in XML
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}