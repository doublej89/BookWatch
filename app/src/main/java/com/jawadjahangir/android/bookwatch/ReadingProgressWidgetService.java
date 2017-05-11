package com.jawadjahangir.android.bookwatch;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import com.jawadjahangir.android.bookwatch.data.BooksContract;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class ReadingProgressWidgetService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_UPDATE_READING_PROGRESS_WIDGET = "com.jawadjahangir.android.bookwatch.action.FOO";

    public ReadingProgressWidgetService() {
        super("ReadingProgressWidgetService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionUpdateWidget(Context context) {
        Intent intent = new Intent(context, ReadingProgressWidgetService.class);
        intent.setAction(ACTION_UPDATE_READING_PROGRESS_WIDGET);
//        intent.putExtra(EXTRA_PARAM1, param1);
//        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
//    public static void startActionBaz(Context context, String param1, String param2) {
//        Intent intent = new Intent(context, ReadingProgressWidgetService.class);
//        intent.setAction(ACTION_BAZ);
//        intent.putExtra(EXTRA_PARAM1, param1);
//        intent.putExtra(EXTRA_PARAM2, param2);
//        context.startService(intent);
//    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_UPDATE_READING_PROGRESS_WIDGET.equals(action)) {
//                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
//                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionUpdateWidget();
            }
        }
    }

    public static final int COL_THUMBNAIL_INDEX = 0;
    public static final int COL_TITLE_INDEX = 1;
    public static final int COL_CUR_PAGE_INDEX = 2;
    public static final int COL_DATE_TIME_INDEX = 3;

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionUpdateWidget() {
        // TODO: Handle action Foo
        Cursor cursor = getContentResolver().query(
                BooksContract.SHELF_ITEM_ENTRY.buildBookWithShelfNameUri("Reading"),
                new String[]{BooksContract.SHELF_ITEM_ENTRY.COLUMN_THUMBNAIL,
                        BooksContract.SHELF_ITEM_ENTRY.COLUMN_TITLE,
                        BooksContract.SHELF_ITEM_ENTRY.COLUMN_CURRENT_PAGE,
                        BooksContract.SHELF_ITEM_ENTRY.COLUMN_DATE_TIME,
                        BooksContract.SHELF_ITEM_ENTRY._ID
                }, null, null, null
        );

        String title = "";
        String coverUrl = "";
        int curPage = 0;
        String dateTime = "";

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();

            coverUrl = cursor.getString(COL_THUMBNAIL_INDEX);
            title = cursor.getString(COL_TITLE_INDEX);
            curPage = cursor.getInt(COL_CUR_PAGE_INDEX);
            dateTime = cursor.getString(COL_DATE_TIME_INDEX);
            cursor.close();
        }

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, ReadingProgressWidgetProvider.class));
        //Now update all widgets
        ReadingProgressWidgetProvider.updateProgressWidgets(this, appWidgetManager, title, coverUrl ,curPage,
                dateTime, appWidgetIds);
}

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
