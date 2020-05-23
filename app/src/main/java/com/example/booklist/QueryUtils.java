package com.example.booklist;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class QueryUtils {
    private static final String TAG = QueryUtils.class.toString();

    /**
     * Create private constructor because this class should never be created.
     */
    private QueryUtils(){
    }

    /**
     *
     * @param requestUrl is the JSON url that contain book information.
     * @return an ArrayList that have a list of book's name, author and link of the book.
     */
    public static ArrayList<Book> extractBook(String requestUrl) {
        String jsonResponse = null;
        URL url = createUrl(requestUrl); //Using the JSON link to create a URL
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e){
            Log.e(TAG, "Problem making the http request: " + e.toString());
        }


        ArrayList<Book> bookList = new ArrayList<>();

        //Pause the JASON RESPONSE
        try {
            JSONObject baseJsonResponse = new JSONObject(jsonResponse);
            JSONArray bookArray = baseJsonResponse.getJSONArray("items");

            for (int i = 0; i < bookArray.length(); i++){
                JSONObject currentBook = bookArray.getJSONObject(i);
                JSONObject volumeInfo = currentBook.getJSONObject("volumeInfo"); //Find volumeInfo in the JSON array that contain book name, author name and url of the book.

                String bookName = volumeInfo.getString("title"); //Extract book name
                String link = volumeInfo.getString("infoLink");//Extract url of the book


                Book book = new Book(bookName, link);//Create book object and put the book name, author name and url in it;
                bookList.add(book); //Put the book to the list.


            }
        } catch (JSONException e){
            Log.e(TAG, "Problem parsing the book JSON result: " + e.toString());
        }
        return bookList;
    }

    /**
     *  Hepler method to make the http request.
     */
    private static String makeHttpRequest(URL url) throws IOException{
        String jsonResonse = "";
        if (url == null){
            return jsonResonse; //If the URL is null, return early.
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000/* milliseconds */);
            urlConnection.setConnectTimeout(15000/* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if (urlConnection.getResponseCode() == 200){ //Check if the request is successful (response code = 200)
                inputStream = urlConnection.getInputStream();
                jsonResonse = readFromStream(inputStream);
            }
        } catch (IOException e){
            Log.e(TAG, "Problem from retrieving the book JSON result: " + e.toString());
        } finally {
            if (urlConnection != null){
                urlConnection.disconnect();
            }
            if (inputStream != null){
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies that an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResonse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException{
        StringBuilder output = new StringBuilder();
        if (inputStream != null){
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine(); //Using the readLine method could throw an IOException, that's why the method signature throws the exception.
            while (line != null){
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Helper method to create the URL after checking the url is not null.
     */
    private static URL createUrl(String stringUrl){
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e){
            Log.e(TAG, "Problem building the URL: " + e.toString());
        }
        return url;
    }
}
