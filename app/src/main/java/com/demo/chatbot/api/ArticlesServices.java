package com.demo.chatbot.api;

import com.demo.chatbot.models.ArticlesList;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ArticlesServices {

    @GET("top-headlines")
    Observable<ArticlesList> getArticles(

            @Query("country") String country ,
            @Query("apiKey") String apiKey

    );

    @GET("everything")
    Observable<ArticlesList> getArticlesSearch(

            @Query("q") String keyword,
            @Query("language") String language,
            @Query("sortBy") String sortBy,
            @Query("apiKey") String apiKey

    );

}
