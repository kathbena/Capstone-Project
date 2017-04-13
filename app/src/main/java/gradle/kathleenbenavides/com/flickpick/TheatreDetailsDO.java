package gradle.kathleenbenavides.com.flickpick;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by kathleenbenavides on 3/10/17.
 */

public class TheatreDetailsDO implements Parcelable {

    private String id;
    private String name;

    public static final Creator<TheatreDetailsDO> CREATOR = new Creator<TheatreDetailsDO>() {
        @Override
        public TheatreDetailsDO createFromParcel(Parcel in) {
            return new TheatreDetailsDO(in);
        }

        @Override
        public TheatreDetailsDO[] newArray(int size) {
            return new TheatreDetailsDO[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
    }

    public TheatreDetailsDO() {

    }

    protected TheatreDetailsDO(Parcel in) {
        id = in.readString();
        name = in.readString();
    }
}
