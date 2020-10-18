package com.demo.chatbot.api;

import com.demo.chatbot.models.WeatherList;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherServices {
    @GET("onecall")
    Observable<WeatherList> getWeather (@Query("lat") String lat,
                                                  @Query("lon") String lon,
                                                  @Query("exclude") String exclude,
                                                  @Query("APPID") String key,
                                                  @Query("units") String units ,
                                                  @Query("cnt") int days);

}