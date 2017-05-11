package com.jawadjahangir.android.bookwatch;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.ads.MobileAds;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.zxing.integration.android.IntentIntegrator;
import com.jawadjahangir.android.bookwatch.search.SearchResultReceiver;
import com.jawadjahangir.android.bookwatch.search.SearchService;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class SearchInternetActivity extends AppCompatActivity implements SearchResultReceiver.Receiver{
    private static final String TAG = SearchInternetActivity.class.getSimpleName();

    private EditText titleSearch;
    private EditText authorSearch;
    private Button searchButton;
    private EditText isbnSearch;
    public SearchResultReceiver resultReceiver;
    private String searchQuery;
    private FirebaseAnalytics mFirebaseAnalytics;

    AlertDialog.Builder alertDialogBuilder;

    private ProgressBar mProgressBar;

    private String url = "https://www.googleapis.com/books/v1/volumes?q=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_internet);

        Toolbar toolbar = (Toolbar) findViewById(R.id.search_google_book_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle(getString(R.string.search_books));

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.GONE);

        titleSearch = (EditText) findViewById(R.id.title_search);
        authorSearch = (EditText) findViewById(R.id.search_author);
        isbnSearch = (EditText) findViewById(R.id.search_isbn);
        searchButton = (Button) findViewById(R.id.search_button);
        resultReceiver = new SearchResultReceiver(new Handler());
        resultReceiver.setReceiver(this);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        alertDialogBuilder = new AlertDialog.Builder(this, R.style.MyDialogTheme);
        alertDialogBuilder.setTitle(getString(R.string.no_network_available));
        alertDialogBuilder.setMessage(getString(R.string.wrong_with_network));
        alertDialogBuilder.setPositiveButton("OK", null);

        MobileAds.initialize(getApplicationContext(), "ca-app-pub-3940256099942544~3347511713");
    }

    public void searchBook(View view) {
        //Log.d(TAG, "Search button clicked");
        if (!isNetworkAvailable(this)) {
            alertDialogBuilder.create();
            alertDialogBuilder.show();
            return;
        }
        mProgressBar.setVisibility(View.VISIBLE);
        String title = titleSearch.getText().toString().trim();
        String author = authorSearch.getText().toString().trim();
        String isbn = isbnSearch.getText().toString().trim();
        String fullUrl = "";
        Bundle bundle = new Bundle();

        try {
            if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(author)) {
                fullUrl = url + "intitle:" + URLEncoder.encode(title, "UTF-8") + "+" + "inauthor:" +
                        URLEncoder.encode(author, "UTF-8") + "&key=" + BuildConfig.GOOGLE_BOOKS_API_KEY;
                searchQuery = "title: " + title + " & " + "author: " + author;
            } else if (TextUtils.isEmpty(title) && !TextUtils.isEmpty(author)) {
                fullUrl = url + "inauthor:" + URLEncoder.encode(author, "UTF-8") +
                        "&key=" + BuildConfig.GOOGLE_BOOKS_API_KEY;
                searchQuery = "author: " + author;
            } else if (!TextUtils.isEmpty(title) && TextUtils.isEmpty(author)) {
                fullUrl = url + "intitle:" + URLEncoder.encode(title, "UTF-8") +
                        "&key=" + BuildConfig.GOOGLE_BOOKS_API_KEY;
                searchQuery = "title: " + title;
            } else if (!TextUtils.isEmpty(isbn) && !TextUtils.isEmpty(title) && !TextUtils.isEmpty(author)) {
                fullUrl = url + "intitle:" + URLEncoder.encode(title, "UTF-8") + "+" + "inauthor:" +
                        URLEncoder.encode(author, "UTF-8") + "+" + "isbn:" +
                        URLEncoder.encode(isbn, "UTF-8") + "&key=" + BuildConfig.GOOGLE_BOOKS_API_KEY;
                searchQuery = "Title: " + title + ", Author name: " + author + ", and ISBN: " + isbn;
            } else if (!TextUtils.isEmpty(isbn) && !TextUtils.isEmpty(author)) {
                fullUrl = url + "inauthor:" + URLEncoder.encode(author, "UTF-8") + "+" + "isbn:" +
                        URLEncoder.encode(isbn, "UTF-8") + "&key=" + BuildConfig.GOOGLE_BOOKS_API_KEY;
                searchQuery = "Author name: " + author + " and ISBN: " + isbn;
            } else if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(isbn)) {
                fullUrl = url + "intitle:" + URLEncoder.encode(title, "UTF-8") + "+" + "isbn:" +
                        URLEncoder.encode(isbn, "UTF-8") + "&key=" + BuildConfig.GOOGLE_BOOKS_API_KEY;
                searchQuery = "Title: " + title + " and ISBN: " + isbn;
            } else if (!TextUtils.isEmpty(isbn)) {
                fullUrl = url + "isbn:" + URLEncoder.encode(isbn, "UTF-8") +
                        "&key=" + BuildConfig.GOOGLE_BOOKS_API_KEY;
                searchQuery = "ISBN: " + isbn;
            } else {
                Toast.makeText(this, getString(R.string.please_enter_something),
                        Toast.LENGTH_SHORT).show();
            }
        } catch (UnsupportedEncodingException e) {
            //Log.e(TAG, "Something went wrong encoding the title or author" + e.getMessage());
        }

        //Log.d(TAG, "url: " + fullUrl);

        //bundle.putString(FirebaseAnalytics.Param.ITEM_ID, id);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, searchQuery);
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "search query");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SEARCH, bundle);


        Intent intent = new Intent(Intent.ACTION_SYNC, null, this, SearchService.class);

        intent.putExtra("url", fullUrl);
        intent.putExtra("receiver", resultReceiver);
        intent.putExtra("requestId", 101);
        startService(intent);
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        switch (resultCode) {
            case SearchService.STATUS_RUNNING:
                //mProgressBar.setVisibility(View.VISIBLE);
                break;
            case SearchService.STATUS_FINISHED:
                mProgressBar.setVisibility(View.GONE);
//
                break;
            case SearchService.STATUS_ERROR:
                /* Handle the error */
                String error = resultData.getString(Intent.EXTRA_TEXT);
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
                break;
            case SearchService.STATUS_NO_RESULT:
                mProgressBar.setVisibility(View.GONE);
                alertDialogBuilder.setTitle(getString(R.string.no_results_available));
                alertDialogBuilder.setMessage(getString(R.string.no_result_due_to_wrong_spelling, searchQuery));
                alertDialogBuilder.create();
                alertDialogBuilder.show();
                break;
            case SearchService.STATUS_SERVER_DOWN:
                mProgressBar.setVisibility(View.GONE);
                alertDialogBuilder.setTitle(getString(R.string.no_results_available));
                alertDialogBuilder.setMessage(getString(R.string.server_down));
                alertDialogBuilder.create();
                alertDialogBuilder.show();
                break;
            case SearchService.STATUS_SERVER_INVALID:
                mProgressBar.setVisibility(View.GONE);
                alertDialogBuilder.setTitle(getString(R.string.no_results_available));
                alertDialogBuilder.setMessage(getString(R.string.server_invalid));
                alertDialogBuilder.create();
                alertDialogBuilder.show();
                break;
        }
    }

    static public boolean isNetworkAvailable(Context c) {
        ConnectivityManager cm =
                (ConnectivityManager)c.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main_menu, menu);
        MenuItem item = menu.findItem(R.id.local_search);
        MenuItemCompat.collapseActionView(item);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setMaxWidth(ViewGroup.LayoutParams.MATCH_PARENT);

        if (searchView != null) {
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    Intent intent = new Intent(SearchInternetActivity.this, LocalBookListActivity.class);
                    intent.putExtra("local_search", query);
                    startActivity(intent);
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    return false;
                }
            });
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if (id == R.id.action_search_internet){
            startActivity(new Intent(this, SearchInternetActivity.class));
            return true;
        } else if (id == R.id.scan_barcode) {
            IntentIntegrator integrator = new IntentIntegrator(this);
            integrator.setPrompt("Scan");
            integrator.initiateScan();
        }
        else if (id == R.id.all_books) {
            Intent intent = new Intent(this, LocalBookListActivity.class);
            startActivity(intent);
        } else if (id == R.id.all_shelves) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}
