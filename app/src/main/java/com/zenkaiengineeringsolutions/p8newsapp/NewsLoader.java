package com.zenkaiengineeringsolutions.p8newsapp;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

/**
 * Async TsakLoader
 */

public class NewsLoader extends AsyncTaskLoader<List<News>>{

    // member to hold the input URL
    String input;

    public NewsLoader (Context context, String url){
        super(context);
        this.input = url;
    }

    @Override
    public List<News> loadInBackground() {
        if (input == null)
            return null;

        return QueryUtils.extractNews(QueryUtils.fetchNewsData(input));
    }


    @Override
    protected void onStartLoading(){
        forceLoad();
    }
}
