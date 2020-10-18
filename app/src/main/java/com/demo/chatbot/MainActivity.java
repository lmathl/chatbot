package com.demo.chatbot;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.demo.chatbot.api.ArticlesApiClient;
import com.demo.chatbot.api.ArticlesServices;
import com.demo.chatbot.api.WeatherApiClient;
import com.demo.chatbot.api.WeatherServices;
import com.demo.chatbot.models.Article;
import com.demo.chatbot.models.ArticlesList;
import com.demo.chatbot.models.Message;
import com.demo.chatbot.models.Weather;
import com.demo.chatbot.models.WeatherList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    private EditText inputMessage;
    private ImageView btn_submit;
    private MessageAdapter mAdapter;
    private ArrayList messageArrayList;
    private RecyclerView recyclerView;
    private String botMenuMessage;
    private ImageView image;
    private WeatherServices weatherServices;
    private ArticlesServices articlesServices;
    private ArrayList<Weather> weatherList;
    private ArrayList<Article> articlesList;

    String country = "hk";
    private Map<String, String> countryMap = new HashMap<String, String>(){{
        put("us", "US");
        put("hk", "Hong Kong");
        put("jp", "Japan");
        put("cn", "China");
        put("tw", "Taiwan");
        put("kr", "Korea");
        put("ru", "Russian");
    }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        messageArrayList = new ArrayList<>();
        botMenuMessage = "Ask me anything from below:\n1. weather\n2. news \n3. hiragana/katakana\n" +
                "4. inches in cm, cm in inches, %\n5. arithmetics\n6. inspiration";
        Message defaultBotMenuMessage = new Message(botMenuMessage, "text", false);
        messageArrayList.add(defaultBotMenuMessage);

        mAdapter = new MessageAdapter(this, getApplicationContext(), messageArrayList);

        // weather
        weatherServices = WeatherApiClient.getApiClient().create(WeatherServices.class);
        weatherList = new ArrayList<Weather>();

        // articles
        articlesServices = ArticlesApiClient.getApiClient().create(ArticlesServices.class);
        articlesList = new ArrayList<Article>();

        recyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        btn_submit = findViewById(R.id.btn_send);
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        inputMessage = findViewById(R.id.edit_request);
        image = findViewById(R.id.image_attached);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_menu:
                Message defaultBotMenuMessage = new Message(botMenuMessage, "text", false);
                messageArrayList.add(defaultBotMenuMessage);
                mAdapter.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(messageArrayList.size() - 1);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void sendMessage() {
        inputMessage = findViewById(R.id.edit_request);
        String request = inputMessage.getText().toString().trim();

        boolean isEmpty = false;

        if (TextUtils.isEmpty(request)){
            isEmpty = true;
            inputMessage.setError("Enter your request");
        }

        if (!isEmpty) {
            Message userMessage = new Message(request, "text", true);
            messageArrayList.add(userMessage);

            // hide the keyboard in order to avoid getTextBeforeCursor on inactive InputConnection
            InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(inputMessage.getWindowToken(), 0);
            inputMessage.setText("");

            recyclerView.smoothScrollToPosition(messageArrayList.size() - 1);
            mAdapter.notifyDataSetChanged();
            respondToUserMessage(request);
        }
    }

    private void respondToUserMessage(String request) {
        if (request.equals("1") || request.contains("weather")){
            fetchWeatherFromAPI();
        } else if (request.contains("% of")){
            handlePercentages(request);
        } else if (request.contains("inches")){
            handleInches(request);
        } else if (request.equals("2") || request.contains("news")) {
            Pattern p = Pattern.compile("([a-z\\s]+)\\s*news",
                    Pattern.CASE_INSENSITIVE);
            Matcher m = p.matcher(request);

            if (m.matches()) {
                country = m.group(1);
                fetchArticlesFromAPI(country);
            } else {
                fetchArticlesFromAPI("hk");
                country = "hk";
            }
        } else if (request.contains("hiragana")){
            Message responseMessage = new Message("hiragana", "image", false);
            messageArrayList.add(responseMessage);
        } else if (request.contains("katakana")){
            Message responseMessage = new Message("katakana", "image", false);
            messageArrayList.add(responseMessage);
        } else if (request.replaceAll("\\s+", "").matches("[0-9]+[\\\\+-\\\\*\\\\/\\\\^]+[0-9]+([\\\\+-\\\\*\\\\/\\\\^][0-9]+)*")){
            Message responseMessage = new Message(request + " = " +
                    CalculateString.getCalculation(request.replaceAll("\\s+", "")), "text", false);
            messageArrayList.add(responseMessage);
        } else {
            int randomQuoteIndex = (int) (Math.random() * 10) % Utils.quotes.length;
            Message responseMessage = new Message(Utils.quotes[randomQuoteIndex], "text", false);
            messageArrayList.add(responseMessage);
        }
        mAdapter.notifyDataSetChanged();
    }

    public void toggleLoading(boolean show){
        ProgressBar bar = findViewById(R.id.progress_loading);
        if (show) {
            bar.setVisibility(View.VISIBLE);
        } else {
            bar.setVisibility(View.GONE);
        }
    }

    public void fetchWeatherFromAPI(){
        toggleLoading(true);

        weatherServices.getWeather("22.25", "114.1667",
                "current,minutely,hourly,alerts", WeatherApiClient.WEATHER_KEY, "metric",5)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(this::weatherSuccessResponse)
                .doOnError(this::weatherErrorResponse)
                .subscribe(new Observer<WeatherList>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                    }

                    @Override
                    public void onNext(@NonNull WeatherList weatherList) {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    private void weatherSuccessResponse(WeatherList weather) {
        if (weather != null) {
            toggleLoading(false);
            weatherList.addAll(weather.getWeeklyWeather());
            mAdapter.setWeatherList(weather.getWeeklyWeather());
            Message responseMessage = new Message("Weather forecast is:",
                    "weather", false);
            messageArrayList.add(responseMessage);
            mAdapter.notifyDataSetChanged();
            recyclerView.smoothScrollToPosition(messageArrayList.size() - 1);
        }
    }

    private void weatherErrorResponse(Throwable err) {
        toggleLoading(false);

        Toast.makeText(MainActivity.this, "Error fetching weather...", Toast.LENGTH_LONG).show();
        Message responseMessage = new Message(err + "\nPlease try again later.", "text", false);
        messageArrayList.add(responseMessage);
        mAdapter.notifyDataSetChanged();
    }

    public void fetchArticlesFromAPI(String country){
        toggleLoading(true);

        if (country.length() == 2){
            articlesServices.getArticles(country, ArticlesApiClient.NEWS_KEY)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext(this::articlesSuccessResponse)
                    .doOnError(this::articlesErrorResponse)
                    .subscribe(new Observer<ArticlesList>() {
                        @Override
                        public void onSubscribe(@NonNull Disposable d) {
                        }

                        @Override
                        public void onNext(ArticlesList articlesList) {
                        }

                        @Override
                        public void onError(Throwable e) {
                        }

                        @Override
                        public void onComplete() {
                        }
                    });
        } else {
            toggleLoading(false);

            Message responseMessage = new Message("Country query should be in two letters: hk news, jp news, cn news, tw news, etc", "text", false);
            messageArrayList.add(responseMessage);
            mAdapter.notifyDataSetChanged();
        }
    }

    private void articlesSuccessResponse(ArticlesList articles) {
        articlesList.clear();
        if (articles != null) {
            toggleLoading(false);

            for (int i = 0; i < 5; i++){
                articlesList.add(articles.getArticle().get(i));
            }
            mAdapter.setArticlesList(articlesList);

            mAdapter.setOnItemClickListener(new MessageAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    Intent intent = new Intent(MainActivity.this, NewsWebViewActivity.class);
                    Article article = articlesList.get(position);
                    intent.putExtra("url", article.getUrl());
                    intent.putExtra("title", article.getTitle());
                    intent.putExtra("source",  article.getSource().getName());
                    startActivity(intent);
                }
            });

            String countryLongForm = countryMap.get(country) != null ?  countryMap.get(country) + " " : "";
            Message responseMessage = new Message("Top headline "  + countryLongForm + "articles fetched:",
                    "news", false);
            messageArrayList.add(responseMessage);
            mAdapter.notifyDataSetChanged();
            recyclerView.smoothScrollToPosition(messageArrayList.size() - 1);
        }
    }

    private void articlesErrorResponse(Throwable err) {
        toggleLoading(false);
        Toast.makeText(MainActivity.this, "Error fetching articles...\n" + err, Toast.LENGTH_LONG).show();

        Message responseMessage = new Message(err + "\nPlease try again later.", "text", false);
        messageArrayList.add(responseMessage);
        mAdapter.notifyDataSetChanged();
    }

    private void handlePercentages(String request){
        Pattern p = Pattern.compile("[a-z\\s]*(\\d+)%\\s*of\\s*(\\d+)[a-z ]*",
                Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(request);
        double output = 0;
        double percentage = 0;
        double number = 0;

        if (m.matches()) {
            percentage = Double.parseDouble(m.group(1));
            number = Double.parseDouble(m.group(2));
            output = percentage/100 * number;
        }
        Message responseMessage = new Message(percentage + "% of " + number + " is " +
                String.format("%.2f", output), "text", false);
        messageArrayList.add(responseMessage);
    }

    private void handleInches(String request){
        Pattern p = Pattern.compile("(\\d+)\\s*inches\\s*in\\s*cm",
                Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(request);
        Pattern p2 = Pattern.compile("(\\d+)\\s*cm\\s*in\\s*inches",
                Pattern.CASE_INSENSITIVE);
        Matcher m2 = p2.matcher(request);
        double output = 0;
        double number = 0;
        double inchesOutput = 0;
        double cmInput = 0;

        if (m.matches()) {
            number = Double.parseDouble(m.group(1));
            output = number * 2.45;
            Message responseMessage = new Message(number + " inches in cm is " +
                    String.format("%.2f", output), "text", false);
            messageArrayList.add(responseMessage);
        } else if (m2.matches()) {
            cmInput = Double.parseDouble(m2.group(1));
            inchesOutput = cmInput / 2.45;
            Message responseMessage = new Message(cmInput + " cm in inches is " +
                    String.format("%.2f", inchesOutput), "text", false);
            messageArrayList.add(responseMessage);
        }
    }
}