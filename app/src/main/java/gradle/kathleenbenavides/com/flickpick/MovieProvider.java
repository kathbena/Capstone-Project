package gradle.kathleenbenavides.com.flickpick;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by kathleenbenavides on 3/20/17.
 * Code is highly based on content provider from SuperDuo project - Football Scores
 */

public class MovieProvider extends ContentProvider {

    private static MoviesDBHelper mOpenHelper;
    private UriMatcher uriMatcher = buildUriMatcher();
    private static final int MOVIES = 100;
    private static final int RANDOM_MOVIE = 101;
    private static final int MOVIES_WITH_DATE = 102;
    private static final String MOVIES_BY_DATE = DatabaseContract.movies_table.DATE_COL + " <= ?";


    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = DatabaseContract.BASE_CONTENT_URI.toString();
        matcher.addURI(authority, "random", RANDOM_MOVIE);
        matcher.addURI(authority, "date" , MOVIES_WITH_DATE);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new MoviesDBHelper(getContext());
        return false;
    }

    private int match_uri(Uri uri)
    {
        String link = uri.toString();
        {
            if(link.contentEquals(DatabaseContract.movies_table.buildMoviesWithDate().toString())) {
                return MOVIES_WITH_DATE;
            }
            else if(link.contentEquals(DatabaseContract.BASE_CONTENT_URI.toString())) {
                return MOVIES;
            }
            else if(link.contentEquals(DatabaseContract.movies_table.buildRandomMovie().toString())) {
                return RANDOM_MOVIE;
            }
        }
        return -1;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] strings, String s, String[] strings1, String s1) {

        Cursor retCursor;
        int match = match_uri(uri);
        switch (match) {
            case MOVIES_WITH_DATE:
                retCursor = mOpenHelper.getReadableDatabase().query(
                        DatabaseContract.MOVIES_TABLE,
                        strings,MOVIES_BY_DATE,strings1,null,null,s1); break;
            case MOVIES: retCursor = mOpenHelper.getReadableDatabase().query(
                    DatabaseContract.MOVIES_TABLE,
                    strings,null,null,null,null,s1); break;
            case RANDOM_MOVIE: retCursor = mOpenHelper.getReadableDatabase().query(
                    DatabaseContract.MOVIES_TABLE,
                    null,null,null,null,null,"RANDOM() LIMIT 1"); break;
            default: throw new UnsupportedOperationException(getContext().getString(R.string.unknown_uri) + " " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(),uri);
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = uriMatcher.match(uri);
        switch (match) {
            case RANDOM_MOVIE:
                return DatabaseContract.movies_table.CONTENT_TYPE;
            case MOVIES_WITH_DATE:
                return DatabaseContract.movies_table.CONTENT_TYPE;
            case MOVIES:
                return DatabaseContract.movies_table.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException(getContext().getString(R.string.unknown_uri) + " " + uri );
        }
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {

        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        switch (match_uri(uri)) {
            case MOVIES:
                db.beginTransaction();
                int retCount = 0;
                try {
                    for(ContentValues value : values) {
                        long _id = db.insertWithOnConflict(DatabaseContract.MOVIES_TABLE, null, value,
                                SQLiteDatabase.CONFLICT_REPLACE);
                        if (_id != -1) {
                            retCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri,null);
                return retCount;
            default:
                return super.bulkInsert(uri,values);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        return null;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }
}
