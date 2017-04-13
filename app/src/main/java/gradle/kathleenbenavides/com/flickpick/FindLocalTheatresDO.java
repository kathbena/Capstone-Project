package gradle.kathleenbenavides.com.flickpick;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by kathleenbenavides on 3/15/17.
 */

public class FindLocalTheatresDO implements Parcelable{

    private String title;
    private ArrayList<ShowtimeDetailsDO> showtimes;
    private String releaseYear;
    private String longDescription;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<ShowtimeDetailsDO> getShowtimes() {
        return showtimes;
    }

    public void setShowtimes(ArrayList<ShowtimeDetailsDO> showtimes) {
        this.showtimes = showtimes;
    }

    public String getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(String releaseYear) {
        this.releaseYear = releaseYear;
    }

    public String getLongDescription() {
        return longDescription;
    }

    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }

    public static final Parcelable.Creator<FindLocalTheatresDO> CREATOR = new Parcelable.Creator<FindLocalTheatresDO>() {
        @Override
        public FindLocalTheatresDO createFromParcel(Parcel in) {
            return new FindLocalTheatresDO(in);
        }

        @Override
        public FindLocalTheatresDO[] newArray(int size) {
            return new FindLocalTheatresDO[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeTypedList(showtimes);
        dest.writeString(releaseYear);
        dest.writeString(longDescription);
    }

    public FindLocalTheatresDO() {

    }

    protected FindLocalTheatresDO(Parcel in) {
        title = in.readString();
        showtimes = new ArrayList<ShowtimeDetailsDO>();
        in.readTypedList(showtimes, ShowtimeDetailsDO.CREATOR);
        releaseYear = in.readString();
        longDescription = in.readString();
    }

}
