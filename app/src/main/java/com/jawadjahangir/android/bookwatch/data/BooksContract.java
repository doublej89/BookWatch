package com.jawadjahangir.android.bookwatch.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by MeMyself on 3/2/2017.
 */
public class BooksContract {
    public static final String ContentAuthority = "com.jawadjahangir.android.bookwatch.app";
    public static final Uri BaseUri = Uri.parse("content://" + ContentAuthority);

    public static final String PATH_SHELF = "shelf";
    public static final String PATH_SHELF_ITEM = "shelf_item";

    public static final class ShelfEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BaseUri.buildUpon()
                .appendPath(PATH_SHELF).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + ContentAuthority + "/" + PATH_SHELF;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + ContentAuthority + "/" + PATH_SHELF;
        public static final String TABLE_NAME = "shelf";
        public static final String COLUMN_SHELF_NAME = "shelf_name";

        public static Uri buildShelfUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildShelfWithShelfNameUri(String shelfName) {
            return CONTENT_URI.buildUpon().appendPath(shelfName).build();
        }

        public static String getShelfName(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        public static int getShelfId(Uri uri){
            return Integer.parseInt(uri.getPathSegments().get(1));
        }
    }

    public static final class SHELF_ITEM_ENTRY implements BaseColumns {
        public static final Uri CONTENT_URI = BaseUri.buildUpon()
                .appendPath(PATH_SHELF_ITEM).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + ContentAuthority + "/" + PATH_SHELF_ITEM;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + ContentAuthority + "/" + PATH_SHELF_ITEM;
        public static final String TABLE_NAME = "shelf_item";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_AUTHOR = "author";
        public static final String COLUMN_BOOK_ID = "book_id";
        public static final String COLUMN_PUBLISHER = "publisher";
        public static final String COLUMN_PUBLISHED_DATE = "published_date";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_ISBN = "ISBN";
        public static final String COLUMN_PAGE_COUNT = "page_count";
        public static final String COLUMN_CATEGORIES = "categories";
        public static final String COLUMN_AVERAGE_RATING = "avg_rating";
        public static final String COLUMN_SMALL_THUMBNAIL = "small_thumb";
        public static final String COLUMN_THUMBNAIL = "thumbnail";
        public static final String COLUMN_READ = "read";
        public static final String COLUMN_LOANED_TO = "loaned_to";
        public static final String COLUMN_CURRENT_PAGE = "current_page";
        public static final String COLUMN_DATE_TIME = "date_time";
        public static final String COLUMN_SHELF_NAME = "shelf_name";

        public static Uri buildBookWithShelfNameUri(String shelfName) {
            return CONTENT_URI.buildUpon().appendPath(shelfName).build();
        }

        public static Uri buildBookWithTitleAndAuthorUri(String title, String author) {
            return CONTENT_URI.buildUpon().appendPath("title").appendPath("author")
                    .appendPath(title).appendPath(author).build();
        }

        public static String getBookTitle(Uri uri){
            return uri.getPathSegments().get(3);
        }

        public static String getBookAuthor(Uri uri){
            return uri.getPathSegments().get(4);
        }

        public static Uri buildLocalSearchUri(String search_query) {
            return CONTENT_URI.buildUpon().appendPath("local").appendPath("search").appendPath(search_query).build();
        }

        public static String getLocalSearchString(Uri uri){
            return uri.getPathSegments().get(3);
        }

//        public static Uri buildBookIdUri(String book_id) {
//            return CONTENT_URI.buildUpon().appendPath("book_id").appendPath(book_id).build();
//        }

        public static Uri buildBookDbIdUri(int _id){
            return CONTENT_URI.buildUpon().appendPath("_id").appendPath(Integer.toString(_id)).build();
        }

        public static String getBookDbId(Uri uri) {
            return uri.getPathSegments().get(2);
        }

//        public static String getBookId(Uri uri){
//            return uri.getPathSegments().get(2);
//        }

        public static String getBookShelfName(Uri uri) {
            return uri.getPathSegments().get(1);
        }

    }
}
