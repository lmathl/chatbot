package com.demo.chatbot;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.demo.chatbot.models.Article;
import com.demo.chatbot.models.Message;
import com.demo.chatbot.models.Weather;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Weather> weatherList;
    private ArrayList<Article> articlesList;
    private Context context;
    protected Activity activity;
    private int SELF = 100;
    private int WEATHER = 400;
    private int ARTICLE = 800;
    private ArrayList<Message> messageArrayList;
    String[] weekdays = {
            "SAT", "SUN", "MON", "TUE", "WED", "THU", "FRI",
    };
    private OnItemClickListener onItemClickListener;
    private int lastPosition = -1;

    public MessageAdapter(Activity activity,
                          Context context,
                          ArrayList<Message> messageArrayList) {
        this.activity = activity;
        this.context = context;
        this.messageArrayList = messageArrayList;
    }
    public void setWeatherList (ArrayList<Weather> weatherList) {
        this.weatherList = weatherList;
    }
    public void setArticlesList (ArrayList<Article> articlesList) {
        this.articlesList = articlesList;
    }
    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // self message
        if (viewType == SELF) {
            return new ViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_chat_self, parent, false));
        }
        // Bot messages: weather and articles have special view holders
        else if (viewType == WEATHER) {
            return new WeatherViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_chat_bot, parent, false
            ));
        }
        else if (viewType == ARTICLE) {
            return new ArticlesViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_chat_bot, parent, false
            ), onItemClickListener);
        } else {

            return new ViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_chat_bot, parent, false));
        }
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messageArrayList.get(position);
        if (message.getIsSelf() == true) {
            return SELF;
        } else if (message.getType() == "weather") {
            return WEATHER;
        }
        else if (message.getType() == "news") {
            return ARTICLE;
        }

        return position;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        Message message = messageArrayList.get(position);

        switch (message.getType()) {
            case "text":
                ((ViewHolder) holder).message.setText(message.getMessage());
                if (!message.getIsSelf()){
                    setAnimation(((ViewHolder) holder).botLayout, position, true);
                } else {
                    setAnimation(((ViewHolder) holder).selfLayout, position, false);
                }
                break;
            case "image":
                ((ViewHolder) holder).botLayout.setVisibility(View.GONE);
                ((ViewHolder) holder).imageResponse.setVisibility(View.VISIBLE);
                setAnimation(((ViewHolder) holder).imageResponse, position,true);

                ImageView iv = ((ViewHolder) holder).imageResponse;
                if (message.getMessage() == "hiragana") {
                    Glide.with(iv.getContext()).load(R.drawable.hiragana).apply(new RequestOptions().override(700, 700)).into(iv);
                } else if (message.getMessage() == "katakana") {
                    Glide.with(iv.getContext()).load(R.drawable.katakana).apply(new RequestOptions().override(700, 700)).into(iv);
                }
                break;
            case "weather":
                ((WeatherViewHolder) holder).message.setText(message.getMessage());

                for (int i = 0; i < weatherList.size(); i++) {
                    Weather weather = weatherList.get(i);
                    Glide
                            .with(context)
                            .load(context.getResources().getIdentifier(
                                    "icon_" + weather.getWeatherDetails().get(0).getIcon(),
                                    "drawable", context.getPackageName()))
                            .into(((WeatherViewHolder) holder).mWeatherIcon[i]);

                    Calendar calendar = new GregorianCalendar();
                    calendar.setTime(new java.util.Date((long) weather.getDate() * 1000));
                    ((WeatherViewHolder) holder).mDate[i].setText(weekdays[calendar.get(Calendar.DAY_OF_WEEK) % 7]);
                    ((WeatherViewHolder) holder).mMaxTemp[i].setText((int) weather.getTemp().getMax() + "°C");
                    ((WeatherViewHolder) holder).mMinTemp[i].setText((int) weather.getTemp().getMin() + "°C");
                    ((WeatherViewHolder) holder).mWeatherCondition[i].setText(String.valueOf(weather.getWeatherDetails().get(0).getShortDescription()));
                }
                break;
            case "news":
                ((ArticlesViewHolder) holder).message.setText(message.getMessage());

                RequestOptions requestOptions = new RequestOptions();
                requestOptions.placeholder(Utils.getRandomDrawbleColor());
                requestOptions.error(Utils.getRandomDrawbleColor());
                requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
                requestOptions.centerCrop();

                for (int i = 0; i < 5; i++) {
                    Article model = articlesList.get(i);

                    int finalI = i;
                    Glide.with(context)
                            .load(model.getUrlToImage())
                            .apply(requestOptions)
                            .listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    ((ArticlesViewHolder) holder).mProgressBar[finalI].setVisibility(View.GONE);
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    ((ArticlesViewHolder) holder).mProgressBar[finalI].setVisibility(View.GONE);
                                    return false;
                                }
                            })
                            .transition(DrawableTransitionOptions.withCrossFade())
                            .into(((ArticlesViewHolder) holder).mImageView[finalI]);

                    ((ArticlesViewHolder) holder).mTitle[i].setText(model.getTitle());
                    ((ArticlesViewHolder) holder).mDesc[i].setText(model.getDescription());
                    ((ArticlesViewHolder) holder).mSource[i].setText(model.getSource().getName());
                    ((ArticlesViewHolder) holder).mTime[i].setText(" \u2022 " + Utils.DateToTimeFormat(model.getPublishedAt()));
                    ((ArticlesViewHolder) holder).mPublishedAt[i].setText(Utils.DateFormat(model.getPublishedAt()));
                }
                break;
        }
    }

    private void setAnimation(View viewToAnimate, int position, boolean slideInRight) {
        // display animation for new items
        if (position > lastPosition) {
            if (slideInRight) {
                Animation animation = AnimationUtils.loadAnimation(context, R.anim.anim_slide_in_right);
                viewToAnimate.startAnimation(animation);
                lastPosition = position;
            } else {
                Animation animation = AnimationUtils.loadAnimation(context, R.anim.anim_slide_in_left);
                viewToAnimate.startAnimation(animation);
                lastPosition = position;
            }
        }
    }

    @Override
    public int getItemCount() {
        return messageArrayList.size();
    }

    // Message
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView message;
        ImageView imageResponse;
        LinearLayout botLayout, selfLayout;
        public ViewHolder(View view) {
            super(view);
            message = itemView.findViewById(R.id.message);
            botLayout = itemView.findViewById(R.id.chat_bot_linear_layout);
            selfLayout = itemView.findViewById(R.id.chat_self_linear_layout);
            imageResponse = itemView.findViewById(R.id.image_attached);
        }
    }

    // Weather
    public class WeatherViewHolder extends RecyclerView.ViewHolder{
        private LinearLayout weatherLayout;
        private TextView[] mDate = new TextView[weatherList.size()];
        private TextView[] mMaxTemp = new TextView[weatherList.size()];
        private TextView[] mMinTemp = new TextView[weatherList.size()];
        private TextView[] mWeatherCondition = new TextView[weatherList.size()];
        private ImageView[] mWeatherIcon = new ImageView[weatherList.size()];
        TextView message;

        public WeatherViewHolder(View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.message);
            weatherLayout = itemView.findViewById(R.id.chat_bot_linear_layout);

            for (int i = 0; i < weatherList.size(); i++) {
                View view = View.inflate(context, R.layout.item_weather, null);
                mWeatherIcon[i] = view.findViewById(R.id.imageViewWeatherIcon);
                mDate[i] = view.findViewById(R.id.textViewDate);
                mMaxTemp[i] = view.findViewById(R.id.textViewMaxTemp);
                mMinTemp[i] = view.findViewById(R.id.textViewMinTemp);
                mWeatherCondition[i] = view.findViewById(R.id.textViewWeatherCondition);

                weatherLayout.addView(view);
            }
        }
    }

    // Article
    private class ArticlesViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout articlesLayout;
        TextView message;
        OnItemClickListener onItemClickListener;

        private TextView[] mTitle = new TextView[articlesList.size()];
        private TextView[] mDesc = new TextView[articlesList.size()];
        private TextView[] mPublishedAt = new TextView[articlesList.size()];
        private TextView[] mSource = new TextView[articlesList.size()];
        private TextView[] mTime = new TextView[articlesList.size()];
        private ImageView[] mImageView = new ImageView[articlesList.size()];
        private ProgressBar[] mProgressBar = new ProgressBar[articlesList.size()];

        ArticlesViewHolder(View itemView, OnItemClickListener onItemClickListener) {
            super(itemView);
            message = itemView.findViewById(R.id.message);
            articlesLayout = itemView.findViewById(R.id.chat_bot_linear_layout);

            for (int i = 0; i < articlesList.size(); i++) {
                View view = View.inflate(context, R.layout.item_news, null);
                mTitle[i] = view.findViewById(R.id.title);
                mDesc[i] = view.findViewById(R.id.desc);
                mPublishedAt[i] = view.findViewById(R.id.publishedAt);
                mSource[i] = view.findViewById(R.id.source);
                mTime[i] = view.findViewById(R.id.time);
                mImageView[i] = view.findViewById(R.id.img);
                mProgressBar[i] = view.findViewById(R.id.progress_load_photo);

                articlesLayout.addView(view);
                int finalI = i;
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onItemClickListener.onItemClick(view, finalI);
                    }
                });
            }

        }
    }
}