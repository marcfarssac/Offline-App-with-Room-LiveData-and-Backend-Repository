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

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;
import com.mfarssac.moviedb.AppExecutors;
import com.mfarssac.moviedb.repository.room.MovieEntry;

import java.net.URL;

/**
 * Provides an API for doing all operations with the server data
 */
public class MoviesNetworkDataSource {
    // The number of movies we want our API to keep, set to 50 as per specs
    public static int MAX_POPULAR_MOVIES_NUMBER = 50;

    public static String MDB_FETCH_PAGE1 = "1";
    public static String MDB_FETCH_PAGE2 = "2";
    public static String MDB_FETCH_PAGE3 = "3";

    private static final String LOG_TAG = MoviesNetworkDataSource.class.getSimpleName();

    // Interval at which to sync with the backend movies database. Use TimeUnit for convenience, rather than
    // writing out a bunch of multiplication ourselves and risk making a silly mistake.
    private static final int SYNC_INTERVAL_HOURS = 3;
//    private static final int SYNC_INTERVAL_SECONDS = (int) TimeUnit.HOURS.toSeconds(SYNC_INTERVAL_HOURS);
    private static final int SYNC_INTERVAL_SECONDS = 180;
    private static final int SYNC_FLEXTIME_SECONDS = SYNC_INTERVAL_SECONDS / 3;
    private static final String MOVIES_SYNC_TAG = "movies-sync";

    // For Singleton instantiation
    private static final Object LOCK = new Object();
    private static MoviesNetworkDataSource sInstance;
    private final Context mContext;

    // LiveData storing the latest downloaded movies
    private final MutableLiveData<MovieEntry[]> mDownloadedPopularMovies;
    private final AppExecutors mExecutors;

    private MoviesNetworkDataSource(Context context, AppExecutors executors) {
        mContext = context;
        mExecutors = executors;
        mDownloadedPopularMovies = new MutableLiveData<MovieEntry[]>();
    }

