package com.example.android.news;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
 * Created by Capri on 04.06.2018.
 */

public class QueryUtils {

    public static final String LOG_TAG = QueryUtils.class.getName();
    public static Bitmap bmp;

    public QueryUtils() {
    }

    /**
     * Query the Guardian dataset and return a list of {@link News} objects.
     */
    public static List<News> fetchNewsData(String urlNewsRequest) {
        // Create URL object
        URL url = createUrl(urlNewsRequest);
        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error closing input stream ", e);
        }

        // Extract relevant fields from the JSON response and create a {@link News} list
        List<News> newsList = parseJSON(jsonResponse);
        // Return the {@link News} list
        return newsList;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String urlRequest) {
        URL urlNewsRequest = null;
        try {
            urlNewsRequest = new URL(urlRequest);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL ", e);
        }
        return urlNewsRequest;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL urlNewsRequest) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (urlNewsRequest == null) {
            return jsonResponse;
        }

        HttpURLConnection httpURLConnection = null;
        InputStream inputStream = null;
        try {
            httpURLConnection = (HttpURLConnection) urlNewsRequest.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setReadTimeout(10000 /*milliseconds */);
            httpURLConnection.setConnectTimeout(15000 /* milliseconds */);
            httpURLConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (httpURLConnection.getResponseCode() == 200) {
                inputStream = httpURLConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + httpURLConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the news JSON results.", e);
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }


    /* Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server. */
    private static String readFromStream(InputStream inputStream) {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            try {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream,
                        Charset.forName("UTF-8"));
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String line = bufferedReader.readLine();
                while (line != null) {
                    output.append(line);
                    line = bufferedReader.readLine();
                }
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error while reading inputStream: ", e);
            }
        }
        return output.toString();
    }

    /**
     * Return an {@link News} object by parsing out information
     * about the news from the jsonResponse string.
     */
    private static List<News> parseJSON(String jsonResponse) {
        // If the JSON string is empty, then return early.
        if (TextUtils.isEmpty(jsonResponse)) {
            return null;
        }
        // Create an empty ArrayList that we can start adding news to
        List<News> newsList = new ArrayList<>();

        try {
            JSONObject rootObject = new JSONObject(jsonResponse);
            JSONObject responseObject = rootObject.getJSONObject("response");
            JSONArray resultsArray = responseObject.getJSONArray("results");
            // If there are results in the results array
            if (resultsArray.length() != 0) {
                // Extract out results
                for (int i = 0; i < resultsArray.length(); i++) {
                    JSONObject resultsObject = resultsArray.getJSONObject(i);
                    String sectionName = responseObject.optString("sectionName");
                    String webUrl = responseObject.optString("webUrl");
                    String publicationDate = responseObject.optString("webPublicationDate");
                    JSONObject fields = resultsObject.getJSONObject("fields");
                    String author = fields.optString("byline");
                    String headline = fields.optString("headline");
                    String articlePreview = fields.optString("trailText");
                    String imageUrl = fields.optString("thumbnail");
                    //String pictureURL = createUrl(imageUrl);
                    newsList.add(new News(sectionName, publicationDate, imageUrl, headline, articlePreview, author, webUrl));
                }
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the news JSON response ", e);
        }
        return newsList;
    }
}