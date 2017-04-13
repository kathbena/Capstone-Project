package gradle.kathleenbenavides.com.flickpick;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by kathleenbenavides on 3/20/17.
 */

public class DatabaseContract {
    public static final String MOVIES_TABLE = "movies_table";
    public static final class movies_table implements BaseColumns
    {
        //Table data contants
        public static final String MOVIE_COL = "movie";
        public static final String MOVIE_ID = "movie_id";
        public static final String DATE_COL = "date";
        public static final String DESCRIPTION_COL = "description";
        public static final String POSTER_PATH_COL = "poster_path";
        public static final String RATING_COL = "rating";

        //Content Type
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH;

        /*public static Uri buildMoviesAllMovies()
        {
            return BASE_CONTENT_URI.buildUpon().appendPath("allmovies").build();
        }*/
        public static Uri buildRandomMovie()
        {
            return BASE_CONTENT_URI.buildUpon().appendPath("random").build();
        }
        public static Uri buildMoviesWithDate()
        {
            return BASE_CONTENT_URI.buildUpon().appendPath("date").build();
        }
    }
    //URI data for db
    public static final String CONTENT_AUTHORITY = "gradle.kathleenbenavides.com.flickpick";
    public static final String PATH = "movies";
    public static Uri BASE_CONTENT_URI = Uri.parse("content://"+CONTENT_AUTHORITY);
}
