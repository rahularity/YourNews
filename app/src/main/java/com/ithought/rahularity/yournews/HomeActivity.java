package com.ithought.rahularity.yournews;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ithought.rahularity.yournews.Models.Article;
import com.ithought.rahularity.yournews.Models.News;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";
    private Context context = this;
    private ProgressBar progress;
    private EditText searchText;
    private ImageButton searchBtn;
    private RecyclerView resultList;
    private static final String ApiKey = "c2130699553c41a9b1089d35ba2faa0e";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Log.d(TAG, "onCreate: In the OnCreate Method");

        init();

        fetchingNews();

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gettingNewsFromKeywords();
            }
        });


    }

    private void gettingNewsFromKeywords() {

        String keywords = searchText.getText().toString().trim();

        if (keywords.isEmpty())
            inputValidation(searchText,"Type a keyword to filter your news");
        else{
            Call<News> call = RetrofitClient
                    .getInstance()
                    .getApi()
                    .getSearchedNews("everything",keywords,"2018-11-01","popularity",ApiKey); //getting top-headlines from India

            enqueueCall(call);

        }


    }

    private void fetchingNews() {
        Call<News> call = RetrofitClient
                .getInstance()
                .getApi()
                .getNews("top-headlines","in",ApiKey); //getting top-headlines from India

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

                    }

                }else{
                    //TODO: when there is nothing to fetch from the API
                    //progress.setVisibility(View.GONE);
                    Toast.makeText(context, "There are no news in the database", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<News> call, Throwable t) {
                Log.e(TAG, "onFailure: something went wrong." + t.getMessage());
                //progress.setVisibility(View.GONE);
                Toast.makeText(HomeActivity.this,"Something went wrong...please try again.",Toast.LENGTH_LONG).show();
            }
        });
    }

    private void init() {
        searchText = (EditText)findViewById(R.id.search_field);
        searchBtn = (ImageButton)findViewById(R.id.search_btn);
        resultList = (RecyclerView)findViewById(R.id.result_list);
        resultList.setLayoutManager(new LinearLayoutManager(context));
    }

    private void inputValidation(EditText et, String msg){
        et.setError(msg);
        et.requestFocus();
    }
}
