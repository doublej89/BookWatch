package com.jawadjahangir.android.bookwatch;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jawadjahangir.android.bookwatch.data.BooksContract;
import com.jawadjahangir.android.bookwatch.dummy.SearchedBookContent;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;



public class DetailTab extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private EditText titleView;
    private EditText authorsView;
    private RatingBar avg_ratingsView;
    private EditText publisherView;
    private EditText publishedDateView;
    private EditText categoriesView;
    private EditText page_countView;
    private TextView descriptionView;
    private ImageView thumbnailView;
    private EditText isbnView;
    private TextView book_idView;
    private TextView small_thumbnailView;
    private TextView thumbnail_urlView;
    private Button addBookButton;

    private static final int SHELVES_LOADER = 0;

    static AlertDialog.Builder alertDialogBuilder;

    LinearLayout radiobuttonContainer;
    private SearchedBookContent.BookDetails mItem;

    private static final String TAG = "DetailFragment";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle("Choose the shelf you want to add the book to");


        radiobuttonContainer = new LinearLayout(getActivity());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        radiobuttonContainer.setLayoutParams(params);
        radiobuttonContainer.setOrientation(LinearLayout.VERTICAL);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_detail_tab, container, false);
        titleView = (EditText) rootView.findViewById(R.id.search_result_title);
        authorsView = (EditText) rootView.findViewById(R.id.search_result_author);
        avg_ratingsView = (RatingBar) rootView.findViewById(R.id.search_result_avg_ratings);
        publisherView = (EditText) rootView.findViewById(R.id.search_result_publisher);
        publishedDateView = (EditText) rootView.findViewById(R.id.search_result_published_date);
        categoriesView = (EditText) rootView.findViewById(R.id.search_result_categories);
        page_countView = (EditText) rootView.findViewById(R.id.search_result_page_count);
        descriptionView = (TextView) rootView.findViewById(R.id.search_result_description);
        thumbnailView = (ImageView) rootView.findViewById(R.id.search_result_thumbnail);
        isbnView = (EditText) rootView.findViewById(R.id.search_result_isbn);
        book_idView = (TextView) rootView.findViewById(R.id.book_id);
        small_thumbnailView = (TextView) rootView.findViewById(R.id.small_thumbnail);
        thumbnail_urlView = (TextView) rootView.findViewById(R.id.thumbnail_url);


        if (getArguments().containsKey(SearchedBookDetailFragment.ARG_ITEM_ID)) {
            FetchBookDetailTask fetchBookDetailTask = new FetchBookDetailTask();
            fetchBookDetailTask.execute(getArguments().getString(SearchedBookDetailFragment.ARG_ITEM_ID));
        }

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(SHELVES_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), BooksContract.ShelfEntry.CONTENT_URI,
                new String[]{BooksContract.ShelfEntry.COLUMN_SHELF_NAME}, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            do {
                int colIndex = data.getColumnIndex(BooksContract.ShelfEntry.COLUMN_SHELF_NAME);
                String shelfName = data.getString(colIndex);
                RadioButton radioButton = new RadioButton(getActivity());
                radioButton.setText(shelfName);
                if (radiobuttonContainer != null) {
                    radiobuttonContainer.addView(radioButton);
                }
            } while (data.moveToNext());
        }

        alertDialogBuilder.setView(radiobuttonContainer);
        alertDialogBuilder.setPositiveButton("Add",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        Cursor cursor = getActivity().getContentResolver().query(
                                BooksContract.SHELF_ITEM_ENTRY.buildBookWithTitleAndAuthorUri(
                                        titleView.getText().toString(), authorsView.getText().toString()),
                                null, null, null, null);
                        if (cursor != null && cursor.getCount() > 0) {
                            Toast.makeText(getActivity(), "You've already added this book",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            String shelfName = "";
                            for (int i = 0; i < radiobuttonContainer.getChildCount(); i++) {
                                if (((RadioButton) radiobuttonContainer.getChildAt(i)).isChecked()) {
                                    shelfName = ((RadioButton) radiobuttonContainer.getChildAt(i)).getText().toString();
                                    break;
                                }
                            }
                            ContentValues values = new ContentValues();
                            values.put(BooksContract.SHELF_ITEM_ENTRY.COLUMN_TITLE, titleView.getText().toString());
                            values.put(BooksContract.SHELF_ITEM_ENTRY.COLUMN_AUTHOR, authorsView.getText().toString());
                            values.put(BooksContract.SHELF_ITEM_ENTRY.COLUMN_BOOK_ID, book_idView.getText().toString());
                            if (!TextUtils.isEmpty(categoriesView.getText())) {
                                values.put(BooksContract.SHELF_ITEM_ENTRY.COLUMN_CATEGORIES, categoriesView.getText().toString());
                            }
                            if (!TextUtils.isEmpty(descriptionView.getText())) {
                                values.put(BooksContract.SHELF_ITEM_ENTRY.COLUMN_DESCRIPTION, descriptionView.getText().toString());
                            }
                            values.put(BooksContract.SHELF_ITEM_ENTRY.COLUMN_ISBN, isbnView.getText().toString());
                            values.put(BooksContract.SHELF_ITEM_ENTRY.COLUMN_PUBLISHER, publisherView.getText().toString());
                            values.put(BooksContract.SHELF_ITEM_ENTRY.COLUMN_PUBLISHED_DATE, publishedDateView.getText().toString());

                            values.put(BooksContract.SHELF_ITEM_ENTRY.COLUMN_AVERAGE_RATING, avg_ratingsView.getRating());
                            if (!TextUtils.isEmpty(page_countView.getText()))
                                values.put(BooksContract.SHELF_ITEM_ENTRY.COLUMN_PAGE_COUNT, Integer.parseInt(page_countView.getText().toString()));
                            if (!TextUtils.isEmpty(small_thumbnailView.getText()))
                                values.put(BooksContract.SHELF_ITEM_ENTRY.COLUMN_SMALL_THUMBNAIL, small_thumbnailView.getText().toString());
                            if (!TextUtils.isEmpty(thumbnail_urlView.getText()))
                                values.put(BooksContract.SHELF_ITEM_ENTRY.COLUMN_THUMBNAIL, thumbnail_urlView.getText().toString());

                            values.put(BooksContract.SHELF_ITEM_ENTRY.COLUMN_SHELF_NAME, shelfName);

                            if (UseTab.checkedRadio != null && UseTab.borrowerField != null) {
                                Log.d("Detail Tab", "use tab fields not empty");
                                int read_stat = 0;
                                if (UseTab.checkedRadio.getText().equals("Yes")) {
                                    read_stat = 1;
                                }
                                values.put(BooksContract.SHELF_ITEM_ENTRY.COLUMN_READ, read_stat);
                                values.put(BooksContract.SHELF_ITEM_ENTRY.COLUMN_LOANED_TO,
                                        UseTab.borrowerField.getText().toString());
                            }

                            getActivity().getContentResolver().insert(BooksContract.SHELF_ITEM_ENTRY.CONTENT_URI, values);
                            Toast.makeText(getActivity(), "You've added a book to " + shelfName,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        alertDialogBuilder.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getActivity().finish();
            }
        });
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }


    public class FetchBookDetailTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            fetchBookDetailFromApi(strings[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            if (mItem != null) {
                if (titleView != null && book_idView != null && authorsView != null && avg_ratingsView != null
                        && publishedDateView != null && publisherView != null && categoriesView != null &&
                        page_countView != null && descriptionView != null && isbnView != null &&
                        thumbnail_urlView != null && thumbnailView != null && small_thumbnailView != null) {
                    titleView.setText(mItem.title);
                    titleView.setVisibility(View.VISIBLE);
                    //Log.d("Detail Tab", titleView.getText().toString());
                    book_idView.setText(mItem.book_id);
                    String[] auth = mItem.authors;
                    String allAuths = "";
                    for (int i = 0; i < auth.length; i++) {
                        if (i == auth.length - 1) {
                            allAuths = allAuths + auth[i];
                        } else {
                            allAuths = allAuths + auth[i] + ", ";
                        }

                    }
                    if (!TextUtils.isEmpty(allAuths)) {
                        authorsView.setText(allAuths);
                    } else {
                        authorsView.setText(R.string.unknown_author);
                    }
                    avg_ratingsView.setRating((float) mItem.avgRatings);

                    if (!TextUtils.isEmpty(mItem.publisher)) {
                        publisherView.setText(mItem.publisher);
                    } else {
                        publisherView.setText(R.string.unknown_publisher);
                    }

                    if (!TextUtils.isEmpty(mItem.publishedDate)) {
                        publishedDateView.setText(mItem.publishedDate);
                    } else {
                        publishedDateView.setText(R.string.unavailable_publish_date);
                    }

                    String[] cats = mItem.categories;
                    String allCats = "";
                    if (cats != null) {
                        for (int i = 0; i < cats.length; i++) {
                            if (i == cats.length - 1) {
                                allCats = allCats + cats[i];
                            } else {
                                allCats = allCats + cats[i] + ", ";
                            }
                        }
                    }
                    if (!TextUtils.isEmpty(allCats)) {
                        categoriesView.setText(allCats);
                    } else {
                        categoriesView.setText(R.string.unknown_genera);
                    }
                    int page_count = mItem.pageCount;
                    if (page_count > 0) {
                        page_countView.setText(Integer.toString(page_count));
                    } else {
                        page_countView.setText(R.string.unavailable_page_count);
                    }

                    if (!TextUtils.isEmpty(mItem.description)) {
                        descriptionView.setText(mItem.description);
                    } else {
                        descriptionView.setText(R.string.unavailable_desc);
                    }

                    String[] identifiers = mItem.isbn;
                    String allIdentifiers = "";
                    if (identifiers != null) {
                        for (int i = 0; i < identifiers.length; i++) {
                            if (i == identifiers.length - 1) {
                                allIdentifiers = allIdentifiers + identifiers[i];
                            } else {
                                allIdentifiers = allIdentifiers + identifiers[i] + ", ";
                            }

                        }
                    }

                    if (!TextUtils.isEmpty(allIdentifiers)) {
                        isbnView.setText(allIdentifiers);
                    } else {
                        isbnView.setText(R.string.unavailable_isbn);
                    }

                    String thumbs = mItem.thumbnail;
                    if (!TextUtils.isEmpty(thumbs)) {
                        Picasso.with(getActivity()).load(thumbs).placeholder(R.drawable.no_cover_thumb).fit().into(thumbnailView);
                    } else {
                        thumbnailView.setImageResource(R.drawable.no_cover_thumb);
                    }
                    thumbnail_urlView.setText(thumbs);

                    small_thumbnailView.setText(mItem.smallThumbnail);
                }

            }
            super.onPostExecute(aVoid);
        }

        private void fetchBookDetailFromApi(String book_id){

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String reviewsJsonStr = null;

            try{
                String bookUrl = "https://www.googleapis.com/books/v1/volumes/" +  book_id +
                        "?key=AIzaSyCknsBvbMce05lynYZnMKe3y8IE7oKeTwA";

                URL url = new URL(bookUrl);
                //Log.d(TAG, "url connecting... " + url.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                if (inputStream == null){
                    
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
                return;
            } catch (JSONException e){
                //Log.e(TAG, "Error parsing JSON" + e.getMessage(), e);
                e.getStackTrace();
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
    }

    private void getBookFromJson(String bookJsonStr) throws JSONException{
        try {
            JSONObject volumeJSON = new JSONObject(bookJsonStr);

            String book_id = volumeJSON.getString("id");
            JSONObject volumeInfo = volumeJSON.getJSONObject("volumeInfo");
            String title = volumeInfo.getString("title");
            JSONArray authorArray = volumeInfo.getJSONArray("authors");


            double avgRatings = 0;

            if (volumeInfo.has("averageRating")) {
                avgRatings = volumeInfo.getDouble("averageRating");
            }

            //Log.d(TAG, "fetching book json " + book_id);

            String[] authors = new String[authorArray.length()];

            for (int i = 0; i < authorArray.length(); i++) {
                authors[i] = authorArray.getString(i);
            }

            String publisher = null;
            if (volumeInfo.has("publisher")) {
                publisher = volumeInfo.getString("publisher");
            }

            String publishedDate = null;
            if (volumeInfo.has("publisher")) {
                publishedDate = volumeInfo.getString("publishedDate");
            }

            String description = "";
            if (volumeInfo.has("description")) {
                description = volumeInfo.getString("description");
            }

            String[] isbn = null;

            if (volumeInfo.has("industryIdentifiers")) {
                JSONArray industryIdentifiers = volumeInfo.getJSONArray("industryIdentifiers");
                isbn = new String[industryIdentifiers.length()];

                for (int i = 0; i < industryIdentifiers.length(); i++) {
                    JSONObject isbnItem = industryIdentifiers.getJSONObject(i);
                    isbn[i] = isbnItem.getString("identifier");
                }
            }



            int pageCount = 0;
            if (volumeInfo.has("pageCount")) {
                pageCount = volumeInfo.getInt("pageCount");
            }

            String[] categories = null;
            if (volumeInfo.has("categories")) {
                JSONArray categoryArray = volumeInfo.getJSONArray("categories");

                if (categoryArray != null) {
                    categories = new String[categoryArray.length()];
                    for (int i = 0; i < categoryArray.length(); i++) {
                        categories[i] = categoryArray.getString(i);
                    }
                } else {
                    categories = new String[]{""};
                }
            }

            String thumbnail = "";
            String smallThumbnail = "";

            if (volumeInfo.has("imageLinks")) {
                JSONObject imageLinks = volumeInfo.getJSONObject("imageLinks");
                if (imageLinks != null) {
                    smallThumbnail = imageLinks.getString("smallThumbnail");
                    thumbnail = imageLinks.getString("thumbnail");
                } else {
                    smallThumbnail = "";
                    thumbnail = "";
                }
            }

            mItem = new SearchedBookContent.BookDetails(book_id, title, authors, thumbnail,
                    avgRatings, publisher, publishedDate, description, isbn, pageCount, categories, smallThumbnail );
            //Log.d("Book Details Creation", mItem.toString());


        }catch (JSONException e){
            //Log.e(TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }
}
