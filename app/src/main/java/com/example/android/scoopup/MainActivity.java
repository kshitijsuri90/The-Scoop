package com.example.android.scoopup;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.content.Loader;
import android.app.LoaderManager.LoaderCallbacks;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderCallbacks<List<News>>{

    private static final String TAG = "MainActivity";
    private static String REQUEST_URL = "http://content.guardianapis.com/search?";
    private static final String QUERY_ORDER_BY = "order-by";
    private static final String QUERY_FIELDS = "q";
    public static final String KEY_SHOW_FIELD = "show-fields";
    public static final String KEY_ALL = "all";
    private static final String QUERY_PAGES = "page-size";
    private static String SECTION_QUERY  = "";
    private static final String SECTION_NAME = "sectionName";
    private static final String SECTION_ID = "sectionId";
    private static final int LOADER_ID = 1;
    private TextView emptyTextView;
    private ProgressBar progressBar;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("The Scoop");
        toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        setTitle("");
        SECTION_QUERY = getIntent().getStringExtra("section").trim();
        emptyTextView = findViewById(R.id.fail_text);
        progressBar = findViewById(R.id.loading_spinner);

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();
            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(LOADER_ID, null, this);
        }
        else {
            // Otherwise, display error
            // First, hide loading indicator so error message will be visible
            progressBar.setVisibility(View.GONE);
            // Update empty state with no connection error message
            emptyTextView.setText(R.string.no_internet);
        }
    }

    @NonNull
    @Override
    public android.content.Loader<List<News>> onCreateLoader(int i, @Nullable Bundle bundle) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // getString retrieves a String value from the preferences. The second parameter is the default value for this preference.
        String category = sharedPreferences.getString(
                getString(R.string.settings_category_key),
                getString(R.string.settings_category_default));
        String orderBy = sharedPreferences.getString(getString(R.string.settings_order_by_key), getString(R.string.settings_order_by_default));

        // parse breaks apart the URI string that's passed into its parameter
        Uri baseUri = Uri.parse(REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter("api-key", "test");
        uriBuilder.appendQueryParameter(KEY_SHOW_FIELD,KEY_ALL);
        uriBuilder.appendQueryParameter("show-tags", "contributor");
        uriBuilder.appendQueryParameter(QUERY_PAGES, "20");

        assert category != null;
        if (!category.equals(getString(R.string.settings_category_default))) {
            uriBuilder.appendQueryParameter(QUERY_FIELDS, category);
            uriBuilder.appendQueryParameter(QUERY_ORDER_BY, "newest");
        }
        else{
            if(SECTION_QUERY.isEmpty()){
                uriBuilder.appendQueryParameter(QUERY_ORDER_BY, orderBy);
                uriBuilder.appendQueryParameter(QUERY_ORDER_BY,category);
            }
            else {
                uriBuilder.appendQueryParameter(SECTION_NAME,SECTION_QUERY);
                uriBuilder.appendQueryParameter(SECTION_ID,SECTION_QUERY);
                uriBuilder.appendQueryParameter(QUERY_FIELDS, SECTION_QUERY);
                uriBuilder.appendQueryParameter(QUERY_ORDER_BY, orderBy);

            }

        }


        return new NewsLoader(this, uriBuilder.toString());

    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> data) {
        Log.d(TAG, "onLoadFinished: executes load finished");
        updateUi((ArrayList<News>) data);
        emptyTextView.setText(R.string.no_search_results);
        progressBar.setVisibility(View.GONE);

    }


    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        updateUi(new ArrayList<News>());
    }

    private void updateUi(final ArrayList<News> news){
        // Find a reference to the {@link ListView} in the layout
        RecyclerView recyclerView =  findViewById(R.id.list);
        if(news.isEmpty()){
            recyclerView.setVisibility(View.GONE);
            emptyTextView.setVisibility(View.VISIBLE);
        }
        else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyTextView.setVisibility(View.GONE);
        }
        RecyclerNewsAdapter adapter = new RecyclerNewsAdapter(this,news);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        final LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_right);
        recyclerView.setLayoutAnimation(controller);
        recyclerView.setAdapter(adapter);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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
