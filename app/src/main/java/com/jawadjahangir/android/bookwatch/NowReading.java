package com.jawadjahangir.android.bookwatch;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.jawadjahangir.android.bookwatch.data.BooksContract;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class NowReading extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{
    private TextView latestPageView;

    AlertDialog.Builder alertDialogBuilder;
    private LinearLayout cardContainer;
    SimpleDateFormat simpleDateFormat;
    Calendar calander;

    private int BOOKS_LOADER = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_now_reading);

        Toolbar toolbar = (Toolbar) findViewById(R.id.now_reading_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle(getString(R.string.now_reading));

        cardContainer = (LinearLayout) findViewById(R.id.now_reading_container);

        calander = Calendar.getInstance();
        simpleDateFormat = new SimpleDateFormat("EEE, d MMM yyyy 'at' HH:mm:ss Z");



        getSupportLoaderManager().initLoader(BOOKS_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, BooksContract.SHELF_ITEM_ENTRY.buildBookWithShelfNameUri("Reading"),
                    new String[]{BooksContract.SHELF_ITEM_ENTRY.COLUMN_THUMBNAIL,
                            BooksContract.SHELF_ITEM_ENTRY.COLUMN_TITLE, BooksContract.SHELF_ITEM_ENTRY.COLUMN_AUTHOR,
                            BooksContract.SHELF_ITEM_ENTRY.COLUMN_PAGE_COUNT,
                            BooksContract.SHELF_ITEM_ENTRY.COLUMN_CURRENT_PAGE,
                            BooksContract.SHELF_ITEM_ENTRY.COLUMN_DATE_TIME,
                            BooksContract.SHELF_ITEM_ENTRY._ID
                            }, null, null, null);

    }

    public static final int COL_THUMBNAIL_INDEX = 0;
    public static final int COL_TITLE_INDEX = 1;
    public static final int COL_AUTHOR_INDEX = 2;
    public static final int COL_PAGE_COUNT_INDEX= 3;
    public static final int COL_CUR_PAGE_INDEX = 4;
    public static final int COL_DATE_TIME_INDEX = 5;
    public static final int COL_ID_INDEX = 6;

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            do {
                int cur_page_number = data.getInt(COL_CUR_PAGE_INDEX);
                String title = data.getString(COL_TITLE_INDEX);
                String author = data.getString(COL_AUTHOR_INDEX);
                String thumb = data.getString(COL_THUMBNAIL_INDEX);
                final int page_count = data.getInt(COL_PAGE_COUNT_INDEX);
                int _id = data.getInt(COL_ID_INDEX);
                String dateTime = data.getString(COL_DATE_TIME_INDEX);

                View cardView = getLayoutInflater().inflate(R.layout.now_reading_item, cardContainer, false);

                TextView titleView = (TextView) cardView.findViewById(R.id.title);
                titleView.setText(title);
                ImageView bookCover = (ImageView) cardView.findViewById(R.id.thumbnail);
                if (!TextUtils.isEmpty(thumb)) {
                    Picasso.with(this).load(thumb).fit().into(bookCover);
                } else {
                    bookCover.setImageResource(R.drawable.no_cover_thumb);
                }
                TextView authorName = (TextView) cardView.findViewById(R.id.author_name);
                authorName.setText(author);
                TextView pageCountView = (TextView) cardView.findViewById(R.id.page_count);
                pageCountView.setText(Integer.toString(page_count));

                final TextView idView = (TextView) cardView.findViewById(R.id.id_view);
                idView.setText(Integer.toString(_id));

                latestPageView = (TextView) cardView.findViewById(R.id.latest_page_number);
                latestPageView.setText(Integer.toString(cur_page_number));

                final TextView dateTimeView = (TextView) cardView.findViewById(R.id.date_time);
                if (!TextUtils.isEmpty(dateTime)) {
                    dateTimeView.setText(dateTime);
                } else {
                    dateTimeView.setText(getString(R.string.not_bookmarked_yet));
                }

                EditText updateField = (EditText) cardView.findViewById(R.id.update_field);

                updateField.setOnKeyListener(new View.OnKeyListener() {
                    @Override
                    public boolean onKey(View view, int keyCode, KeyEvent event) {
                        EditText myEditText = (EditText) view;
                        int latest_page_number = 0;
                        String regex = "\\d+";
                        if (myEditText.getText().toString().matches(regex)) {
                            latest_page_number = Integer.parseInt(myEditText.getText().toString());
                        }

                        if (keyCode == EditorInfo.IME_ACTION_SEARCH ||
                                keyCode == EditorInfo.IME_ACTION_DONE ||
                                event.getAction() == KeyEvent.ACTION_DOWN &&
                                        event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {

                            if (!event.isShiftPressed()) {
                                //Log.v("AndroidEnterKeyActivity","Enter Key Pressed!");
                                if (latest_page_number > page_count) {
                                    Toast.makeText(NowReading.this, getString(R.string.page_number_limit),
                                            Toast.LENGTH_SHORT).show();
                                    return true;
                                } else if (latest_page_number < 0) {
                                    Toast.makeText(NowReading.this, getString(R.string.non_negative_page_number),
                                            Toast.LENGTH_SHORT).show();
                                    return true;
                                }
                                int id = Integer.parseInt(idView.getText().toString());
                                String time = simpleDateFormat.format(calander.getTime());
                                ContentValues values = new ContentValues();
                                values.put(BooksContract.SHELF_ITEM_ENTRY.COLUMN_CURRENT_PAGE, latest_page_number);
                                values.put(BooksContract.SHELF_ITEM_ENTRY.COLUMN_DATE_TIME, time);
                                getContentResolver().update(BooksContract.SHELF_ITEM_ENTRY.buildBookDbIdUri(id),
                                        values, null, null);
                                Toast.makeText(NowReading.this, getString(R.string.bookmark_updated),
                                        Toast.LENGTH_SHORT).show();
                                latestPageView.setText(Integer.toString(latest_page_number));
                                dateTimeView.setText(time);

                                ReadingProgressWidgetService.startActionUpdateWidget(NowReading.this);
                                return true;
                            }

                        }
                        return false; // pass on to other listeners.

                    }
                });

                cardContainer.addView(cardView);


            } while (data.moveToNext());
        } else {
            TextView emptyView = (TextView) findViewById(R.id.recyclerview_book_no_reading);
            emptyView.setVisibility(View.VISIBLE);
            emptyView.setText(getString(R.string.no_reading_message));
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

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
                    Intent intent = new Intent(NowReading.this, LocalBookListActivity.class);
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
