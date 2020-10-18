package com.demo.chatbot.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class WeatherList implements Serializable{
    @SerializedName("daily")
    private ArrayList<Weather> weeklyWeather;

    @SerializedName("cod")
    private String code;
    private String message;

    public WeatherList(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public ArrayList<Weather> getWeeklyWeather() {
        return weeklyWeather;
    }

    public void setWeeklyWeather(ArrayList<Weather> weeklyWeather) {
        this.weeklyWeather = weeklyWeather;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
