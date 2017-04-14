package gradle.kathleenbenavides.com.flickpick;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    private ArrayList<Genres> genresList = new ArrayList<>();
    private final String LOG_TAG = MainActivity.class.getSimpleName();
    private int selectedGenreID;
    private String selectedGenreName;
    //Greatest date from today
    private String greatestDateFormat;
    //Current or least date from today
    private String mostCurrentDate;
    //Setting to true since stayingIn is default value
    private boolean stayingIn = true;
    private GoogleApiClient mGoogleApiClient;

    //Permission code that will be checked in the method onRequestPermissionsResult
    private static final int LOCATION_PERMISSION_CODE = 23;
    private Location location;
    private double latitude;
    private double longitude;
    private String zipCode = "";
    private Spinner categorySpinner;
    //SavedInstanceState Constants
    private final String STAYING_IN_CONTENT = "stayingInContent";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button findFlick = (Button) findViewById(R.id.findFlickButton);
        //Create genre list
        createList();

        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        categorySpinner = (Spinner) findViewById(R.id.categories_spinner);
        //Create an ArrayAdapter for the string array and a default spinner layout
        ArrayAdapter<Genres> adapter = new ArrayAdapter<Genres>(this, android.R.layout.simple_spinner_dropdown_item, genresList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);
        //Check savedInstanceState for staying in boolean
        if(savedInstanceState != null){
            stayingIn = savedInstanceState.getBoolean(STAYING_IN_CONTENT);
        }

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                Genres genre = (Genres) adapterView.getSelectedItem();
                Log.v(LOG_TAG, "Name: " + genre.getName() + " ID: " + genre.getId());
                selectedGenreID = genre.getId();
                selectedGenreName = genre.getName();
                //Save genre selection to show when returning to activity from detail page
                int genreChoice = categorySpinner.getSelectedItemPosition();
                SharedPreferences sharedPref = getSharedPreferences("FileName",0);
                SharedPreferences.Editor prefEditor = sharedPref.edit();
                prefEditor.putInt("genreChoice",genreChoice);
                prefEditor.commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        findFlick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Check if staying in or going out
                if (!stayingIn) {
                    //If going out, check location to try to get current location
                    if (location != null) {
                        Geocoder geoCoder = new Geocoder(getApplicationContext());
                        try {
                            List<Address> matches = geoCoder.getFromLocation(latitude, longitude, 1);
                            android.location.Address bestMatch = (matches.isEmpty() ? null : matches.get(0));
                            //Check if you got a zip code
                            if (bestMatch.getPostalCode() != null && !bestMatch.getPostalCode().isEmpty()) {
                                //Able to get zip code, now set
                                zipCode = bestMatch.getPostalCode();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            //Keep URL generic
                        }
                    }
                }
                //Check if any dates are null before sending them to Detail Activity
                if(mostCurrentDate == null) {
                    setMostCurrentDate(stayingIn);
                }
                if(greatestDateFormat == null) {
                    setGreatestDate(stayingIn);
                }
                //Method to create and send data in intent
                //Last check for zipcode in case user allowed cufrent location
                //but location is off on device
                if(!stayingIn) {
                    if (zipCode == null || zipCode.isEmpty()) {
                        //Show dialog to enter zipcode since no zipcode is there for request
                        String message = getString(R.string.zipcode_update_settings) + getString(R.string.enter_zipcode_text);
                        enterZipCodeDialog(message);
                    } else {
                        sendMessage();
                    }
                } else {
                    sendMessage();
                }

            }
        });



    }

    public void createList(){
        //Add genres
        genresList.add(new Genres(28, "Action"));
        genresList.add(new Genres(12, "Adventure"));
        genresList.add(new Genres(16, "Animation"));
        genresList.add(new Genres(35, "Comedy"));
        genresList.add(new Genres(99, "Documentary"));
        genresList.add(new Genres(18, "Drama"));
        genresList.add(new Genres(10751, "Family"));
        genresList.add(new Genres(27, "Horror"));
        genresList.add(new Genres(9648, "Mystery"));
        genresList.add(new Genres(10749, "Romance"));
        genresList.add(new Genres(878, "Science Fiction"));
        genresList.add(new Genres(53, "Thriller"));
        genresList.add(new Genres(10752, "War"));
        genresList.add(new Genres(37, "Western"));
    }


    public void onRadioButtonClicked(View view) {

        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio_staying_in:
                if (checked)
                    //Staying in
                    setGreatestDate(true);
                    setMostCurrentDate(true);
                    stayingIn = true;
                    break;
            case R.id.radio_movies:
                if (checked)
                    //Going to the movies
                    setGreatestDate(false);
                    setMostCurrentDate(false);
                    stayingIn = false;
                    checkLocationPermission();
                    break;
        }
    }

    //Furthest date from today's date for movie search
    public void setGreatestDate(boolean stayingIn){
        Calendar calendar = Calendar.getInstance();

        if(!stayingIn){
            //Going out - set furthest search date to 1 month ago for latest releases
            calendar.add(Calendar.MONTH, -1);
        } else {
            //Staying in - set furthest search date to a year ago for rentals
            calendar.add(Calendar.YEAR, -1);
        }

        Date date = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        greatestDateFormat = format.format(date);
    }

    //Most current date from today's date for movie search
    public void setMostCurrentDate(boolean stayingIn){
        Calendar calendar = Calendar.getInstance();

        if(stayingIn){
            //Staying in - set most recent search date to 3 months ago for rental search
            //Average DVD/Rental time is 3 months after movie release
            calendar.add(Calendar.MONTH, -3);
        }

        Date date = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        mostCurrentDate = format.format(date);
    }


    public void sendMessage(){
        //Intent for Search Activity
        Intent intent = new Intent(this, SearchActivity.class);
        intent.putExtra("EXTRA_GENRE", selectedGenreID);
        intent.putExtra("EXTRA_GENRE_NAME", selectedGenreName);
        intent.putExtra("EXTRA_GREATEST_DATE", greatestDateFormat);
        intent.putExtra("EXTRA_MOST_CURRENT_DATE", mostCurrentDate);
        intent.putExtra("STAYING_IN", stayingIn);
        intent.putExtra("ZIPCODE", zipCode);
        startActivity(intent);

    }

    //Following check permissions guidelines and code from https://developer.android.com/training/permissions/requesting.html#perm-request
    public void checkLocationPermission() {
        //Get permission status
        int result = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION);
        if (result != PackageManager.PERMISSION_GRANTED) {
            if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)){
                //Show dialog to use to get permission
                Log.i(LOG_TAG, getString(R.string.zipcode_update_settings));
                String message = getString(R.string.zipcode_update_settings) + getString(R.string.enter_zipcode_text);
                enterZipCodeDialog(message);

            } else {
                //Request the permission
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        LOCATION_PERMISSION_CODE);
            }
        }
    }

    //Following check permissions guidelines and code from https://developer.android.com/training/permissions/requesting.html#perm-request
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch(requestCode) {
            case LOCATION_PERMISSION_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    Log.v(LOG_TAG, getString(R.string.current_location_granted_log));

                    if (location != null) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                    }
                } else {
                    Log.v(LOG_TAG, getString(R.string.denied_current_Location));
                    // Permission was denied
                    // Show alert to enter zipcode
                    String message = getString(R.string.user_denied_current_location) + getString(R.string.enter_zipcode_text);
                    enterZipCodeDialog(message);
                }
                return;
            }
        }
    }

    //Zipcode dialog since it is used in multiple places
    public void enterZipCodeDialog(String message){
        // functionality that depends on this permission.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.zipcode_title));
        if(message != null){
            builder.setMessage(message);
        }
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                zipCode = input.getText().toString();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        //Check the permission
        try {
            location = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            if (location != null) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
            }
        } catch (SecurityException e) {
            Log.e(LOG_TAG, getString(R.string.error_current_location));
        }

    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(LOG_TAG, getString(R.string.connection_failed) + connectionResult.getErrorCode());
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Get genre selection and set spinner with it
        SharedPreferences sharedPref = getSharedPreferences("FileName",MODE_PRIVATE);
        int spinnerPosition = sharedPref.getInt("genreChoice",-1);
        if(spinnerPosition != -1) {
            // set the value of the spinner
            categorySpinner.setSelection(spinnerPosition);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Clear genre selection from shared prefs when leaving app
        SharedPreferences sharedPref = getSharedPreferences("FileName", MODE_PRIVATE);
        sharedPref.edit().remove("genreChoice").commit();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //Add genreChoice to shared prefs for return to activity
        SharedPreferences sharedPref = getSharedPreferences("FileName",MODE_PRIVATE);
        int spinnerPosition = sharedPref.getInt("genreChoice",-1);
        if(spinnerPosition != -1) {
            // set the value of the spinner
            categorySpinner.setSelection(spinnerPosition);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putBoolean(STAYING_IN_CONTENT, stayingIn);
    }

}
