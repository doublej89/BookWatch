package com.jawadjahangir.android.bookwatch;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;

import com.google.zxing.integration.android.IntentIntegrator;

/**
 * An activity representing a single SearchedBook detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link SearchedBookListActivity}.
 */
public class SearchedBookDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchedbook_detail);


        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(SearchedBookDetailFragment.ARG_ITEM_ID,
                    getIntent().getStringExtra(SearchedBookDetailFragment.ARG_ITEM_ID));
            SearchedBookDetailFragment fragment = new SearchedBookDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.searchedbook_detail_container, fragment)
                    .commit();
        }
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
                    Intent intent = new Intent(SearchedBookDetailActivity.this, LocalBookListActivity.class);
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
