package gradle.kathleenbenavides.com.flickpick;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import gradle.kathleenbenavides.com.flickpick.DatabaseContract.movies_table;

/**
 * Created by kathleenbenavides on 3/20/17.
 * Code is based on DBHelper from SuperDuo project - Football Scores
 */

public class MoviesDBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Movies.db";
    private static final int DATABASE_VERSION = 1;
    public MoviesDBHelper(Context context)
    {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String CreateMoviesTable = "CREATE TABLE " + DatabaseContract.MOVIES_TABLE + " ("
                + movies_table.MOVIE_ID + " INTEGER PRIMARY KEY,"
                + movies_table.DATE_COL + " TEXT NOT NULL,"
                + movies_table.MOVIE_COL + " TEXT NOT NULL,"
                + movies_table.DESCRIPTION_COL + " TEXT NOT NULL,"
                + movies_table.POSTER_PATH_COL + " TEXT NOT NULL,"
                + movies_table.RATING_COL + " TEXT NOT NULL,"
                + " UNIQUE ("+movies_table.MOVIE_ID+") ON CONFLICT REPLACE );";
        sqLiteDatabase.execSQL(CreateMoviesTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //Remove old values when upgrading.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.MOVIES_TABLE);
    }
}
