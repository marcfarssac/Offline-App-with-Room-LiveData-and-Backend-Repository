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

import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.mfarssac.moviedb.repository.room.MovieEntry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;

/**
 * Parser for MovieDB JSON data.
 */
final class MovieDbJsonParser {

    // Movie information. Each movie info is an element of the "list" array
    private static final String MDB_PAGE_RESULTS = "results";

    private static final String MDB_VIDEO = "video";
    private static final String MDB_VOTE_AVERAGE = "vote_average";
    private static final String MDB_TITLE = "title";
    private static final String MDB_POPULARITY = "popularity";
    private static final String MDB_POSTER_PATH = "poster_path";
    private static final String MDB_ORIGINAL_LANGUAGE = "original_language";
    private static final String MDB_ORIGINAL_TITLE = "original_title";
    private static final String MDB_GENRE_IDS = "genre_ids";
    private static final String MDB_BACKDROP_PATH = "backdrop_path";
    private static final String MDB_ADULT = "adult";
    private static final String MDB_OVERVIEW = "overview";
    private static final String MDB_RELEASE_DATE = "release_date";

    // Movie DB API Status Codes
    private static final String MDB_STATUS_CODE = "status_code";

    private static boolean hasHttpError(JSONObject movieDbJson) throws JSONException {

        if (movieDbJson.has(MDB_STATUS_CODE)) {
            int statusCode = movieDbJson.getInt(MDB_STATUS_CODE);

            switch (statusCode) {
                case HttpURLConnection.HTTP_OK:
                    return false;
                default:
                    // Server probably down
                    return true;
            }
        }
        return false;
    }

    private static MovieEntry[] fromJson(final JSONObject moviesPageJson) throws JSONException {

        JSONArray jsonMoviesArray = moviesPageJson.getJSONArray(MDB_PAGE_RESULTS);

        MovieEntry[] movieEntries = new MovieEntry[jsonMoviesArray.length()];
        Gson gson = new Gson();

        for (int i = 0; i < jsonMoviesArray.length(); i++) {
            // Get the JSON object representing one movie of the page
            JSONObject movieEntry = jsonMoviesArray.getJSONObject(i);

            MovieEntry movie = gson.fromJson(movieEntry.toString(), MovieEntry.class);
            movieEntries[i] = movie;

        }
        return movieEntries;
    }


    /**
     * This method parses JSON from a web response and returns an array
     * with the 20 more popular movies in the requested movies page.
     * The API return pages with popular movies with 20 movies in each
     *
     * @param movieDBJsonStr JSON response from server
     * @return One page of information with 20 movies
     * @throws JSONException If JSON data cannot be properly parsed
     */
    @Nullable
    MovieDbResponse parse(final String movieDBJsonStr) throws JSONException {
        JSONObject moviesJson = new JSONObject(movieDBJsonStr);

        // Is there an error?
        if (hasHttpError(moviesJson)) {
            return null;
        }

        MovieEntry[] movieEntries = fromJson(moviesJson);

        return new MovieDbResponse(movieEntries);
    }
}