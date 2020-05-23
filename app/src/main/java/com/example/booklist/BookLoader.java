package com.example.booklist;

import android.content.Context;

import androidx.loader.content.AsyncTaskLoader;

import java.util.List;

public class BookLoader extends AsyncTaskLoader<List<Book>> {
    private String url;

    public BookLoader(Context context, String url) {
        super(context);
        this.url = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<Book> loadInBackground() {
        if (url == null){
            return null;
        }
        List<Book> book = QueryUtils.extractBook(url); //Perform the network request, parse the response, and extract a list of book.
        return book;
    }
}