    /**
     * Get the singleton for this class
     */
    public static MoviesNetworkDataSource getInstance(Context context, AppExecutors executors) {
        Log.d(LOG_TAG, "Getting the network data source");
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new MoviesNetworkDataSource(context.getApplicationContext(), executors);
                Log.d(LOG_TAG, "Made new network data source");
            }
        }
        return sInstance;
    }

    public LiveData<MovieEntry[]> getCurrentMovies() {
        return mDownloadedPopularMovies;
    }

    /**
     * Starts an intent service to fetch the movie.
     */
    public void startFetchMoviesService() {
        Intent intentToFetch = new Intent(mContext, MoviesSyncIntentService.class);
        mContext.startService(intentToFetch);
        Log.d(LOG_TAG, "Service created");
    }

    /**
     * Schedules a repeating job service which fetches the movie.
     */
    public void scheduleRecurringFetchMoviesSync() {
        Driver driver = new GooglePlayDriver(mContext);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);

        // Create the Job to periodically sync the Movie DB
        Job syncMovieDbJob = dispatcher.newJobBuilder()
                /* The Service that will be used to sync the Movies Database's data */
                .setService(MoviesDBJobService.class)
                /* Set the UNIQUE tag used to identify this Job */
                .setTag(MOVIES_SYNC_TAG)
                /*
                 * Network constraints on which this Job should run. We choose to run on any
                 * network, but you can also choose to run only on un-metered networks or when the
                 * device is charging. It might be a good idea to include a preference for this,
                 * as some users may not want to download any data on their mobile plan. ($$$)
                 */
                .setConstraints(Constraint.ON_ANY_NETWORK)
                /*
                 * setLifetime sets how long this job should persist. The options are to keep the
                 * Job "forever" or to have it die the next time the device boots up.
                 */
                .setLifetime(Lifetime.FOREVER)
                /*
                 * We want the Movie DB's movie data to stay up to date, so we tell this Job to recur.
                 */
                .setRecurring(true)
                /*
                 * We want the movie data to be synced every 3 to 4 hours. The first argument for
                 * Trigger's static executionWindow method is the start of the time frame when the
                 * sync should be performed. The second argument is the latest point in time at
                 * which the data should be synced. Please note that this end time is not
                 * guaranteed, but is more of a guideline for FirebaseJobDispatcher to go off of.
                 */
                .setTrigger(Trigger.executionWindow(
                        SYNC_INTERVAL_SECONDS,
                        SYNC_INTERVAL_SECONDS + SYNC_FLEXTIME_SECONDS))
                /*
                 * If a Job with the tag with provided already exists, this new job will replace
                 * the old one.
                 */
                .setReplaceCurrent(true)
                /* Once the Job is ready, call the builder's build method to return the Job */
                .build();

        // Schedule the Job with the dispatcher
        dispatcher.schedule(syncMovieDbJob);
        Log.d(LOG_TAG, "Job scheduled");
    }

    /**
     * Gets the newest movie
     */
    void fetchMovies() {
        Log.d(LOG_TAG, "Fetch movies started");
        mExecutors.networkIO().execute(() -> {
            try {

                // The getUrl method will return the URL that we need to get the a page of popular
                // movies from the database.

                URL movieRequestUrl = NetworkUtils.getUrl(MDB_FETCH_PAGE1);

                // Use the URL to retrieve the JSON
                String jsonMovieResponse = NetworkUtils.getResponseFromHttpUrl(movieRequestUrl);

                // Parse the JSON into a list of movies
                MovieDbResponse response = new MovieDbJsonParser().parse(jsonMovieResponse);
                Log.d(LOG_TAG, "JSON Parsing finished");


                // As long as there are movies, update the LiveData storing the most recent
                // movies. This will trigger observers of that LiveData, such as the
                // MoviesRepository.
                if (response != null && response.getMovies().length != 0) {
                    Log.d(LOG_TAG, "JSON not null and has " + response.getMovies().length
                            + " values");

                    // Off of the main thread to update LiveData, we use postValue.
                    // It posts the update to the main thread.
                    mDownloadedPopularMovies.postValue(response.getMovies());

                    // If the code reaches this point, we have successfully performed our sync
                }
            } catch (Exception e) {
                // Server probably invalid
                e.printStackTrace();
            }

        });

        mExecutors.networkIO().execute(() -> {
            try {

                // The getUrl method will return the URL that we need to get the a page of popular
                // movies from the database.

                URL movieRequestUrl = NetworkUtils.getUrl(MDB_FETCH_PAGE2);

                // Use the URL to retrieve the JSON
                String jsonMovieResponse = NetworkUtils.getResponseFromHttpUrl(movieRequestUrl);

                // Parse the JSON into a list of movies
                MovieDbResponse response = new MovieDbJsonParser().parse(jsonMovieResponse);
                Log.d(LOG_TAG, "JSON Parsing finished");


                // As long as there are movies, update the LiveData storing the most recent
                // movies. This will trigger observers of that LiveData, such as the
                // MoviesRepository.
                if (response != null && response.getMovies().length != 0) {
                    Log.d(LOG_TAG, "JSON not null and has " + response.getMovies().length
                            + " values");

                    // Off of the main thread to update LiveData, we use postValue.
                    // It posts the update to the main thread.
                    mDownloadedPopularMovies.postValue(response.getMovies());

                    // If the code reaches this point, we have successfully performed our sync
                }
            } catch (Exception e) {
                // Server probably invalid
                e.printStackTrace();
            }

        });

        mExecutors.networkIO().execute(() -> {
            try {

                // The getUrl method will return the URL that we need to get the page JSON for the
                // 20 movies.

                URL movieRequestUrl = NetworkUtils.getUrl(MDB_FETCH_PAGE3);

                // Use the URL to retrieve the JSON
                String jsonMovieResponse = NetworkUtils.getResponseFromHttpUrl(movieRequestUrl);

                // Parse the JSON into a list of movies
                MovieDbResponse response = new MovieDbJsonParser().parse(jsonMovieResponse);
                Log.d(LOG_TAG, "JSON Parsing finished");


                // As long as there are movies, update the LiveData storing the most recent
                // movies. This will trigger observers of that LiveData, such as the
                // MoviesRepository.
                if (response != null && response.getMovies().length != 0) {
                    Log.d(LOG_TAG, "JSON not null and has " + response.getMovies().length
                            + " values");

                    // Off of the main thread to update LiveData, we use postValue.
                    // It posts the update to the main thread.
                    mDownloadedPopularMovies.postValue(response.getMovies());

                    // If the code reaches this point, we have successfully performed our sync
                }
            } catch (Exception e) {
                // Server probably invalid
                e.printStackTrace();
            }

        });

    }

}