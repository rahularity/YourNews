package com.ithought.rahularity.yournews.Home;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.ithought.rahularity.yournews.Adapters.ArticleAdapter;
import com.ithought.rahularity.yournews.Models.Article;
import com.ithought.rahularity.yournews.Models.News;
import com.ithought.rahularity.yournews.R;
import com.ithought.rahularity.yournews.RetrofitClient;
import com.ithought.rahularity.yournews.Utils.BottomNavigationViewHelper;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.PriorityQueue;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 1;
    private FusedLocationProviderClient mFusedLocationClient;
    private Geocoder geocoder;
    public static List<Address> addresses;

    private static final String TAG = "HomeActivity";
    public static final int ACTIVITY_NUM = 0;
    private Context context = this;
    private ProgressBar progress;
    private RecyclerView resultList;
    private TextView instructionText;
    private TextView locationText;
    private static final String ApiKey = "c2130699553c41a9b1089d35ba2faa0e";
    public static String city,state,country,address, knownName, postalCode;
    private HashMap<String, String> country_map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Log.d(TAG, "onCreate: In the OnCreate Method");

        setupBottomNavigationView(); //setting up bottom navigation view
        init(); //initialising all the private data members at one place
        setting_country_hashmap();

        checkLocationPermission();

    }

    private void setting_country_hashmap() {
        country_map.put("India","in");
        country_map.put("USA", "us");
        country_map.put("UK", "gb");
        country_map.put("South Africa", "za");
        country_map.put("Argentina", "ar");
    }

    private void checkLocationPermission() {

        if(ContextCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){

            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {

                        Log.d(TAG, "onSuccess: getting location now");
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            Log.d(TAG, "onSuccess: location latitude is "+ location.getLatitude());
                            Double latitude = location.getLatitude();
                            Double longitude = location.getLongitude();

                            try {
                                addresses = geocoder.getFromLocation( latitude, longitude, 1);
                                address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                                city = addresses.get(0).getLocality();
                                state = addresses.get(0).getAdminArea();
                                country = addresses.get(0).getCountryName();
                                postalCode = addresses.get(0).getPostalCode();
                                knownName = addresses.get(0).getFeatureName();

                                Log.d(TAG, "onSuccess: country is " + country);

                                //locationText.setText(city + ", " + state + ", " + country);
                                locationText.setText(address);
                                fetchingNews(); //GET request on the API for getting all the latest news location wise.

                            } catch (IOException e) {
                                e.printStackTrace();
                            }


                        }else{
                            Toast.makeText(context,"unable to fetch your location at this time", Toast.LENGTH_LONG).show();
                        }
                    });
        }else{

            requestLocationPermission();

        }
    }

    private void requestLocationPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)){

            new AlertDialog.Builder(this)
                    .setTitle("Location Permission")
                    .setMessage("Location permission is required to get news from your current location.")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //Prompt the user once explanation has been shown
                            ActivityCompat.requestPermissions(HomeActivity.this,
                                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                    MY_PERMISSIONS_REQUEST_LOCATION);

                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .create()
                    .show();

        }else{
            // No explanation needed; request the permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == MY_PERMISSIONS_REQUEST_LOCATION){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //Permission is granted
                finish();
                startActivity(getIntent());

            }else{
                //Permission is not granted
            }
        }
    }


    private void fetchingNews() {
        progress.setVisibility(View.VISIBLE);
        instructionText.setVisibility(View.GONE);

        String country_code = country_map.get(country);
        Call<News> call = RetrofitClient
                .getInstance(this)
                .getApi()
                .getNews("top-headlines",country_code, ApiKey); //getting top-headlines from India

        Log.d(TAG, "fetchingNews: country code is " + country_code);

        enqueueCall(call);
    }

    private void enqueueCall(Call<News> call) {
        call.enqueue(new Callback<News>() {
            @Override
            public void onResponse(Call<News> call, Response<News> response) {
                Log.d(TAG, "onResponse: Server Response" + response.toString());

                int responseCode = response.code();

                if(responseCode == 200){
                    //TODO: Fetching news from the current location of the user and displaying top and breaking news.
                    String status = response.body().getStatus();
                    int totalResults = response.body().getTotalResults();

                    if (status.equals("ok")){

                        List<Article> articles= response.body().getArticles();
                        resultList.setAdapter(new ArticleAdapter(articles,R.layout.news_list_view,getApplicationContext()));
                        progress.setVisibility(View.GONE);

                    }

                }else{
                    //TODO: when there is nothing to fetch from the API
                    progress.setVisibility(View.GONE);
                    instructionText.setVisibility(View.VISIBLE);
                    Toast.makeText(context, "Something went wrong...please try again.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<News> call, Throwable t) {
                Log.e(TAG, "onFailure: something went wrong." + t.getMessage());
                progress.setVisibility(View.GONE);
                instructionText.setVisibility(View.VISIBLE);
                Toast.makeText(HomeActivity.this,"Please check your Internet connection.",Toast.LENGTH_LONG).show();
            }
        });
    }

    private void init() {
        geocoder = new Geocoder(this, Locale.getDefault());
        country_map = new HashMap<>();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationText = findViewById(R.id.location_text);
        instructionText = (TextView)findViewById(R.id.instruction_text);
        progress = (ProgressBar)findViewById(R.id.progress);
        resultList = (RecyclerView)findViewById(R.id.result_list);
        resultList.setLayoutManager(new LinearLayoutManager(context));
    }

    /**
     *BottomNavigationView setup
     */
    private void setupBottomNavigationView(){
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx)findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.setUpBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(context,bottomNavigationViewEx);

        //getting menu item so as to highlight the correct BottomNavigationItem
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }

}
