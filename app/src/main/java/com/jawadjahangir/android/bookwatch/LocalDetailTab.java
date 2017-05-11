package com.jawadjahangir.android.bookwatch;


import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.jawadjahangir.android.bookwatch.data.BooksContract;
import com.squareup.picasso.Picasso;


/**
 * A simple {@link Fragment} subclass.
 */
public class LocalDetailTab extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public TextView titleView;
    public TextView authorsView;
    public RatingBar avg_ratingsView;
    public TextView publisherView;
    public TextView publishedDateView;
    public TextView categoriesView;
    public TextView page_countView;
    public TextView descriptionView;
    public ImageView thumbnailView;
    public TextView isbnView;
    private TextView book_idView;
    private TextView small_thumbnailView;
    private TextView thumbnail_urlView;

    private int _id = -1;

    public static final String[] BOOK_COLUMNS = {
            BooksContract.SHELF_ITEM_ENTRY.COLUMN_TITLE,
            BooksContract.SHELF_ITEM_ENTRY.COLUMN_AUTHOR,
            BooksContract.SHELF_ITEM_ENTRY.COLUMN_AVERAGE_RATING,
            BooksContract.SHELF_ITEM_ENTRY.COLUMN_SHELF_NAME,
            BooksContract.SHELF_ITEM_ENTRY.COLUMN_CATEGORIES,
            BooksContract.SHELF_ITEM_ENTRY.COLUMN_DESCRIPTION,
            BooksContract.SHELF_ITEM_ENTRY.COLUMN_ISBN,
            BooksContract.SHELF_ITEM_ENTRY.COLUMN_PAGE_COUNT,
            BooksContract.SHELF_ITEM_ENTRY.COLUMN_PUBLISHED_DATE,
            BooksContract.SHELF_ITEM_ENTRY.COLUMN_PUBLISHER,
            BooksContract.SHELF_ITEM_ENTRY.COLUMN_THUMBNAIL
    };

    public static final int COL_TITLE_INDEX = 0;
    public static final int COL_AUTHOR_INDEX = 1;
    public static final int COL_AVG_RATING_INDEX = 2;
    public static final int COL_SHELF_NAME_INDEX = 3;
    public static final int COL_CAT_INDEX = 4;
    public static final int COL_DESC_INDEX = 5;
    public static final int COL_ISBN_INDEX = 6;
    public static final int COL_PAGE_COUNT_INDEX = 7;
    public static final int COL_PUBLISHED_DATE_INDEX = 8;
    public static final int COL_PUBLISHER_INDEX  = 9;
    public static final int COL_THUMBNAIL_INDEX = 10;

    private static final int BOOK_LOADER = 15;


    public LocalDetailTab() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_local_detail_tab, container, false);

        titleView = (TextView) rootView.findViewById(R.id.search_result_title);
        authorsView = (TextView) rootView.findViewById(R.id.search_result_author);
        avg_ratingsView = (RatingBar) rootView.findViewById(R.id.local_search_result_avg_ratings);
        publisherView = (TextView) rootView.findViewById(R.id.search_result_publisher);
        publishedDateView = (TextView) rootView.findViewById(R.id.search_result_published_date);
        categoriesView = (TextView) rootView.findViewById(R.id.search_result_categories);
        page_countView = (TextView) rootView.findViewById(R.id.search_result_page_count);
        thumbnailView = (ImageView) rootView.findViewById(R.id.search_result_thumbnail);
        isbnView = (TextView) rootView.findViewById(R.id.search_result_isbn);
        book_idView = (TextView) rootView.findViewById(R.id.book_id);
        small_thumbnailView = (TextView) rootView.findViewById(R.id.small_thumbnail);
        thumbnail_urlView = (TextView) rootView.findViewById(R.id.thumbnail_url);

        if (getArguments().containsKey(LocalBookDetailFragment.ARG_ITEM_ID)) {
            _id = getArguments().getInt(LocalBookDetailFragment.ARG_ITEM_ID);
            //Log.d("Local Data", "id: " + _id);
        }

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(BOOK_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (_id > -1) {
            //Log.d("Local Detail", BooksContract.SHELF_ITEM_ENTRY.buildBookDbIdUri(_id).toString());
            return new CursorLoader(getActivity(), BooksContract.SHELF_ITEM_ENTRY.buildBookDbIdUri(_id),
                    BOOK_COLUMNS, null, null, null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            titleView.setText(data.getString(COL_TITLE_INDEX));
            Log.d("Local Detail", titleView.getText().toString());
            authorsView.setText(data.getString(COL_AUTHOR_INDEX));
            avg_ratingsView.setRating((float) data.getDouble(COL_AVG_RATING_INDEX));
            publisherView.setText(data.getString(COL_PUBLISHER_INDEX));
            publishedDateView.setText(data.getString(COL_PUBLISHED_DATE_INDEX));
            categoriesView.setText(data.getString(COL_CAT_INDEX));
            page_countView.setText(Integer.toString(data.getInt(COL_PAGE_COUNT_INDEX)));
            //descriptionView.setText(data.getString(COL_DESC_INDEX));
            if (!TextUtils.isEmpty(data.getString(COL_THUMBNAIL_INDEX))) {
                Picasso.with(getActivity()).load(data.getString(COL_THUMBNAIL_INDEX)).fit()
                        .into(thumbnailView);
            } else {
                thumbnailView.setImageResource(R.drawable.no_cover_thumb);
            }
            isbnView.setText(data.getString(COL_ISBN_INDEX));



        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

}
