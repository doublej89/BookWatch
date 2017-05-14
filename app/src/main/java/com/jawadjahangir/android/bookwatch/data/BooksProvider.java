package com.jawadjahangir.android.bookwatch.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by MeMyself on 3/3/2017.
 */
public class BooksProvider extends ContentProvider {
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private BooksDbHelper booksDbHelper;

    static final int SHELF = 100;
    static final int SHELF_WITH_ID = 150;
    static final int SHELF_WITH_NAME = 170;
    static final int SHELF_ITEM = 200;
    static final int SHELF_ITEM_WITH_ID = 250;
    static final int SHELF_ITEM_WITH_NAME = 300;
    static final int SHELF_ITEM_WITH_BOOK_ID = 350;
    static final int SHELF_ITEM_LOCAL_SEARCH = 400;
    static final int SHELF_ITEM_TITLE_AUTHOR = 420;

    private static final SQLiteQueryBuilder mBooksByShelfQueryBuilder;

    static {
        mBooksByShelfQueryBuilder = new SQLiteQueryBuilder();

        mBooksByShelfQueryBuilder.setTables(
                BooksContract.ShelfEntry.TABLE_NAME + " INNER JOIN " +
                BooksContract.SHELF_ITEM_ENTRY.TABLE_NAME + " ON " +
                BooksContract.ShelfEntry.TABLE_NAME + "." + BooksContract.ShelfEntry.COLUMN_SHELF_NAME + " = " +
                BooksContract.SHELF_ITEM_ENTRY.TABLE_NAME + "." + BooksContract.SHELF_ITEM_ENTRY.COLUMN_SHELF_NAME
        );
    }

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = BooksContract.ContentAuthority;

