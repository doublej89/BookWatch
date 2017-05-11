package com.jawadjahangir.android.bookwatch.search;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.ResultReceiver;
import android.text.TextUtils;

import com.jawadjahangir.android.bookwatch.SearchedBookListActivity;
import com.jawadjahangir.android.bookwatch.dummy.SearchedBookContent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by MeMyself on 3/6/2017.
 */
public class SearchService extends IntentService{
    private static final String TAG = "SearchService";

    public static final int STATUS_RUNNING = 0;
    public static final int STATUS_FINISHED = 1;
    public static final int STATUS_ERROR = 2;
    public static final int STATUS_NO_RESULT = 3;
    public static final int STATUS_SERVER_DOWN = 4;
    public static final int STATUS_SERVER_INVALID = 5;

    ResultReceiver receiver;


    private List<SearchedBookContent.Book> searchedResults;

    public SearchService() {
        super(SearchService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //Log.d(TAG, "Service Started!");

        receiver = intent.getParcelableExtra("receiver");
        String booksUrl = null;

        if (intent.hasExtra("url"))
            booksUrl = intent.getStringExtra("url");
        else if (intent.hasExtra("barcode_scan")) {
            String isbn = intent.getStringExtra("barcode_scan");
            if (!TextUtils.isEmpty(isbn)) {
                try {
                    booksUrl = "https://www.googleapis.com/books/v1/volumes?q=isbn:" +
                            URLEncoder.encode(isbn, "UTF-8") + "&key=AIzaSyCknsBvbMce05lynYZnMKe3y8IE7oKeTwA";
                } catch (UnsupportedEncodingException e) {
                    //Log.e(TAG, "Something went wrong encoding the isbn" + e.getMessage());
                }
            }
        }
        //Log.d(TAG, "booksUrl " + booksUrl);
        Bundle bundle = new Bundle();
        Intent resultIntent = new Intent(getBaseContext(), SearchedBookListActivity.class);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        if (!TextUtils.isEmpty(booksUrl)) {
            /* Update UI: Download Service is Running */
            if (!intent.hasExtra("barcode_scan"))
                receiver.send(STATUS_RUNNING, Bundle.EMPTY);
            searchedResults = new ArrayList<SearchedBookContent.Book>();

            try {
                fetchBookFromApi(booksUrl);

                if (searchedResults.size() > 0) {
                    //bundle.putParcelableArrayList("");
                    resultIntent.putParcelableArrayListExtra("search_results",
                            (ArrayList<? extends Parcelable>) searchedResults);
                    getApplication().startActivity(resultIntent);
                } else {
                    receiver.send(STATUS_NO_RESULT, Bundle.EMPTY);
                }
            } catch (Exception e) {
                //Log.d(TAG, "No data was received! " + book_id + " " + title + " " + Arrays.toString(isbn));

                /* Sending error message back to activity */
                bundle.putString(Intent.EXTRA_TEXT, e.toString());
                receiver.send(STATUS_ERROR, bundle);
            }
        }
        //Log.d(TAG, "Service Stopping!");
        this.stopSelf();
    }

    private void fetchBookFromApi(String booksUrl){

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String reviewsJsonStr = null;

        try{

            URL url = new URL(booksUrl);
            //Log.d(TAG, "url connecting... " + url.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            if (inputStream == null){
               // Log.d(TAG, "There is no input stream!");
                return;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuffer buffer = new StringBuffer();

            String line;
            while ((line = reader.readLine()) != null){
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0){
                return;
            }
            reviewsJsonStr = buffer.toString();
            //Log.d(TAG, "bookJSON: " + reviewsJsonStr);
            getBookFromJson(reviewsJsonStr);
        } catch (IOException e){
            //Log.d(TAG, "Error IO", e);
            receiver.send(STATUS_SERVER_DOWN, Bundle.EMPTY);
        } catch (JSONException e){
            //Log.e(TAG, "Error parsing JSON" + e.getMessage(), e);
            e.getStackTrace();
            receiver.send(STATUS_SERVER_INVALID, Bundle.EMPTY);
        } finally {
            if (urlConnection != null){
                urlConnection.disconnect();
            }
            if (reader != null){
                try{
                    reader.close();
                } catch (final IOException e){
                    //Log.e(TAG, "Error closing stream!", e);
                }
            }
        }
        return;
    }

    private void getBookFromJson(String reviewsJsonStr) throws JSONException{
        try {
            JSONObject jsonObject = new JSONObject(reviewsJsonStr);
            int totalItems = jsonObject.getInt("totalItems");
            if (totalItems > 0) {
                JSONArray itemsArray = jsonObject.getJSONArray("items");

                for (int i = 0; i < itemsArray.length(); i++) {
                    JSONObject volumeJSON = itemsArray.getJSONObject(i);
                    String book_id = volumeJSON.getString("id");
                    JSONObject volumeInfo = volumeJSON.getJSONObject("volumeInfo");
                    String title = volumeInfo.getString("title");
                    JSONArray authorArray = new JSONArray();
                    if (volumeInfo.has("authors")) {
                        authorArray = volumeInfo.getJSONArray("authors");
                    }
//
                    String authors = "";

                    for (int j = 0; j < authorArray.length(); j++) {
                        if (j < authorArray.length() - 1)
                            authors += authorArray.getString(j) + ", ";
                        else
                            authors += authorArray.getString(j);
                    }

                    double avgRatings = 0;

                    if (volumeInfo.has("averageRating")) {
                        avgRatings = volumeInfo.getDouble("averageRating");
                    }

                    String smallThumbnail = "";

                    if (volumeInfo.has("imageLinks")) {
                        JSONObject imageLinks = volumeInfo.getJSONObject("imageLinks");
                        smallThumbnail = imageLinks.getString("smallThumbnail");
                    }

                    SearchedBookContent.Book book = new SearchedBookContent.Book(book_id, title, authors,
                            smallThumbnail, avgRatings);

                    searchedResults.add(book);

                }
            }
        }catch (JSONException e){
            //Log.e(TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }

}
