package com.example.booklist;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Book>> {
    private static final int BOOK_LOADER_ID = 1;

    private EditText edit_inputBookName;
    private Button btn_search;
    private ListView list_book;
    private BookListAdaptor adapter;
    private TextView emptyText;
    private ProgressBar loading;
    private String jsonLink = "https://www.googleapis.com/books/v1/volumes?q=android&maxResults=10";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViews();

        list_book.setEmptyView(emptyText);
        adapter = new BookListAdaptor(this, new ArrayList<Book>());
        list_book.setAdapter(adapter);

        list_book.setOnItemClickListener(listItemListener);
        btn_search.setOnClickListener(buttonListener);

        if (hasNetwork()){
            LoaderManager loaderManager = LoaderManager.getInstance(this);
            loaderManager.initLoader(BOOK_LOADER_ID, null, this);
        }
        else {
            loading.setVisibility(View.GONE);
            emptyText.setText(R.string.no_network);
        }
    }

    /**
     * Find the UI views from the xml file.
     */
    private void findViews(){
        edit_inputBookName = (EditText) findViewById(R.id.edit_inputBookName);
        btn_search = (Button) findViewById(R.id.btn_search);
        list_book = (ListView) findViewById(R.id.list_book);
        emptyText = (TextView) findViewById(R.id.emptyText);
        loading = (ProgressBar) findViewById(R.id.loading);
    }

    /**
     *  Go to website when user click on the book name.
     */
    private ListView.OnItemClickListener listItemListener = new ListView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Book book = adapter.getItem(position); // Find the current earthquake that was clicked on
            Uri uri = Uri.parse(book.getUrl()); // Convert the String URL into a URI object (to pass into the Intent constructor)
            Intent intent = new Intent(Intent.ACTION_VIEW, uri); // Create a new intent to view the earthquake URI
            startActivity(intent); // Send the intent to launch a new activity
        }
    };

    /**
     *  Click the button to show the book list based on user's input.
     */
    private Button.OnClickListener buttonListener = new Button.OnClickListener(){
        @Override
        public void onClick(View v) {
            String search = edit_inputBookName.getText().toString();
            jsonLink = "https://www.googleapis.com/books/v1/volumes?q=" + search + "&maxResults=10"; //Update the url with user's input

            LoaderManager.getInstance(MainActivity.this).restartLoader(BOOK_LOADER_ID, null, MainActivity.this); //Restart the loader

            edit_inputBookName.setText("");
        }
    };

    @NonNull
    @Override
    public Loader<List<Book>> onCreateLoader(int id, @Nullable Bundle args) {
        return new BookLoader(MainActivity.this, jsonLink);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Book>> loader, List<Book> data) {
        loading.setVisibility(View.GONE);
        if (hasNetwork()){
            emptyText.setText(R.string.no_record); // Set empty state text to display "No record found."
            adapter.clear(); // Clear the adapter of previous data
            if (data != null || !data.isEmpty()){
                adapter.addAll(data);
            }
        }
        else {
            emptyText.setText(R.string.no_network);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Book>> loader) {
        adapter.clear(); // Loader reset, so we can clear out our existing data.
    }

    /**
     * Check whether the network is connected.
     */
    private boolean hasNetwork(){
        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){ //Check the android version
            NetworkCapabilities capabilities = cm.getNetworkCapabilities(cm.getActiveNetwork());
            if (capabilities != null){ // There is a network connection
                return true;
            }
            else {
                return false;
            }
        }
        else {
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()){ // There is a network connection
                return true;
            }
            else {
                return false;
            }
        }
    }
}
