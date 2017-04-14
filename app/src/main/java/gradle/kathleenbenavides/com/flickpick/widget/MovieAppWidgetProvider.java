package gradle.kathleenbenavides.com.flickpick.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.widget.RemoteViews;

import com.squareup.picasso.Picasso;

import gradle.kathleenbenavides.com.flickpick.DatabaseContract;
import gradle.kathleenbenavides.com.flickpick.DetailActivity;
import gradle.kathleenbenavides.com.flickpick.MovieDetailsDO;
import gradle.kathleenbenavides.com.flickpick.R;

/**
 * Created by kathleenbenavides on 3/20/17.
 */

public class MovieAppWidgetProvider extends AppWidgetProvider {

    public static final int MOVIE_DATE = 1;
    public static final int MOVIE_TITLE = 2;
    public static final int MOVIE_OVERVIEW = 3;
    public static final int POSTER_PATH = 4;
    public static final int RATING = 5;
    private MovieDetailsDO movie = new MovieDetailsDO();
    private final String LOG_TAG = MovieAppWidgetProvider.class.getSimpleName();

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        ComponentName widget = new ComponentName(context, MovieAppWidgetProvider.class);
        int[] allIds = appWidgetManager.getAppWidgetIds(widget);

        for(int widgetID : allIds) {
            String testString = null;

           //Make call to get data from db
            CursorLoader loader = new CursorLoader(context, DatabaseContract.movies_table.buildRandomMovie(),
                    null,null,null,null);
            Cursor data = loader.loadInBackground();

            Log.v(LOG_TAG, String.valueOf(data.getCount()));
            //Add data to master list for display
            if(data != null) {
                data.moveToFirst();
            }

            testString = data.getString(MOVIE_TITLE);
            movie.setOriginal_title(testString);
            movie.setRelease_date(data.getString(MOVIE_DATE));
            movie.setOverview(data.getString(MOVIE_OVERVIEW));
            movie.setPoster_path(data.getString(POSTER_PATH));
            movie.setVote_average(data.getString(RATING));
            data.close();

            //Create intent to launch Activity
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra("MOVIE_SELECTED", movie);
            intent.putExtra("STAYING_IN", true);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            //Get layout for app widget and attach on click listener
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.appwidget_provider_layout);
            views.setOnClickPendingIntent(R.id.widget_view, pendingIntent);

            //Set the data for the view - first upcoming game
            if(testString != null && movie.getPoster_path() != null){
                Picasso.with(context).load(movie.getPoster_path()).into(views, R.id.poster, new int[] {widgetID});
                views.setTextViewText(R.id.movie_title, testString);
            } else {
                views.setTextViewText(R.id.movie_title, context.getString(R.string.widget_nomovies));
            }

            //AppWidgetManager performs update on current widget
            appWidgetManager.updateAppWidget(widgetID, views);
        }


    }
}
