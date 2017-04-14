package gradle.kathleenbenavides.com.flickpick;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by kathleenbenavides on 3/13/17.
 */

public class SelectTheatreActivity extends AppCompatActivity{

    private ListView listView;
    private ArrayList<String> theatreNames;
    //SavedInstanceState Constants
    private final String THEATRE_CONTENT = getString(R.string.theatre_content);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final ArrayList<ShowtimeDetailsDO> showTimes = getIntent().getParcelableArrayListExtra("SHOWTIMES");

        if(savedInstanceState == null){
            //Check if there are no theatre names then add them from showtimes data
            if(theatreNames == null){
                theatreNames = new ArrayList<String>();

                if(showTimes != null && !showTimes.isEmpty()){
                    //Get each showtime detail and add to new list of Theatre names for view
                    for(ShowtimeDetailsDO showTimeDetail: showTimes){
                        String theatreRow = showTimeDetail.getTheatre().getName();
                        theatreNames.add(theatreRow);
                    }
                }
            }
        } else {
            theatreNames = (ArrayList<String>) savedInstanceState.get(THEATRE_CONTENT);
        }


        setContentView(R.layout.theatre_list_view);
        listView = (ListView) findViewById(R.id.list);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, theatreNames);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Send ticket url to webview on selection of theatre
                String url = showTimes.get(i).getTicketURI();
                Intent intent = new Intent(SelectTheatreActivity.this, WebViewActivity.class);
                intent.putExtra("URL", url);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);

        if(theatreNames != null){
            outState.putStringArrayList(THEATRE_CONTENT, theatreNames);
        }
    }


}
