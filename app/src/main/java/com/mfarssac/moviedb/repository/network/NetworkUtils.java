/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * The MovieDB App was created by M a r c  F a r s s a c
 *
 */
package com.mfarssac.moviedb.repository.network;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * These utilities will be used to communicate with the movie servers.
 */
final class NetworkUtils {

    private static final String API_KEY = "e465e0cf912d2120334c4948be6cd49b";

    private static final String TAG = NetworkUtils.class.getSimpleName();

    private static final String MOVIEDB_URL =
            "https://api.themoviedb.org/3/movie/popular" + "?api_key=" + API_KEY + "&language=en-US";

    private static final String MOVIE_DB_BASE_URL = MOVIEDB_URL;

    /* The format we want our API to return */
    private static final String format = "json";

    /* The page number parameter allows us to provide a popular movies page number
    /* string to the API */
    private static final String PAGE_NUMBER = "page";

    /**
     * Retrieves the proper URL to query for the movie data.
     *
     * @return URL to query movie service
     */
    static URL getUrl(String pageNumber) {
        return buildUrlWithPageNumber(pageNumber);
    }

    /**
     * Builds the URL used to talk to the movieDB server using a popular results page number.
     * Since we want to have a list of 50 movies displayed on the recyclerview and we get only
     * 20 items for each page we call, we will need to call the backend for page 1,2 and 3
     *
     * @param pageNumber The popular getMovie page number (with 20 movies)
     *                   that will be queried for.
     * @return The URL to use to query the movie server.
     */
    private static URL buildUrlWithPageNumber(String pageNumber) {
        Uri popularMoviesQueryUri = Uri.parse(MOVIE_DB_BASE_URL).buildUpon()
                .appendQueryParameter(PAGE_NUMBER, pageNumber)
                .build();

        try {
            URL popularMoviesQueryUrl = new URL(popularMoviesQueryUri.toString());
            Log.v(TAG, "URL: " + popularMoviesQueryUrl);
            return popularMoviesQueryUrl;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response, null if no response
     * @throws IOException Related to network and stream reading
     */
    static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            String response = null;
            if (hasInput) {
                response = scanner.next();
            }
            scanner.close();
            return response;
        } finally {
            urlConnection.disconnect();
        }
    }
}