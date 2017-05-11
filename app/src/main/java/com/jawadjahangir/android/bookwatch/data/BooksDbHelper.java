package com.jawadjahangir.android.bookwatch.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by MeMyself on 3/3/2017.
 */
public class BooksDbHelper extends SQLiteOpenHelper{
    private static final int DATABASE_VERSION = 1;
    static final String DATABASE_NAME = "books.db";

    public BooksDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQLITE_CREATE_SHELF_TABLE = "CREATE TABLE " + BooksContract.ShelfEntry.TABLE_NAME + " (" +
                BooksContract.ShelfEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                BooksContract.ShelfEntry.COLUMN_SHELF_NAME + " TEXT NOT NULL);";

        final String SQLITE_CREATE_SHELF_ITEM_TABLE = "CREATE TABLE " + BooksContract.SHELF_ITEM_ENTRY.TABLE_NAME +
                " (" + BooksContract.SHELF_ITEM_ENTRY._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                BooksContract.SHELF_ITEM_ENTRY.COLUMN_TITLE + " TEXT NOT NULL, " +
                BooksContract.SHELF_ITEM_ENTRY.COLUMN_AUTHOR + " TEXT, " +
                BooksContract.SHELF_ITEM_ENTRY.COLUMN_BOOK_ID + " TEXT UNIQUE NOT NULL, " +
                BooksContract.SHELF_ITEM_ENTRY.COLUMN_CATEGORIES + " TEXT, " +
                BooksContract.SHELF_ITEM_ENTRY.COLUMN_ISBN + " TEXT UNIQUE NOT NULL, " +
                BooksContract.SHELF_ITEM_ENTRY.COLUMN_AVERAGE_RATING + " REAL, " +
                BooksContract.SHELF_ITEM_ENTRY.COLUMN_PAGE_COUNT + " INTEGER, " +
                BooksContract.SHELF_ITEM_ENTRY.COLUMN_PUBLISHER + " TEXT, " +
                BooksContract.SHELF_ITEM_ENTRY.COLUMN_PUBLISHED_DATE + " TEXT, " +
                BooksContract.SHELF_ITEM_ENTRY.COLUMN_SMALL_THUMBNAIL + " TEXT, " +
                BooksContract.SHELF_ITEM_ENTRY.COLUMN_THUMBNAIL + " TEXT, " +
                BooksContract.SHELF_ITEM_ENTRY.COLUMN_DESCRIPTION + " TEXT, " +
                BooksContract.SHELF_ITEM_ENTRY.COLUMN_READ + " INTEGER, " +
                BooksContract.SHELF_ITEM_ENTRY.COLUMN_LOANED_TO + " TEXT, " +
                BooksContract.SHELF_ITEM_ENTRY.COLUMN_CURRENT_PAGE + " INTEGER, " +
                BooksContract.SHELF_ITEM_ENTRY.COLUMN_SHELF_NAME + " TEXT NOT NULL, " +
                BooksContract.SHELF_ITEM_ENTRY.COLUMN_DATE_TIME + " TEXT, " +
                "FOREIGN KEY(" + BooksContract.SHELF_ITEM_ENTRY.COLUMN_SHELF_NAME + ") REFERENCES " +
                BooksContract.ShelfEntry.TABLE_NAME + "(" + BooksContract.ShelfEntry.COLUMN_SHELF_NAME + "));";

        sqLiteDatabase.execSQL(SQLITE_CREATE_SHELF_TABLE);
        sqLiteDatabase.execSQL(SQLITE_CREATE_SHELF_ITEM_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + BooksContract.ShelfEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + BooksContract.SHELF_ITEM_ENTRY.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
