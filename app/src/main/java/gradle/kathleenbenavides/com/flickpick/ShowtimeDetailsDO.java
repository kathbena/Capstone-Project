package gradle.kathleenbenavides.com.flickpick;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by kathleenbenavides on 3/10/17.
 */

public class ShowtimeDetailsDO implements Parcelable {

    private TheatreDetailsDO theatre;
    private String ticketURI;

    public static final Creator<ShowtimeDetailsDO> CREATOR = new Creator<ShowtimeDetailsDO>() {
        @Override
        public ShowtimeDetailsDO createFromParcel(Parcel in) {
            return new ShowtimeDetailsDO(in);
        }

        @Override
        public ShowtimeDetailsDO[] newArray(int size) {
            return new ShowtimeDetailsDO[size];
        }
    };

    public TheatreDetailsDO getTheatre() {
        return theatre;
    }

    public void setTheatre(TheatreDetailsDO theatre) {
        this.theatre = theatre;
    }

    public String getTicketURI() {
        return ticketURI;
    }

    public void setTicketURI(String ticketURI) {
        this.ticketURI = ticketURI;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(theatre, flags);
        dest.writeString(ticketURI);
    }

    public ShowtimeDetailsDO() {

    }

    protected ShowtimeDetailsDO(Parcel in) {
        theatre = in.readParcelable(TheatreDetailsDO.class.getClassLoader());
        ticketURI = in.readString();
    }
}
