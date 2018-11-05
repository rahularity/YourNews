package com.ithought.rahularity.yournews;

import com.ithought.rahularity.yournews.Models.News;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface Api {

    @FormUrlEncoded
    @POST("compare/")
    Call<News> compare(
            @Field("image") String image

    );

    @GET("{headline_type}")
    Call<News> getNews(
            @Path("headline_type") String headline_type,
            @Query("country") String country,
            @Query("apiKey") String apiKey

    );

    @GET("{headline_type}")
    Call<News> getSearchedNews(
            @Path("headline_type") String headline_type,
            @Query("q") String keyword,
            @Query("from") String date,
            @Query("sortBy") String filter,
            @Query("apiKey") String apiKey
    );



}
