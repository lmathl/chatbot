package com.demo.chatbot;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class Utils {
    public static String[] quotes = {
            "\"Nothing diminishes anxiety faster than action.\" - Walter Anderson",
            "\"Do not discourage yourself with what you haven't done, encourage yourself with what you will do.\" - Neale Donald Walsch",
            "\"Life is simple. Are you happy? Yes? Keep going. No? Change something.\" - Unknown",
            "\"All things are difficult before they are easy.\" - Thomas Fuller",
            "\"Growth is painful. Change is painful. But nothing is as painful as staying stuck somewhere you don't belong.\" - Mandy Hale",
            "\"A year from now you will wish you had started today.\" - Karen Lamb",
            "\"Life has no limitations, except the ones you make.\" - Les Brown",
            "\"Keep a positive mind. Remember, a failed attempt doesn't make you a failure—giving up does.\" - Lorii Myers",
            "\"There is never enough time to do everything, but there is always enough time to do the most important thing.\" - Brian Tracy",
            "\"No matter how hard the past, you can always begin again.\" - Jack Kornfield",
            "\"Don't use all-or-nothing thinking. Take each day as its own day, and don't worry about it if you mess up one day. The most important thing you can do is just get back up on the horse.\" - Henry Cloud",
            "\"Don't let the mistakes and disappointments of the past control and direct your future.\" - Zig Ziglar",
            "\"Luck is a dividend of sweat. The more you sweat, the luckier you get\" - Ray Kroc",
            "\"Deciding what not to do is as important as deciding what to do.\" - Steve Jobs",
            "\"All great achievements require time.\" - Maya Angelou",
            "\"Take pride in your pain; you are stronger than those who have none.\" - Lois Lowry",
            "\"The best way to not feel hopeless is to get up and do something.Don’t wait for good things to happen to you. If you go out and make some good things happen, you will fill the world with hope, you will fill yourself with hope.\" - Barack Obama",
            "\"Your life does not get better by chance.It gets better by change.\" - Jim Rohn",
            "\"Powerful avalanches begin with small shifts.\" - Pamela McFarland Walsh",
            "\"You are confined only by the walls you build yourself.\" - Andrew Murphy",
            "\"We are what we repeatedly do.Excellence then, is not an act, but a habit.\" - Will Durant",
            "\"A day without laughter is a day wasted.\" - Charlie Chaplin",
            "\"Decide whether or not the goal is worth the risks involved.If it is, stop worrying.\" - Amelia Earhart",
            "\"One day you will wake up and there won't be any more time to do the things you've always wanted.Do it now.\" - Paulo Coelho",
            "\"Today's accomplishments were yesterday's impossibilities.\" - Robert Schuller",
            "\"Do not let what you cannot do interfere with what you can do.\" - John Wooden",
            "\"Whatever you hold in your mind on a consistent basis is exactly what you will experience in your life.\" - Tony Robbins",
            "\"Most people get ahead during the time that others waste.\" - Henry Ford",
            "\"Don't find fault. Find a remedy.\" - Henry Ford",
            "\"There are always limits, and there are always opportunities. The ones we rehearse and focus on are the ones that shape our attitude and our actions.\" - Seth Godin",
            "\"Yesterday is history; tomorrow is a mystery. Today is a gift, which is why we call it the present.\" - Bil Keane",
            "\"You don't have to see the whole staircase. Just take the first step.\" - Martin Luther King",
            "\"If you lose, don't lose the lesson.\" - Dalai Lama"
    };

    public static ColorDrawable[] vibrantLightColorList =
            {
                    new ColorDrawable(Color.parseColor("#ffeead")),
                    new ColorDrawable(Color.parseColor("#93cfb3")),
                    new ColorDrawable(Color.parseColor("#fd7a7a")),
                    new ColorDrawable(Color.parseColor("#faca5f")),
                    new ColorDrawable(Color.parseColor("#1ba798")),
                    new ColorDrawable(Color.parseColor("#6aa9ae")),
                    new ColorDrawable(Color.parseColor("#ffbf27")),
                    new ColorDrawable(Color.parseColor("#d93947"))
            };

    public static ColorDrawable getRandomDrawbleColor() {
        int idx = new Random().nextInt(vibrantLightColorList.length);
        return vibrantLightColorList[idx];
    }

    public static String DateToTimeFormat(String oldstringDate){
        PrettyTime p = new PrettyTime(new Locale(getCountry()));
        String isTime = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'",
                    Locale.ENGLISH);
            Date date = sdf.parse(oldstringDate);
            isTime = p.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return isTime;
    }

    public static String DateFormat(String oldstringDate){
        String newDate;
        SimpleDateFormat dateFormat = new SimpleDateFormat("E, d MMM yyyy", new Locale(getCountry()));
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").parse(oldstringDate);
            newDate = dateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            newDate = oldstringDate;
        }

        return newDate;
    }

    public static String getCountry(){
        Locale locale = Locale.getDefault();
        String country = locale.getCountry();
        return country.toLowerCase();
    }
}
