package gradle.kathleenbenavides.com.flickpick.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import gradle.kathleenbenavides.com.flickpick.R;

/**
 * Created by kathleenbenavides on 3/20/17.
 * Allows user to quickly select a random movie to view
 */

public class MovieAppWidgetProvider extends AppWidgetProvider {

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        ComponentName widget = new ComponentName(context, MovieAppWidgetProvider.class);
        int[] allIds = appWidgetManager.getAppWidgetIds(widget);

        for(int widgetID : allIds) {
            //Create intent to launch Activity
            Intent intent = new Intent(context, WidgetRandomDetailActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            //Get layout for app widget and attach on click listener
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.appwidget_provider_layout);
            views.setOnClickPendingIntent(R.id.widget_view, pendingIntent);

            //AppWidgetManager to show widget
            appWidgetManager.updateAppWidget(widgetID, views);
        }


    }
}
