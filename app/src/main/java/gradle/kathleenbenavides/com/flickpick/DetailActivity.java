package gradle.kathleenbenavides.com.flickpick;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by kathleenbenavides on 3/13/17.
 */

public class DetailActivity extends AppCompatActivity {

    private String url;
    private ArrayList<ShowtimeDetailsDO> showTimes;
    private boolean stayingIn;
    private MovieDetailsDO movieSelected;
    private FindLocalTheatresDO localMovieSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        //Check if intent exists, then assign it
        if(intent.hasExtra("STAYING_IN")){
            stayingIn = intent.getExtras().getBoolean("STAYING_IN");
        }

        setContentView(R.layout.detail_view);
        TextView title = (TextView) findViewById(R.id.title);
        ImageView poster = (ImageView) findViewById(R.id.poster);
        TextView release = (TextView) findViewById(R.id.release);
        TextView rating = (TextView) findViewById(R.id.rating);
        TextView overView = (TextView) findViewById(R.id.overview);
        Button buyMovie = (Button) findViewById(R.id.buy_movie);
        Button findTheater = (Button) findViewById(R.id.find_theater);
        Button searchAgain = (Button) findViewById(R.id.search_again);
        LinearLayout ratingLayout = (LinearLayout) findViewById(R.id.rating_layout);


        if(stayingIn) {
            //If movie data is null check if there is intent to set it
            if(movieSelected == null){
                if(intent.hasExtra("MOVIE_SELECTED")){
                    movieSelected = intent.getParcelableExtra("MOVIE_SELECTED");
                }
            }
            //If movie data is there set items for view
            if(movieSelected != null) {
                ratingLayout.setVisibility(View.VISIBLE);
                if(movieSelected.getOriginal_title() != null) {
                    title.setText(movieSelected.getOriginal_title());
                    //Set title of activity
                    setTitle(movieSelected.getOriginal_title());
                }
                if (movieSelected.getPoster_path() != null || !movieSelected.getPoster_path().isEmpty()){
                    Picasso.with(DetailActivity.this).load(movieSelected.getPoster_path()).into(poster);
                }

                if(movieSelected.getRelease_date() != null){
                    release.setText("  " + movieSelected.getRelease_date());
                }

                if(movieSelected.getVote_average() != null){
                    rating.setText("  " + movieSelected.getVote_average());
                }

                if(movieSelected.getOverview() != null){
                    overView.setText(movieSelected.getOverview());
                }

                url = getString(R.string.amazon_url) + formatTitle(movieSelected.getOriginal_title());
            }
        } else {
            findTheater.setVisibility(View.VISIBLE);
            buyMovie.setVisibility(View.GONE);
            ratingLayout.setVisibility(View.GONE);
            //If movie data is null check if there is intent to set it
            if(localMovieSelected == null){
                if(intent.hasExtra("LOCAL_MOVIE_SELECTED")){
                    localMovieSelected = intent.getParcelableExtra("LOCAL_MOVIE_SELECTED");
                }
            }
            //If movie data is there set items for view
            if(localMovieSelected != null) {
                if(localMovieSelected.getTitle() != null){
                    title.setText(localMovieSelected.getTitle());
                    setTitle(localMovieSelected.getTitle());
                }

                if(localMovieSelected.getReleaseYear() != null){
                    release.setText("  " + localMovieSelected.getReleaseYear());
                }

                if(localMovieSelected.getReleaseYear() != null) {
                    overView.setText(localMovieSelected.getLongDescription());
                }

                if(localMovieSelected.getShowtimes() != null && !localMovieSelected.getShowtimes().isEmpty()) {
                    showTimes = new ArrayList<ShowtimeDetailsDO>();
                    showTimes = cleanUpShowTimesList(localMovieSelected.getShowtimes());
                }
            }
        }

        buyMovie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Open webview for url
                Intent intent = new Intent(DetailActivity.this, WebViewActivity.class);
                intent.putExtra("URL", url);
                startActivity(intent);
            }
        });

        findTheater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Start activity to select a theatre for movie
                Intent intent = new Intent(DetailActivity.this, SelectTheatreActivity.class);
                intent.putParcelableArrayListExtra("SHOWTIMES",  showTimes);
                startActivity(intent);
            }
        });

        searchAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Return to main search page to do new search
                Intent intent = new Intent(DetailActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private ArrayList<ShowtimeDetailsDO> cleanUpShowTimesList(ArrayList<ShowtimeDetailsDO> list){

        ArrayList<ShowtimeDetailsDO> results = new ArrayList<ShowtimeDetailsDO>();

        if(list != null && !list.isEmpty()) {
            Set<String> titles = new HashSet<String>();
            for( ShowtimeDetailsDO item : list ) {
                if( titles.add( item.getTheatre().getId())) {
                    results.add( item );
                }
            }
        }
        return results;
    }

    private String formatTitle(String title){
        return title = title.replace(' ', '+');
    }

}
