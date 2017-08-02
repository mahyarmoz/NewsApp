package com.zenkaiengineeringsolutions.p8newsapp;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.GONE;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<News>>, SearchView.OnQueryTextListener {

    //public static final String LOG_TAG = MainActivity.class.getSimpleName();
    public static final String API_KEY = "3a9e0c70-08b8-4a4a-a7de-3b2144668636";
    public static final String SEARCH_TERM = "searchQuery";

    NewsAdapter newsAdapter;
    @BindView(R.id.gridview) GridView gridView;
    @BindView(R.id.empty_textview) TextView emptyView;
    @BindView(R.id.loading_spinner) ProgressBar loadingSpinner;
    @BindView(R.id.swipe_refresh) SwipeRefreshLayout mySRL;

    // Variables for the Search View/Menu Implementation
    MenuItem searchMenuItem;
    SearchView searchView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        gridView.setEmptyView(emptyView);

        // Try to fetch data only if there's a connection
        if (isConnected()) {
            loadingSpinner.setVisibility(GONE);
            emptyView.setText("Search Something to Show Results Here");
        }

        else{
            loadingSpinner.setVisibility(GONE);
            emptyView.setText("No Internet Connection");
        }

        mySRL.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });

    }

    // Refresh function to be called upon swiping (called in a separate function as onRefresh couldn't pass 'this' into the Loadercall
    private void refresh(){
        getLoaderManager().restartLoader(0, null, this);
        mySRL.setRefreshing(false);
    }

    // Function to check connectivity (called at the start and also before every search)
    private boolean isConnected(){
        ConnectivityManager cm = (ConnectivityManager)getBaseContext().getSystemService(Context.CONNECTIVITY_SERVICE);       // Should it be get Application Context or getBaseContext, both work when I try
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return (activeNetwork != null && activeNetwork.isConnectedOrConnecting());
    }

    @Override
    public Loader<List<News>> onCreateLoader(int id, Bundle args) {
        if (args==null||args.isEmpty())
            return null;
        else {
            String searchTerm = args.getString(SEARCH_TERM);
            String fullURL = QueryUtils.createFullPath(searchTerm);
            return new NewsLoader(this, fullURL);
        }
    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> data) {
        loadingSpinner.setVisibility(GONE);

        if (data == null || data.isEmpty()) {
            emptyView.setText("No Data to Display");
            return;
        }
        updateUi(data);
    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {

    }

    private void updateUi(List<News> data){
        final List<News> newsList = data;

        newsAdapter = new NewsAdapter(this, newsList);
        gridView.setAdapter(newsAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                News newsItem = newsList.get(position);

                Intent intent = new Intent (Intent.ACTION_VIEW);
                intent.setData(Uri.parse(newsItem.getUrl()));
                startActivity(intent);

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        searchMenuItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchMenuItem.getActionView();

        // Setting the listener on the SearchView
        searchView.setOnQueryTextListener(this);

        return true;
    }


    // This method will be executed as soon as the user clicks on
    // the search button on the keyboard
    @Override
    public boolean onQueryTextSubmit(String query) {

        // Clear the old results if there's any
        if (newsAdapter != null)
            newsAdapter.clear();

        // Only Perform the search if there's an internet  connection
        if (isConnected()) {
            Toast.makeText(this, "Searching for " + query, Toast.LENGTH_SHORT).show();
            Bundle inputs = new Bundle();
            inputs.putString(SEARCH_TERM, query);
            emptyView.setText("");
            loadingSpinner.setVisibility(View.VISIBLE);
            getLoaderManager().restartLoader(0, inputs, this);
        }

        else{
            loadingSpinner.setVisibility(GONE);
            emptyView.setText("No Internet Connection");
        }

        // clear search bar & collapse the search box back to the menu icon
        searchView.setQuery("", false);
        searchView.setIconified(true);

        // clear the focus of the SearchView and
        View current = getCurrentFocus();
        if (current != null)
            current.clearFocus();

        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

}
