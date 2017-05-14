package com.jawadjahangir.android.bookwatch;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.jawadjahangir.android.bookwatch.data.BooksContract;
import com.jawadjahangir.android.bookwatch.dummy.SearchedBookContent;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * An activity representing a list of Local Books. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link LocalBookDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class LocalBookListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, RecyclerViewClickListener{

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */

    List<SearchedBookContent.NonParcelBook> sbooks;
    private boolean mTwoPane;
    private String shelf_name;
    private String queryString;
    View recyclerView;

    private int mPosition = ListView.INVALID_POSITION;
    private static final String SELECTED_KEY = "selected_position";

    private int BOOKS_LOADER = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_localbook_list);

        Intent intent = getIntent();

        if (intent.hasExtra("reveal_shelf")) {
            shelf_name = intent.getStringExtra("reveal_shelf");
        } else if (intent.hasExtra("local_search")) {
            queryString = intent.getStringExtra("local_search");
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);


        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (!TextUtils.isEmpty(shelf_name)) {
                getSupportActionBar().setTitle(getString(R.string.books_kept_in, shelf_name));

            } else if (!TextUtils.isEmpty(queryString)) {
                getSupportActionBar().setTitle(getString(R.string.search_results, queryString));
            } else {
                getSupportActionBar().setTitle(getString(R.string.all_books));
            }
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }



        recyclerView = findViewById(R.id.localbook_list);
        assert recyclerView != null;
       

        if (findViewById(R.id.localbook_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            // The listview probably hasn't even been populated yet.  Actually perform the
            // swapout in onLoadFinished.
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }


        getSupportLoaderManager().initLoader(BOOKS_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (!TextUtils.isEmpty(shelf_name))
            return new CursorLoader(this, BooksContract.SHELF_ITEM_ENTRY.buildBookWithShelfNameUri(shelf_name),
                new String[]{BooksContract.SHELF_ITEM_ENTRY.COLUMN_SMALL_THUMBNAIL,
                        BooksContract.SHELF_ITEM_ENTRY.COLUMN_TITLE, BooksContract.SHELF_ITEM_ENTRY.COLUMN_AUTHOR,
                        BooksContract.SHELF_ITEM_ENTRY.COLUMN_SHELF_NAME,
                        BooksContract.SHELF_ITEM_ENTRY._ID}, null, null, null);
        else if (!TextUtils.isEmpty(queryString)) {
            //Log.i("Local Search", BooksContract.SHELF_ITEM_ENTRY.buildLocalSearchUri(queryString).toString());
            return new CursorLoader(this, BooksContract.SHELF_ITEM_ENTRY.buildLocalSearchUri(queryString),
                    new String[]{BooksContract.SHELF_ITEM_ENTRY.COLUMN_SMALL_THUMBNAIL,
                            BooksContract.SHELF_ITEM_ENTRY.COLUMN_TITLE, BooksContract.SHELF_ITEM_ENTRY.COLUMN_AUTHOR,
                            BooksContract.SHELF_ITEM_ENTRY.COLUMN_SHELF_NAME,
                            BooksContract.SHELF_ITEM_ENTRY._ID}, null, null, null);
        }
        return new CursorLoader(this, BooksContract.SHELF_ITEM_ENTRY.CONTENT_URI,
                new String[]{BooksContract.SHELF_ITEM_ENTRY.COLUMN_SMALL_THUMBNAIL,
                        BooksContract.SHELF_ITEM_ENTRY.COLUMN_TITLE, BooksContract.SHELF_ITEM_ENTRY.COLUMN_AUTHOR,
                        BooksContract.SHELF_ITEM_ENTRY.COLUMN_SHELF_NAME,
                        BooksContract.SHELF_ITEM_ENTRY._ID}, null, null, null);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // When tablets rotate, the currently selected list item needs to be saved.
        // When no item is selected, mPosition will be set to ListView.INVALID_POSITION,
        // so check for that before storing.
        if (mPosition != ListView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }

    public static final int COL_SMALL_THUMBNAIL_INDEX = 0;
    public static final int COL_TITLE_INDEX = 1;
    public static final int COL_AUTHOR_INDEX = 2;
    public static final int COL_SHELF_NAME_INDEX= 3;
    public static final int COL_BOOK_ID_INDEX = 4;

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        sbooks = new ArrayList<>();
        if (data != null && data.moveToFirst()) {
            do {
                int book_id = data.getInt(COL_BOOK_ID_INDEX);
                String title = data.getString(COL_TITLE_INDEX);
                String author = data.getString(COL_AUTHOR_INDEX);
                String small_thumb = data.getString(COL_SMALL_THUMBNAIL_INDEX);
                String page_count = data.getString(COL_SHELF_NAME_INDEX);
                SearchedBookContent.NonParcelBook book = new SearchedBookContent.NonParcelBook(book_id, title, author, small_thumb,
                        page_count);
                sbooks.add(book);
            } while (data.moveToNext());
        }

        setupRecyclerView((RecyclerView) recyclerView);

        if (mPosition != ListView.INVALID_POSITION){
            ((RecyclerView) recyclerView).smoothScrollToPosition(mPosition);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        ((RecyclerView) recyclerView).setAdapter(new SimpleItemRecyclerViewAdapter(null, null));
    }


    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        if (sbooks.size() == 0) {
            TextView emptyView = (TextView) findViewById(R.id.recyclerview_book_shelf_empty);
            emptyView.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(shelf_name)) {
                emptyView.setText(getString(R.string.no_book_added, shelf_name));
                emptyView.setContentDescription(getString(R.string.no_book_added, shelf_name));
                recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(null, null));
                return;
            } else if (!TextUtils.isEmpty(queryString)) {
                emptyView.setText(getString(R.string.no_search_results, queryString));
                emptyView.setContentDescription(getString(R.string.no_search_results, queryString));
                recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(null, null));
                return;
            } else {
                emptyView.setText(getString(R.string.no_book_added_default));
                emptyView.setContentDescription(getString(R.string.no_book_added_default));
                recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(null, null));
                return;
            }
        }
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(sbooks, this));
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {



        private final List<SearchedBookContent.NonParcelBook> mValues;
        private RecyclerViewClickListener itemListener;

        public SimpleItemRecyclerViewAdapter(List<SearchedBookContent.NonParcelBook> items, RecyclerViewClickListener itemListener) {
            mValues = items;
            this.itemListener = itemListener;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.localbook_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            final AlertDialog.Builder alertDialogBuilder =
                    new AlertDialog.Builder(LocalBookListActivity.this, R.style.MyDialogTheme);
            alertDialogBuilder.setTitle(getString(R.string.delete_confirmation));
            holder.mItem = mValues.get(position);
            if (mValues.get(position).title.length() > 20) {
                holder.mTitle.setText(mValues.get(position).title.substring(0, 19) + "...");
            } else {
                holder.mTitle.setText(mValues.get(position).title);
            }
            holder.mAuthor.setText(mValues.get(position).author);
            if (!TextUtils.isEmpty(mValues.get(position).thumbnail)) {
                Picasso.with(LocalBookListActivity.this).load(mValues.get(position).thumbnail).placeholder(R.drawable.no_cover_thumb).fit()
                        .into(holder.thumbnailView);
            } else {
                holder.thumbnailView.setImageResource(R.drawable.no_cover_thumb);
            }
            holder.shelfView.setText(mValues.get(position).shelfName);

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemListener.recyclerViewListClicked(v, holder.getLayoutPosition());
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putInt(LocalBookDetailFragment.ARG_ITEM_ID, holder.mItem._id);
                        LocalBookDetailFragment fragment = new LocalBookDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.searchedbook_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, LocalBookDetailActivity.class);
                        intent.putExtra(LocalBookDetailFragment.ARG_ITEM_ID, holder.mItem._id);

                        context.startActivity(intent);
                    }
                }
            });

            alertDialogBuilder.setPositiveButton(getString(R.string.yes),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            getContentResolver().delete(
                                    BooksContract.SHELF_ITEM_ENTRY.buildBookDbIdUri(holder.mItem._id),
                                    null, null);
                            getSupportLoaderManager().restartLoader(BOOKS_LOADER, null, LocalBookListActivity.this);
                        }
                    });

            alertDialogBuilder.setNegativeButton(getString(R.string.no),new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });

            holder.deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    alertDialogBuilder.create();
                    alertDialogBuilder.show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final ImageView thumbnailView;
            public final TextView mTitle;
            public final TextView mAuthor;
            public final TextView shelfView;
            public Button deleteButton;
            public SearchedBookContent.NonParcelBook mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mTitle = (TextView) view.findViewById(R.id.title);
                mAuthor = (TextView) view.findViewById(R.id.author);
                thumbnailView = (ImageView) view.findViewById(R.id.small_thumbnail);
                shelfView = (TextView) view.findViewById(R.id.shelf_name);
                deleteButton = (Button) view.findViewById(R.id.delete_button);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mTitle.getText() + "'";
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main_menu, menu);
        MenuItem item = menu.findItem(R.id.local_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setMaxWidth(ViewGroup.LayoutParams.MATCH_PARENT);

        if (searchView != null) {
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    shelf_name = null;
                    queryString = query;
                    getSupportLoaderManager().restartLoader(BOOKS_LOADER, null, LocalBookListActivity.this);
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
        }
        else if (id == R.id.all_books) {
            Intent intent = new Intent(this, LocalBookListActivity.class);
            startActivity(intent);
        } else if (id == R.id.scan_barcode) {
            IntentIntegrator integrator = new IntentIntegrator(this);
            integrator.setPrompt("Scan");
            integrator.initiateScan();
        } else if (id == R.id.all_shelves) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void recyclerViewListClicked(View v, int position) {
        mPosition = position;
    }
}
