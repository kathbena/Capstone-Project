package gradle.kathleenbenavides.com.flickpick;

/**
 * Created by kathleenbenavides on 3/10/17.
 */

public class Genres {

    private int id;
    private String name;

    public Genres(int id, String name){
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    //override the string to display the string in spinner
    @Override
    public String toString() {
        return name;
    }

}
