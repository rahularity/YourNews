package com.ithought.rahularity.yournews.FilterNews;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.ithought.rahularity.yournews.Adapters.ArticleAdapter;
import com.ithought.rahularity.yournews.Models.Article;
import com.ithought.rahularity.yournews.Models.News;
import com.ithought.rahularity.yournews.R;
import com.ithought.rahularity.yournews.RetrofitClient;
import com.ithought.rahularity.yournews.Utils.BottomNavigationViewHelper;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FilterNewsActivity extends AppCompatActivity {

    private static final String TAG = "FilterNewsActivity";
    public static final int ACTIVITY_NUM = 1;
    private Context context = this;
    private ProgressBar progress;
    private EditText searchText;
    private ImageButton searchBtn;
    private RecyclerView resultList;
    private TextView instructionText, info;
    private TextView ABC,BBC,HINDU,CBS,CNBC,GOOGLE,CNN,TOI,ESPN;
    private static final String ApiKey = "c2130699553c41a9b1089d35ba2faa0e";
    private ArrayList<String> sourceList;
    private String keywords = "";
    public static String date = "2018-11-01";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_news);

        Log.d(TAG, "onCreate: In the OnCreate Method");

        setupBottomNavigationView(); //setting up bottom navigation view
        init(); //initialising all the private data members at one place

        searchBtn.setOnClickListener(view -> {
            keywords = searchText.getText().toString().trim();
            if (keywords.isEmpty())
                inputValidation(searchText,"Type a keyword to filter your news");
            else
                gettingNewsFromKeywords();
        }); //GET request on the API for getting all the latest news searched according to the keyword.


        searchText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) &&
                        (i == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    keywords = searchText.getText().toString().trim();
                    if (keywords.isEmpty())
                        inputValidation(searchText,"Type a keyword to filter your news");
                    else
                        gettingNewsFromKeywords();
                    Toast.makeText(FilterNewsActivity.this, searchText.getText(), Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            }
        });
        sourceFilterClicks();


//        filter.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                filterByNewsSource();
//            }
//        });

    }

    private void sourceFilterClicks() {
        ABC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sourceSetting("abc-news",ABC);
            }
        });

        BBC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sourceSetting("bbc-news",BBC);
            }
        });

        HINDU.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sourceSetting("the-hindu",HINDU);
            }
        });

        CBS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sourceSetting("cbs-news",CBS);
            }
        });

        CNBC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sourceSetting("cnbc",CNBC);
            }
        });

        CNN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sourceSetting("cnn",CNN);
            }
        });

        ESPN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sourceSetting("espn",ESPN);
            }
        });

        GOOGLE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sourceSetting("google-news",GOOGLE);
            }
        });

        TOI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sourceSetting("the-times-of-india",TOI);
            }
        });
    }

    private void sourceSetting(String source, TextView textView) {
        if(sourceList.contains(source)){
            sourceList.remove(source);
            textView.setBackgroundResource(R.drawable.filter_background_not_selected);
            Toast.makeText(FilterNewsActivity.this,textView.getText().toString() + " removed", Toast.LENGTH_SHORT).show();
            gettingNewsFromKeywords();
        }else{
            sourceList.add(source);
            textView.setBackgroundResource(R.drawable.filter_background_selected);
            Toast.makeText(FilterNewsActivity.this,textView.getText().toString() + " selected", Toast.LENGTH_SHORT).show();
            gettingNewsFromKeywords();
        }
    }

    private void gettingNewsFromKeywords() {

            String sources = TextUtils.join( "," , sourceList);
            Log.d(TAG, "gettingNewsFromKeywords: source list are " + sources);

            progress.setVisibility(View.VISIBLE);
            instructionText.setVisibility(View.GONE);
            Call<News> call = RetrofitClient
                    .getInstance(context)
                    .getApi()
                    .getSearchedNews("everything",sources,keywords,date,"popularity",ApiKey); //getting top-headlines from India

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
                        info.setText("These news are fetched from " + date + " and sorted by popularity and selected news source.");
                        info.setVisibility(View.VISIBLE);

                    }

                }else{
                    //TODO: when there is nothing to fetch from the API
                    progress.setVisibility(View.GONE);
                    instructionText.setVisibility(View.VISIBLE);
                    info.setVisibility(View.GONE);
                    Toast.makeText(context, "Something went wrong...please try again.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<News> call, Throwable t) {
                Log.e(TAG, "onFailure: something went wrong." + t.getMessage());
                progress.setVisibility(View.GONE);
                instructionText.setVisibility(View.VISIBLE);
                Toast.makeText(FilterNewsActivity.this,"Check your Internet Connectivity and try again.",Toast.LENGTH_LONG).show();
            }
        });
    }

    private void init() {
        ABC = findViewById(R.id.ABC);
        BBC = findViewById(R.id.BBC);
        HINDU = findViewById(R.id.hindu);
        CBS = findViewById(R.id.CBS);
        CNBC = findViewById(R.id.CNBC);
        GOOGLE = findViewById(R.id.google_news);
        CNN = findViewById(R.id.CNN);
        TOI = findViewById(R.id.TOI);
        ESPN = findViewById(R.id.ESPN);

        sourceList = new ArrayList<>();
        instructionText = (TextView)findViewById(R.id.instruction_text);
        progress = (ProgressBar)findViewById(R.id.progress);
        searchText = (EditText)findViewById(R.id.search_field);
        searchBtn = (ImageButton)findViewById(R.id.search_btn);
        resultList = (RecyclerView)findViewById(R.id.result_list);
        resultList.setLayoutManager(new LinearLayoutManager(context));
        info = findViewById(R.id.search_info);


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

    private void inputValidation(EditText et, String msg){
        et.setError(msg);
        et.requestFocus();
    }
}
