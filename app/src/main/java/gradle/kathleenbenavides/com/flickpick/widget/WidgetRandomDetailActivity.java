package gradle.kathleenbenavides.com.flickpick.widget;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;

import gradle.kathleenbenavides.com.flickpick.DatabaseContract;
import gradle.kathleenbenavides.com.flickpick.DetailActivity;
import gradle.kathleenbenavides.com.flickpick.MovieDetailsDO;

/**
 * Created by kathleenbenavides on 3/13/17.
 */

public class WidgetRandomDetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_ID =3;
    private MovieDetailsDO movie = new MovieDetailsDO();
    public static final int MOVIE_DATE = 1;
    public static final int MOVIE_TITLE = 2;
    public static final int MOVIE_OVERVIEW = 3;
    public static final int POSTER_PATH = 4;
    public static final int RATING = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportLoaderManager().initLoader(LOADER_ID,null,this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        //Query to get movies at random
        return new CursorLoader(this, DatabaseContract.movies_table.buildRandomMovie(),
                null,null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        //If we have data, get the first one
        if(data != null) {
            data.moveToFirst();
        }
        //Get data to add to data object to send to Detail Activity
        movie.setOriginal_title(data.getString(MOVIE_TITLE));
        movie.setRelease_date(data.getString(MOVIE_DATE));
        movie.setOverview(data.getString(MOVIE_OVERVIEW));
        movie.setPoster_path(data.getString(POSTER_PATH));
        movie.setVote_average(data.getString(RATING));
        data.close();

        //Start intent to show detail activity
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra("MOVIE_SELECTED", movie);
        intent.putExtra("STAYING_IN", true);
        startActivity(intent);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //none
    }
}