        matcher.addURI(authority, BooksContract.PATH_SHELF, SHELF);
        matcher.addURI(authority, BooksContract.PATH_SHELF + "/*", SHELF_WITH_NAME);
        matcher.addURI(authority, BooksContract.PATH_SHELF_ITEM, SHELF_ITEM);
        matcher.addURI(authority, BooksContract.PATH_SHELF_ITEM + "/*", SHELF_ITEM_WITH_NAME);
        matcher.addURI(authority, BooksContract.PATH_SHELF_ITEM + "/*/*/*", SHELF_ITEM_LOCAL_SEARCH);
        matcher.addURI(authority, BooksContract.PATH_SHELF_ITEM + "/*/*", SHELF_ITEM_WITH_ID);
        matcher.addURI(authority, BooksContract.PATH_SHELF_ITEM + "/*/*/*/*", SHELF_ITEM_TITLE_AUTHOR);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        booksDbHelper = new BooksDbHelper(getContext());
        return true;
    }

    private static final String shelfNameSelection = BooksContract.ShelfEntry.TABLE_NAME + "." +
            BooksContract.ShelfEntry.COLUMN_SHELF_NAME + " = ? ";

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case SHELF:
                return BooksContract.ShelfEntry.CONTENT_TYPE;
            case SHELF_WITH_NAME:
                return BooksContract.ShelfEntry.CONTENT_ITEM_TYPE;
            case SHELF_ITEM:
                return BooksContract.SHELF_ITEM_ENTRY.CONTENT_TYPE;
            case SHELF_ITEM_WITH_NAME:
                return BooksContract.SHELF_ITEM_ENTRY.CONTENT_TYPE;
            case SHELF_ITEM_WITH_BOOK_ID:
                return BooksContract.SHELF_ITEM_ENTRY.CONTENT_ITEM_TYPE;
            case SHELF_ITEM_WITH_ID:
                return BooksContract.SHELF_ITEM_ENTRY.CONTENT_ITEM_TYPE;
            case SHELF_ITEM_LOCAL_SEARCH:
                return BooksContract.SHELF_ITEM_ENTRY.CONTENT_TYPE;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case SHELF:
                retCursor = booksDbHelper.getReadableDatabase().query(
                        BooksContract.ShelfEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case SHELF_WITH_ID:
                selection = BooksContract.ShelfEntry.TABLE_NAME + "." + BooksContract.ShelfEntry.COLUMN_SHELF_NAME
                        + " = ? ";
                selectionArgs = new String[] {BooksContract.ShelfEntry.getShelfName(uri)};
                retCursor = booksDbHelper.getReadableDatabase().query(
                        BooksContract.ShelfEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case SHELF_ITEM:
                retCursor = booksDbHelper.getReadableDatabase().query(
                        BooksContract.SHELF_ITEM_ENTRY.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case SHELF_ITEM_WITH_NAME:
                selection = BooksContract.SHELF_ITEM_ENTRY.COLUMN_SHELF_NAME + " = ? ";
                selectionArgs = new String[] {BooksContract.SHELF_ITEM_ENTRY.getBookShelfName(uri)};
                retCursor = booksDbHelper.getReadableDatabase().query(
                        BooksContract.SHELF_ITEM_ENTRY.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case SHELF_ITEM_WITH_ID:
                selection = BooksContract.SHELF_ITEM_ENTRY._ID + " = ? ";
                selectionArgs = new String[] {BooksContract.SHELF_ITEM_ENTRY.getBookDbId(uri)};
                retCursor = booksDbHelper.getReadableDatabase().query(
                        BooksContract.SHELF_ITEM_ENTRY.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case SHELF_ITEM_TITLE_AUTHOR:
                selection = BooksContract.SHELF_ITEM_ENTRY.COLUMN_TITLE + " = ? AND " +
                        BooksContract.SHELF_ITEM_ENTRY.COLUMN_AUTHOR + " = ?";
                selectionArgs = new String[] {BooksContract.SHELF_ITEM_ENTRY.getBookTitle(uri),
                        BooksContract.SHELF_ITEM_ENTRY.getBookAuthor(uri)};
                retCursor = booksDbHelper.getReadableDatabase().query(
                        BooksContract.SHELF_ITEM_ENTRY.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case SHELF_ITEM_LOCAL_SEARCH:
                //Log.i("Local Search", "You searched for: " + BooksContract.SHELF_ITEM_ENTRY.getLocalSearchString(uri));
                selection = BooksContract.SHELF_ITEM_ENTRY.TABLE_NAME + "." +
                        BooksContract.SHELF_ITEM_ENTRY.COLUMN_TITLE + " LIKE ? OR " +
                        BooksContract.SHELF_ITEM_ENTRY.TABLE_NAME + "." + BooksContract.SHELF_ITEM_ENTRY.COLUMN_AUTHOR
                        + " LIKE ? ";
                selectionArgs = new String[] {"%" + BooksContract.SHELF_ITEM_ENTRY.getLocalSearchString(uri) + "%",
                        "%" + BooksContract.SHELF_ITEM_ENTRY.getLocalSearchString(uri) + "%"};

                retCursor = booksDbHelper.getReadableDatabase().query(
                        BooksContract.SHELF_ITEM_ENTRY.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        Uri retUri;
        switch (sUriMatcher.match(uri)) {
            case SHELF: {
                long _id = booksDbHelper.getWritableDatabase().insert(BooksContract.ShelfEntry.TABLE_NAME, null, contentValues);
                if (_id > 0) {
                    retUri = BooksContract.ShelfEntry.buildShelfUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into: " + uri);
                }
                break;
            }
            case SHELF_ITEM: {
                long _id = booksDbHelper.getWritableDatabase().insert(BooksContract.SHELF_ITEM_ENTRY.TABLE_NAME, null, contentValues);
                if (_id > 0) {
                    retUri = BooksContract.SHELF_ITEM_ENTRY.buildBookDbIdUri((int)_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into: " + uri);
                }
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return retUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int rowsDeleted;
        if (selection == null) selection = "1";
        switch (sUriMatcher.match(uri)) {
            case SHELF:
                rowsDeleted = booksDbHelper.getWritableDatabase().delete(BooksContract.ShelfEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            case SHELF_ITEM:
                selection = BooksContract.SHELF_ITEM_ENTRY.TABLE_NAME + "." + BooksContract.SHELF_ITEM_ENTRY.COLUMN_SHELF_NAME +
                        " = ? ";
                selectionArgs = new String[] {BooksContract.SHELF_ITEM_ENTRY.getBookShelfName(uri)};
                rowsDeleted = booksDbHelper.getWritableDatabase().delete(BooksContract.SHELF_ITEM_ENTRY.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            case SHELF_WITH_ID:
                selection = BooksContract.ShelfEntry.TABLE_NAME + "." + BooksContract.ShelfEntry._ID + " = ? ";
                selectionArgs = new String[] {Integer.toString(BooksContract.ShelfEntry.getShelfId(uri))};
                rowsDeleted = booksDbHelper.getWritableDatabase().delete(BooksContract.ShelfEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            case SHELF_ITEM_WITH_ID:
                selection = BooksContract.SHELF_ITEM_ENTRY._ID + " = ? ";
                selectionArgs = new String[] {BooksContract.SHELF_ITEM_ENTRY.getBookDbId(uri)};
                rowsDeleted = booksDbHelper.getWritableDatabase().delete(
                        BooksContract.SHELF_ITEM_ENTRY.TABLE_NAME,
                        selection,
                        selectionArgs
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsDeleted != 0)
            getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        int rowsUpdated;
        switch (sUriMatcher.match(uri)) {
            case SHELF:
                rowsUpdated = booksDbHelper.getWritableDatabase().update(BooksContract.ShelfEntry.TABLE_NAME,
                        contentValues,
                        selection,
                        selectionArgs);
                break;
            case SHELF_ITEM:
                selection = BooksContract.SHELF_ITEM_ENTRY.TABLE_NAME + "." + BooksContract.SHELF_ITEM_ENTRY.COLUMN_SHELF_NAME +
                        " = ? ";
                selectionArgs = new String[] {BooksContract.SHELF_ITEM_ENTRY.getBookShelfName(uri)};
                rowsUpdated = booksDbHelper.getWritableDatabase().update(BooksContract.SHELF_ITEM_ENTRY.TABLE_NAME,
                        contentValues,
                        selection,
                        selectionArgs);
                break;
            case SHELF_WITH_ID:
                selection = BooksContract.ShelfEntry.TABLE_NAME + "." + BooksContract.ShelfEntry._ID + " = ? ";
                selectionArgs = new String[] {Integer.toString(BooksContract.ShelfEntry.getShelfId(uri))};
                rowsUpdated = booksDbHelper.getWritableDatabase().update(BooksContract.ShelfEntry.TABLE_NAME,
                        contentValues,
                        selection,
                        selectionArgs);
                break;
            case SHELF_ITEM_WITH_ID:
                selection = BooksContract.SHELF_ITEM_ENTRY.TABLE_NAME + "." +
                        BooksContract.SHELF_ITEM_ENTRY._ID + " = ? ";
                selectionArgs = new String[] {
                        BooksContract.SHELF_ITEM_ENTRY.getBookDbId(uri)};
                rowsUpdated = booksDbHelper.getWritableDatabase().update(BooksContract.SHELF_ITEM_ENTRY.TABLE_NAME,
                        contentValues,
                        selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0)
            getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(@NonNull  Uri uri, @NonNull ContentValues[] values) {
        final SQLiteDatabase db = booksDbHelper.getWritableDatabase();
        switch (sUriMatcher.match(uri)) {
            case SHELF: {
                db.beginTransaction();
                int counter = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(BooksContract.ShelfEntry.TABLE_NAME, null, value);
                        if (_id != -1)
                            counter++;
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return counter;
            }
            case SHELF_ITEM: {
                db.beginTransaction();
                int counter = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(BooksContract.SHELF_ITEM_ENTRY.TABLE_NAME, null, value);
                        if (_id != -1)
                            counter++;
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return counter;
            }
            default:
                return super.bulkInsert(uri, values);
        }
    }
}
