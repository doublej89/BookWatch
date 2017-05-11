package com.jawadjahangir.android.bookwatch;


import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jawadjahangir.android.bookwatch.data.BooksContract;

import org.jsoup.Jsoup;


/**
 * A simple {@link Fragment} subclass.
 */
public class LocalUseTab extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private int _id = -1;

    private static final int BOOK_LOADER = 15;

    TextView loanView;
    TextView readView;
    TextView descriptionView;


    public LocalUseTab() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_local_use_tab, container, false);

        loanView = (TextView) rootView.findViewById(R.id.loan_view);
        readView = (TextView) rootView.findViewById(R.id.read_view);
        descriptionView = (TextView) rootView.findViewById(R.id.book_description);

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
                    new String[] {BooksContract.SHELF_ITEM_ENTRY.COLUMN_LOANED_TO,
                            BooksContract.SHELF_ITEM_ENTRY.COLUMN_READ,
                            BooksContract.SHELF_ITEM_ENTRY.COLUMN_DESCRIPTION}, null, null, null);
        }
        return null;
    }

    public static final int COL_LOANED_INDEX = 0;
    public static final int COL_READ_INDEX = 1;
    public static final int COL_DESC_INDEX = 2;

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            if (!TextUtils.isEmpty(data.getString(COL_LOANED_INDEX))) {
                loanView.setText(getActivity().getString(R.string.you_loaned_this_book,
                        data.getString(COL_LOANED_INDEX)) );
            } else {
                loanView.setText(R.string.book_is_with_you);
            }

            if (data.getInt(COL_READ_INDEX) == 1) {
                readView.setText(R.string.book_read);
            } else {
                readView.setText(R.string.book_not_read);
            }

            if (!TextUtils.isEmpty(data.getString(COL_DESC_INDEX))) {
                descriptionView.setText(Jsoup.parse(data.getString(COL_DESC_INDEX)).text());
            } else {
                descriptionView.setText(R.string.unavailable_desc);
            }


        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

}
