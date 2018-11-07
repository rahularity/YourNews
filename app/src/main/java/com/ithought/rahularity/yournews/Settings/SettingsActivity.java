package com.ithought.rahularity.yournews.Settings;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ithought.rahularity.yournews.FilterNews.FilterNewsActivity;
import com.ithought.rahularity.yournews.Home.HomeActivity;
import com.ithought.rahularity.yournews.R;
import com.ithought.rahularity.yournews.Utils.BottomNavigationViewHelper;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.util.Calendar;

public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = "SettingsActivity";
    public static final int ACTIVITY_NUM = 2;
    private Context context = this;
    private TextView mDate, address;
    private String date_string, country_string;
    private Button save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        setupBottomNavigationView(); //setting up bottom navigation view
        init(); //initialising all the private data members at one place

        mDate.setText(FilterNewsActivity.date);
        address.setText(HomeActivity.address);

        mDate.setOnClickListener(view -> getDate());
        save.setOnClickListener(view -> saveSetting());
    }

    private void getDate() {
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, monthOfYear, dayOfMonth) -> mDate.setText(year +  "-" + (monthOfYear + 1) + "-" + dayOfMonth  ), mYear, mMonth, mDay);
        datePickerDialog.show();

    }



    private void saveSetting() {

        date_string =mDate.getText().toString();
        FilterNewsActivity.date = date_string;
        Toast.makeText(this, "Settings saved", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(SettingsActivity.this, FilterNewsActivity.class));

    }

    private void init() {
        mDate = findViewById(R.id.date);
        save = findViewById(R.id.save);
        address = findViewById(R.id.address);
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
