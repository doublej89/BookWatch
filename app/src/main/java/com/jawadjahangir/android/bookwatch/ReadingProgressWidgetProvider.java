package com.jawadjahangir.android.bookwatch;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.RemoteViews;

import com.squareup.picasso.Picasso;

/**
 * Implementation of App Widget functionality.
 */
public class ReadingProgressWidgetProvider extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, String title,
                                String coverUrl, int curPage, String dateTime, int appWidgetId, int[] appWidgetIds) {
        Intent intent = new Intent(context, NowReading.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.reading_progress_widget_provider);
        views.setOnClickPendingIntent(R.id.widget_view, pendingIntent);
        views.setTextViewText(R.id.widget_title, title);

        if (!TextUtils.isEmpty(coverUrl))
            Picasso.with(context).load(coverUrl).placeholder(R.drawable.no_cover_thumb).into(views, R.id.widget_book_cover, appWidgetIds);
        else
            views.setImageViewResource(R.id.widget_book_cover, R.drawable.no_cover_thumb);

        views.setTextViewText(R.id.widget_bookmarked_page, Integer.toString(curPage));
        views.setTextViewText(R.id.widget_bookmark_date_time, dateTime);


        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        ReadingProgressWidgetService.startActionUpdateWidget(context);
    }

    public static void updateProgressWidgets(Context context, AppWidgetManager appWidgetManager, String title,
                                             String coverUrl, int curPage, String dateTime, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, title, coverUrl, curPage, dateTime, appWidgetId, appWidgetIds);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

