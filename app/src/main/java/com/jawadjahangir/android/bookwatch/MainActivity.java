package com.jawadjahangir.android.bookwatch;

import android.content.ContentValues;
import android.content.DialogInterface;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.jawadjahangir.android.bookwatch.data.BooksContract;
import com.jawadjahangir.android.bookwatch.search.SearchService;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private final int SHELF_LOADER = 1;

    private LinearLayout shelvesLayout;

    AlertDialog.Builder alertDialogBuilder;
    EditText createShelfField;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle(getString(R.string.app_name));

        ContentValues value1 = new ContentValues();
        value1.put(BooksContract.ShelfEntry.COLUMN_SHELF_NAME, getString(R.string.read));
        ContentValues value2 = new ContentValues();
        value2.put(BooksContract.ShelfEntry.COLUMN_SHELF_NAME, getString(R.string.reading));
        ContentValues value3 = new ContentValues();
        value3.put(BooksContract.ShelfEntry.COLUMN_SHELF_NAME, getString(R.string.intend_to_read));
        ContentValues value4 = new ContentValues();
        value4.put(BooksContract.ShelfEntry.COLUMN_SHELF_NAME, getString(R.string.favorites));
        ContentValues value5 = new ContentValues();
        value5.put(BooksContract.ShelfEntry.COLUMN_SHELF_NAME, getString(R.string.loaned));

        ContentValues[] values = new ContentValues[] {value1, value2, value3, value4, value5};

        Cursor cursor = getContentResolver().query(BooksContract.ShelfEntry.CONTENT_URI,
                new String[]{BooksContract.ShelfEntry.COLUMN_SHELF_NAME}, null, null, null);

        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.close();
            } else {
                getContentResolver().bulkInsert(BooksContract.ShelfEntry.CONTENT_URI, values);
                cursor.close();
            }
        }

        shelvesLayout = (LinearLayout) findViewById(R.id.shelves_list);

        alertDialogBuilder = new AlertDialog.Builder(this, R.style.MyDialogTheme);
        alertDialogBuilder.setTitle(getString(R.string.enter_shelf_name));
        createShelfField = new EditText(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(4, 16, 4, 16);
        createShelfField.setLayoutParams(params);

        alertDialogBuilder.setView(createShelfField);
        alertDialogBuilder.setPositiveButton(getString(R.string.create_shelf),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                       if (!TextUtils.isEmpty(createShelfField.getText())) {
                           String shelf_name = createShelfField.getText().toString();
                           Cursor cursor = getContentResolver().query(BooksContract.ShelfEntry.CONTENT_URI,
                                   new String[]{BooksContract.ShelfEntry.COLUMN_SHELF_NAME}, null, null, null);
                           if (cursor != null) {
                               if (cursor.getCount() > 0) {
                                   Toast.makeText(MainActivity.this, getString(R.string.shelf_already_exists),
                                           Toast.LENGTH_SHORT).show();
                               } else {
                                   ContentValues contentValues = new ContentValues();
                                   contentValues.put(BooksContract.ShelfEntry.COLUMN_SHELF_NAME, shelf_name);
                                   getContentResolver().insert(BooksContract.ShelfEntry.CONTENT_URI, contentValues);
                                   getSupportLoaderManager().restartLoader(SHELF_LOADER, null, MainActivity.this);
                               }
                               cursor.close();
                           }
                       } else {
                           Toast.makeText(MainActivity.this, getString(R.string.cant_create_shelf),
                                   Toast.LENGTH_SHORT).show();
                       }
                    }
                });

        alertDialogBuilder.setNegativeButton(getString(R.string.cancel),new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        getSupportLoaderManager().initLoader(SHELF_LOADER, null, this);


    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        return new CursorLoader(this, BooksContract.ShelfEntry.CONTENT_URI,
                    new String[]{BooksContract.ShelfEntry.COLUMN_SHELF_NAME}, null, null, null);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

            if (data != null && data.moveToFirst()) {
                do {
                    int colIndex = data.getColumnIndex(BooksContract.ShelfEntry.COLUMN_SHELF_NAME);
                    String shelfName = data.getString(colIndex);
                    Button button = new Button(this);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    );
                    params.setMargins(16, 16, 16, 16);
                    button.setLayoutParams(params);
                    button.setBackgroundResource(R.drawable.main_page_button);
                    button.setTextColor(getResources().getColor(R.color.white));
                    button.setText(shelfName);
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (((Button)view).getText().equals("Reading")) {
                                Intent intent = new Intent(MainActivity.this, NowReading.class);
                                startActivity(intent);
                            } else {
                                Intent intent = new Intent(MainActivity.this, LocalBookListActivity.class);
                                intent.putExtra("reveal_shelf", ((Button) view).getText());
                                startActivity(intent);
                            }
                        }
                    });
                    if (shelvesLayout != null) {
                        shelvesLayout.addView(button);
                    }
                } while (data.moveToNext());
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
                    Intent intent = new Intent(MainActivity.this, LocalBookListActivity.class);
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
        } else if (id == R.id.create_shelf) {
            alertDialogBuilder.create();
            alertDialogBuilder.show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            String isbn = result.getContents();
            Intent intent = new Intent(Intent.ACTION_SYNC, null, this, SearchService.class);
            intent.putExtra("barcode_scan", isbn);
            startService(intent);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
