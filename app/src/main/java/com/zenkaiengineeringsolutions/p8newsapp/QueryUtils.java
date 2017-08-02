package com.zenkaiengineeringsolutions.p8newsapp;

import android.net.Uri;
import android.text.TextUtils;
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
import java.util.List;

/**
 * Utility Class defining methods used in the main method
 */

public class QueryUtils {

    public static final String LOG_TAG = QueryUtils.class.getSimpleName();

    private QueryUtils(){}


    /**
     * Creates a full path from the search term
     */

    public static String createFullPath (String searchTerm) {
        Uri builtUri = Uri.parse("http://content.guardianapis.com/search?")
                .buildUpon()
                .appendQueryParameter("q", searchTerm)
                .appendQueryParameter("show-tags", "contributor")
                .appendQueryParameter("page-size", "50")
                .appendQueryParameter("api-key", MainActivity.API_KEY)
                .build();

        return builtUri.toString();


    }

    /**
     * Returns a JSON response based on the Requested URL
     */

    public static String fetchNewsData (String requestUrl){

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error closing input stream", e);
        }

        return jsonResponse;

    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }


    /**
     *     Function for Parsing the Data
     */
    public static List<News> extractNews (String jsonInput){

        if (TextUtils.isEmpty(jsonInput))
            return null;

        List<News> newsList = new ArrayList<>();

        try {
            JSONObject root = new JSONObject(jsonInput);                 // perhaps first check that the status is "ok"

            JSONObject response = root.getJSONObject("response");
            JSONArray results = response.getJSONArray("results");

            for (int i = 0; i<results.length(); i++) {
                JSONObject newsObject = results.getJSONObject(i);

                String title = "";
                if (newsObject.has("webTitle"))
                    title = newsObject.getString("webTitle");

                String date = "";
                if (newsObject.has("webPublicationDate")) {
                    String publishedDate = newsObject.getString("webPublicationDate");
                    date = publishedDate.substring(0, 10);
                }

                String section = "";
                if (newsObject.has("sectionName"))
                    section = newsObject.getString("sectionName");

                String url = "";
                if (newsObject.has("webUrl"))
                    url = newsObject.getString("webUrl");

                // Fetch the author only if the item has the appropriate numbers

                String author = "";
                if (newsObject.has("tags")) {
                    JSONArray newsTags = newsObject.getJSONArray("tags");
                    if (newsTags.length() > 0) {
                        JSONObject firstAuthor = newsTags.getJSONObject(0);
                        author = firstAuthor.getString("webTitle");
                    }
                    // Rather than listing multiple authors, just list first one followed by 'et al'
                    if (newsTags.length()>1){
                        author+= " et al";
                    }
                }
                newsList.add(new News(title, section, date, url, author));
            }

        } catch (JSONException e){
            Log.e(LOG_TAG, "Problem Parsing the JSON result", e);
        }


        return newsList;

    }


}
