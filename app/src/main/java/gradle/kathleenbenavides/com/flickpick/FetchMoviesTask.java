package gradle.kathleenbenavides.com.flickpick;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by kathleenbenavides on 12/9/15.
 */
public class FetchMoviesTask extends AsyncTask<String, Void, String> {

    private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();
    private String requestURL;
    public CompletedTask delegate = null;

    //Set the delegate
    public FetchMoviesTask(CompletedTask delegate) {
        this.delegate = delegate;
    }

    // Error logging and buffer reader is used from my Sunshine app sample project creation
    @Override
    protected String doInBackground(String... params) {
        requestURL = params[0];

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String movieJsonStr = null;
        try {
            Uri builtUri = Uri.parse(requestURL).buildUpon()
                    .build();

            URL url = new URL(builtUri.toString());
            Log.v(LOG_TAG, "Built URI " + builtUri.toString());

            // Create the request to MovieDB, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                Log.v(LOG_TAG, "InputStream is null");
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline for easier reading
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                //buffer is empty, log and return
                Log.v(LOG_TAG, "Buffer length is 0");
                return null;
            }
            movieJsonStr = buffer.toString();
            Log. v(LOG_TAG, "Movie string: " + movieJsonStr);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            //Callback to return them back to main page
            delegate.errorCompletedTask();

            return null;
        } finally{
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
        return movieJsonStr;
    }

    @Override
    protected void onPostExecute(String result) {
        delegate.completedTask(result);
    }

    public interface CompletedTask {
        void completedTask(String result);
        void errorCompletedTask();
    }


}
