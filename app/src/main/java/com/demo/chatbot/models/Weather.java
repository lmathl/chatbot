package com.demo.chatbot.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Weather implements Serializable{
    @SerializedName("dt")
    private int date;
    private Temp temp;
    @SerializedName("weather")
    private List<WeatherDetails> weatherDetails;

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public Temp getTemp() {
        return temp;
    }

    public void setTemp(Temp temp) {
        this.temp = temp;
    }

    public List<WeatherDetails> getWeatherDetails() {
        return weatherDetails;
    }

    public void setWeatherDetails(List<WeatherDetails> weatherDetails) {
        this.weatherDetails = weatherDetails;
    }
}