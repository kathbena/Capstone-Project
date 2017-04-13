package gradle.kathleenbenavides.com.flickpick;

import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Vector;

import gradle.kathleenbenavides.com.flickpick.FetchMoviesTask.CompletedTask;

/**
 * Created by kathleenbenavides on 3/10/17.
 * Some code is from previous Movies project
 */

public class SearchActivity extends AppCompatActivity {

    private final String LOG_TAG = SearchActivity.class.getSimpleName();
    //arraylist details object
    private ArrayList<MovieDetailsDO> movieDetails;
    private ArrayList<FindLocalTheatresDO> localMovies;
    private String requestURL;
    private String genreID;
    private String genreName;
    private String greatestDate;
    private String mostCurrentDate;
    private boolean stayingIn;
    private String zipCode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_page);

        // Get the Intent that started this activity and extract the string
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            genreID = Integer.toString(extras.getInt("EXTRA_GENRE"));
            genreName = extras.getString("EXTRA_GENRE_NAME");
            greatestDate = extras.getString("EXTRA_GREATEST_DATE");
            mostCurrentDate = extras.getString("EXTRA_MOST_CURRENT_DATE");
            stayingIn = extras.getBoolean("STAYING_IN");
            zipCode = extras.getString("ZIPCODE");
        }
        //Animate the popcorn on page
        animatePopcorn();
        //Set delay of search to show animating popcorn
        setDelay();
    }

    public void animatePopcorn() {
        //Start animation of popcorn on start of page
        ImageView popcornImage = (ImageView) findViewById(R.id.popcorn_image);
        Animation startAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate_animation);
        if(popcornImage != null && startAnimation != null){
            popcornImage.startAnimation(startAnimation);
            getWindow().getDecorView().announceForAccessibility(getString(R.string.accessibility_searching));
        }

    }

    public void setDelay(){
        final Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //If we have movie details show them, otherwise perform new search
                if (movieDetails != null) {
                    if(!movieDetails.isEmpty()){
                        showMovieDialog(movieDetails);
                    } else {
                        //Log no more movies and show dialog for new search
                        Log.i(LOG_TAG, getString(R.string.no_more_movies));
                        showNoResultsDialog();
                    }
                } else if (localMovies != null) {
                    if(!localMovies.isEmpty()){
                        showMovieTheatreDialog(localMovies);
                    } else {
                        //Log no more movies and show dialog for new search
                        Log.i(LOG_TAG, getString(R.string.no_more_movies));
                        showNoResultsDialog();
                    }
                } else {
                    //Execute search after 5s to show animating popcorn
                    //download data from below url
                    if(stayingIn){
                        //If staying in, construct moviedb request
                        //Get api key from buildConfig
                        String apiKey = BuildConfig.moviedb_api_key;
                        requestURL = getString(R.string.moviedb_popular_url) +
                                apiKey +
                                getString(R.string.moviedb_primary_release_date_gte) + greatestDate +
                                getString(R.string.moviedb_primary_release_date_lte) + mostCurrentDate +
                                getString(R.string.moviedb_with_genre) + genreID;
                        startMovieAsyncTask(requestURL);
                    } else {
                        //If going out, construct graceNote request
                        //Get api key from buildConfig
                        String apiKey = BuildConfig.gracenote_api_key;
                        requestURL = getString(R.string.gracenote_url) + apiKey +
                                getString(R.string.gracenote_startDate) + mostCurrentDate +
                                getString(R.string.gracenote_zip) + zipCode;
                        startMoviesTheatresTask(requestURL);
                    }
                    Log.v(LOG_TAG, getString(R.string.request_url_text) + requestURL);
                }
            }
        }, 5000);
    }


    //Request for all movies when staying in
    public void startMovieAsyncTask(String requestURL){
        FetchMoviesTask task = new FetchMoviesTask(new CompletedTask() {
            @Override
            public void completedTask(String movies) {
                try {
                    //Announce for accessibility search has started
                    getWindow().getDecorView().announceForAccessibility(getString(R.string.accessibility_done_searching));
                    //If movies is null and not empty get movies from json
                    if(movies != null && !movies.isEmpty()){
                        movieDetails = new ArrayList<MovieDetailsDO>();
                        movieDetails = getMovieDataFromJson(movies);
                        if(!movieDetails.isEmpty()){
                            showMovieDialog(movieDetails);
                        }
                    } else {
                        //No movies came back
                        showNoResultsDialog();
                    }
                } catch (JSONException e) {
                    Log.e(LOG_TAG, e.getMessage(), e);
                    e.printStackTrace();
                }
            }

            @Override
            public void errorCompletedTask() {
                //Announce for accessibility search is done
                getWindow().getDecorView().announceForAccessibility(getString(R.string.accessibility_done_searching));
                //Error searching, display error and return to main page
                showErrorSearchingDialog();
            }
        });
        task.execute(requestURL);
    }

    //Request for movie in theatres near user for going out
    public void startMoviesTheatresTask(String requestURL){
        FetchMoviesTask task = new FetchMoviesTask(new CompletedTask() {
            @Override
            public void completedTask(String movieResult) {
                try{
                    //Announce for accessibility search has started
                    getWindow().getDecorView().announceForAccessibility(getString(R.string.accessibility_searching));
                    if(movieResult != null && !movieResult.isEmpty()){
                        localMovies = new ArrayList<FindLocalTheatresDO>();
                        localMovies = getTheatreMoviesFromJson(movieResult);
                        if(!localMovies.isEmpty()){
                            showMovieTheatreDialog(localMovies);
                        }
                    } else {
                        //No movies came back
                        showNoResultsDialog();
                    }
                } catch (JSONException e) {
                    Log.e(LOG_TAG, e.getMessage(), e);
                    e.printStackTrace();
                }
            }

            @Override
            public void errorCompletedTask() {
                //Announce for accessibility search is done
                getWindow().getDecorView().announceForAccessibility(getString(R.string.accessibility_done_searching));
                //Error searching, display error and return to main page
                showErrorSearchingDialog();

            }
        });
        task.execute(requestURL);
    }

    //No Results Dialog
    public void showNoResultsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SearchActivity.this);

        builder.setTitle(getString(R.string.no_results_title));
        builder.setMessage(getString(R.string.no_results_message));

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Return to main page
                Intent intent = new Intent(SearchActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    //Error searching Dialog
    public void showErrorSearchingDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(SearchActivity.this);

        builder.setTitle(getString(R.string.error_searching));
        builder.setMessage(getString(R.string.error_searching_message));

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Return to main page
                Intent intent = new Intent(SearchActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Take the String representing the result of movies in JSON Format and
     * pull out the data we need to construct the Strings needed for the wireframes.
     */
    private ArrayList<MovieDetailsDO> getMovieDataFromJson(String movieJsonStr)
            throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        final String results = "results";
        final String MOVIEID = "id";
        final String ORIGINAL_TITLE = "original_title";
        final String OVERVIEW = "overview";
        final String RELEASE_DATE = "release_date";
        final String POSTER_PATH = "poster_path";
        final String VOTE_AVERAGE = "vote_average";

        //Match data for db used for widget
        String movie_title = null;
        String movie_date = null;
        String movie_id = null;
        String overview = null;
        String poster_path = null;
        String rating = null;

        JSONObject movieJson = new JSONObject(movieJsonStr);
        JSONArray movieArray = movieJson.getJSONArray(results);

        //ContentValues to be inserted for db
        Vector<ContentValues> values = new Vector <ContentValues> (movieArray.length());

        for (int i = 0; i < movieArray.length(); i++) {
            MovieDetailsDO detail = new MovieDetailsDO();

            // Set details for each movie and add to arraylist
            JSONObject movie = movieArray.getJSONObject(i);
            movie_id = movie.getString(MOVIEID);
            detail.setId(movie.getString(MOVIEID));
            movie_title = movie.getString(ORIGINAL_TITLE);
            detail.setOriginal_title(movie.getString(ORIGINAL_TITLE));
            detail.setOverview(movie.getString(OVERVIEW));
            overview = movie.getString(OVERVIEW);
            detail.setRelease_date(movie.getString(RELEASE_DATE));
            movie_date = movie.getString(RELEASE_DATE);
            // Get the poster url and construct it
            String poster = movie.getString(POSTER_PATH);
            detail.setPoster_path(constructPosterURL(poster));
            poster_path = constructPosterURL(poster);
            detail.setVote_average(movie.getString(VOTE_AVERAGE));
            rating = movie.getString(VOTE_AVERAGE);

            //Save list of movies to db for widget consumption
            ContentValues movie_values = new ContentValues();
            movie_values.put(DatabaseContract.movies_table.MOVIE_ID,movie_id);
            movie_values.put(DatabaseContract.movies_table.DATE_COL,movie_date);
            movie_values.put(DatabaseContract.movies_table.MOVIE_COL,movie_title);
            movie_values.put(DatabaseContract.movies_table.DESCRIPTION_COL,overview);
            movie_values.put(DatabaseContract.movies_table.POSTER_PATH_COL, poster_path);
            movie_values.put(DatabaseContract.movies_table.RATING_COL, rating);

            movieDetails.add(detail);
            values.add(movie_values);
        }
        //Insert data into db for widget use
        int inserted_data = 0;
        ContentValues[] insert_data = new ContentValues[values.size()];
        values.toArray(insert_data);
        inserted_data = getApplicationContext().getContentResolver().bulkInsert(
                DatabaseContract.BASE_CONTENT_URI,insert_data);

        return movieDetails;
    }

    /**
     * Take the String representing the result of a movie in JSON Format and
     * pull out the data we need to construct the Strings needed for the wireframes.
     */
    private ArrayList<FindLocalTheatresDO> getTheatreMoviesFromJson(String movieJsonStr)
            throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        final String TITLE = "title";
        final String SHOWTIMES = "showtimes";
        final String RELEASEYEAR = "releaseYear";
        final String LONGDESCRIPTION = "longDescription";
        final String GENRES = "genres";
        final String TICKETURI = "ticketURI";
        final String THEATRE = "theatre";
        final String ID = "id";
        final String NAME = "name";
        int movieCount;
        boolean contains = false;

        JSONArray movieJson = new JSONArray(movieJsonStr);
        if(movieJson.length() >= 10){
            movieCount = 10;
        } else {
            movieCount = movieJson.length();
        }

        for (int i = 0; i < movieCount; i++) {
            FindLocalTheatresDO localMovie = new FindLocalTheatresDO();

            // Set details for each movie and theatre and add to arraylist
            JSONObject object = movieJson.getJSONObject(i);
            localMovie.setTitle(object.getString(TITLE));
            if(object.has(RELEASEYEAR)) {
                localMovie.setReleaseYear(object.getString(RELEASEYEAR));
            }
            if(object.has(LONGDESCRIPTION)) {
                localMovie.setLongDescription(object.getString(LONGDESCRIPTION));
            }

            if(object.has(GENRES)) {
                ArrayList<String> genres = new ArrayList<String>();
                JSONArray genreArray = object.getJSONArray(GENRES);

                contains = false;
                for (int k = 0; k < genreArray.length(); k++){
                    String movieGenre = genreArray.getString(k);
                    String strLower = genreName.toLowerCase();
                    //If genre name does not match selected, do not add movie
                    if (genreName.equalsIgnoreCase(movieGenre) || movieGenre.contains(strLower)) {
                        contains = true;
                        break;
                    }
                }
            }

            ArrayList<ShowtimeDetailsDO> showtimeDetails = new ArrayList<ShowtimeDetailsDO>();
            JSONArray showtimeArray = object.getJSONArray(SHOWTIMES);

            for (int j = 0; j < showtimeArray.length(); j++){
                ShowtimeDetailsDO movieDetails = new ShowtimeDetailsDO();

                JSONObject showtimeObject = showtimeArray.getJSONObject(j);

                movieDetails.setTicketURI(showtimeObject.getString(TICKETURI));

                TheatreDetailsDO theatreDetails = new TheatreDetailsDO();
                JSONObject theatreObject = showtimeObject.getJSONObject(THEATRE);
                theatreDetails.setId(theatreObject.getString(ID));
                theatreDetails.setName(theatreObject.getString(NAME));

                movieDetails.setTheatre(theatreDetails);
                showtimeDetails.add(movieDetails);
            }

            localMovie.setShowtimes(showtimeDetails);
            if(contains == true){
                localMovies.add(localMovie);
            }
        }

        return localMovies;
    }

    //Dialog to show each movie for user to select to view
    public void showMovieDialog(final ArrayList<MovieDetailsDO> result) {

        final MovieDetailsDO movieItem = result.get(0);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String title = movieItem.getOriginal_title();
        String description = movieItem.getOverview();

        if(title == null){
            title = getString(R.string.title_unavailable);
        }

        if(description == null){
            description = getString(R.string.overview_unavailable);
        }

        builder.setTitle(title);
        builder.setMessage(description);

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Intent to start details page and send data of movie over
                Intent intent = new Intent(SearchActivity.this, DetailActivity.class);
                intent.putExtra("MOVIE_SELECTED", movieItem);
                intent.putExtra("STAYING_IN", stayingIn);
                startActivity(intent);
            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.i(LOG_TAG, getString(R.string.removing_movie_text));
                animatePopcorn();
                setDelay();
                //Remove the result from the list so the next movie is at the top
                result.remove(0);
            }
        });

        AlertDialog dialog = builder.create();
        //Check if an activity is finishing
        //helps prevent crashes if user goes back and forth from search page to main activity
        if(!((Activity) this).isFinishing()) {
            //Show the dialog
            dialog.show();
        }
    }

    //Dialog to show each movie for user to select to view
    public void showMovieTheatreDialog(final ArrayList<FindLocalTheatresDO> result) {

        final FindLocalTheatresDO localMovie = result.get(0);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String title = localMovie.getTitle();
        String description = localMovie.getLongDescription();

        if(title == null){
            title = "Movie title is currently unavailable";
        }

        if(description == null){
            description = "Movie description is currently unavailable";
        }

        builder.setTitle(title);
        builder.setMessage(description);

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Intent to start details page and send data of movie over
                Intent intent = new Intent(SearchActivity.this, DetailActivity.class);
                intent.putExtra("LOCAL_MOVIE_SELECTED", localMovie);
                intent.putExtra("STAYING_IN", stayingIn);
                startActivity(intent);
            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.i(LOG_TAG, getString(R.string.removing_movie_text));
                animatePopcorn();
                setDelay();
                //Remove the result from the list so the next movie is at the top
                result.remove(0);
            }
        });
        AlertDialog dialog = builder.create();
        //Check if an activity is finishing
        //helps prevent crashes if user goes back and forth from search page to main activity
        if(!((Activity) this).isFinishing()) {
            //Show the dialog
            dialog.show();
        }

    }

    //This function constructs the poster url and size for requesting in Picasso
    public String constructPosterURL(String poster) {
        String baseURL = getString(R.string.base_poster_url);
        String posterSize = "w500";
        //If poster is null assign empty
        //Will check for this on detail page
        if(poster == null) {
            poster = "";
        }
        String posterPath = baseURL + posterSize + poster;

        return posterPath;
    }



}
