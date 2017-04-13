package gradle.kathleenbenavides.com.flickpick;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by kathleenbenavides on 3/10/17.
 */

public class MovieDetailsDO implements Parcelable {

    private String id;
    private String original_title;
    private String release_date;
    private String vote_average;
    private String poster_path;
    private String overview;

    public static final Creator<MovieDetailsDO> CREATOR = new Creator<MovieDetailsDO>() {
        @Override
        public MovieDetailsDO createFromParcel(Parcel in) {
            return new MovieDetailsDO(in);
        }

        @Override
        public MovieDetailsDO[] newArray(int size) {
            return new MovieDetailsDO[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOriginal_title() {
        return original_title;
    }

    public void setOriginal_title(String original_title) {
        this.original_title = original_title;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public String getVote_average() {
        return vote_average;
    }

    public void setVote_average(String vote_average) {
        this.vote_average = vote_average;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(original_title);
        dest.writeString(release_date);
        dest.writeString(vote_average);
        dest.writeString(poster_path);
        dest.writeString(overview);
    }

    public MovieDetailsDO() {

    }

    protected MovieDetailsDO(Parcel in) {
        id = in.readString();
        original_title = in.readString();
        release_date = in.readString();
        vote_average = in.readString();
        poster_path = in.readString();
        overview = in.readString();
    }
}
