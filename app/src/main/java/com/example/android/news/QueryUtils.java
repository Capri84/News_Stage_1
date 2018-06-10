package com.example.android.news;

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

    private static final String LOG_TAG = QueryUtils.class.getName();
    private static final String SECTION = "Section";
    private static final String WEB_URL = "https://www.theguardian.com/international";
    private static final String DATE = "Date is N/A";
    private static final String HEADLINE = "This is an interesting article without a headline";
    private static final String UNKNOWN_AUTHOR = "Unknown author";

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
            JSONObject responseObject = rootObject.optJSONObject("response");
            JSONArray resultsArray = responseObject.optJSONArray("results");
            // If there are results in the results array
            if (resultsArray.length() != 0) {
                StringBuilder articleAuthors = new StringBuilder();
                // Extract out results
                for (int i = 0; i < resultsArray.length(); i++) {
                    JSONObject resultsObject = resultsArray.optJSONObject(i);
                    if (resultsObject == null) {
                        continue;
                    }
                    String sectionName = resultsObject.optString("sectionName");
                    if (TextUtils.isEmpty(sectionName)) {
                        sectionName = SECTION;
                    }
                    String webUrl = resultsObject.optString("webUrl");
                    if (TextUtils.isEmpty(webUrl)) {
                        webUrl = WEB_URL;
                    }
                    String publicationDate = resultsObject.optString("webPublicationDate");
                    String date;
                    if (TextUtils.isEmpty(publicationDate)) {
                        date = DATE;
                    } else {
                        date = publicationDate.substring(0, 10);
                    }
                    JSONObject fields = resultsObject.optJSONObject("fields");
                    if (fields == null) {
                        continue;
                    }
                    String headline = fields.optString("headline");
                    if (TextUtils.isEmpty(headline)) {
                        headline = HEADLINE;
                    }
                    String articlePreview = fields.optString("trailText");
                    String imageUrl = fields.optString("thumbnail");
                    JSONArray tagsArray = resultsObject.optJSONArray("tags");
                    // If there are results in the tags array
                    if (tagsArray.length() != 0) {
                        for (int j = 0; j < tagsArray.length(); j++) {
                            JSONObject tagsObject = tagsArray.optJSONObject(j);
                            String author = tagsObject.optString("webTitle");
                            if (j != tagsArray.length() - 1) {
                                articleAuthors.append(author).append(", ");
                            } else {
                                articleAuthors.append(author);
                            }
                        }
                    } else {
                        articleAuthors.append(UNKNOWN_AUTHOR);
                    }
                    newsList.add(new News(sectionName, date, imageUrl, headline, articlePreview, articleAuthors.toString(), webUrl));
                    articleAuthors.setLength(0);
                }
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the news JSON response ", e);
        }
        return newsList;
    }
}