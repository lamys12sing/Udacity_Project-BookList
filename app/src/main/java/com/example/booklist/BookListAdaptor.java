package com.example.booklist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class BookListAdaptor extends ArrayAdapter<Book> {

    public BookListAdaptor(@NonNull Context context, ArrayList<Book> bookName) {
        super(context, 0, bookName);
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View myView = convertView;
        if (myView == null){
            myView = LayoutInflater.from(getContext()).inflate(R.layout.book_list_layout, parent, false);
        }

        //Get the object{@link Book} located in that position in the list.
        Book book = getItem(position);

        TextView text_bookName = (TextView) myView.findViewById(R.id.text_bookName);
        text_bookName.setText(book.getBook());

        return myView;
    }
}
