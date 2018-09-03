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

package com.mfarssac.moviedb.repository;

import android.arch.lifecycle.LiveData;
import android.util.Log;

import com.mfarssac.moviedb.AppExecutors;
import com.mfarssac.moviedb.repository.network.MoviesNetworkDataSource;
import com.mfarssac.moviedb.repository.room.ListMovieEntry;
import com.mfarssac.moviedb.repository.room.MovieDao;
import com.mfarssac.moviedb.repository.room.MovieEntry;

import java.util.List;

/**
 * Handles data operations in the Movie DB App. Acts as a mediator between {@link MoviesNetworkDataSource}
 * and {@link MovieDao}
 */
public class MoviesRepository {
    private static final String LOG_TAG = MoviesRepository.class.getSimpleName();

    // For Singleton instantiation
    private static final Object LOCK = new Object();
    private static MoviesRepository sInstance;
    private final MovieDao mMovieDao;
    private final MoviesNetworkDataSource mMoviesNetworkDataSource;
    private final AppExecutors mExecutors;
    private boolean mInitialized = false;

    private MoviesRepository(MovieDao movieDao,
                             MoviesNetworkDataSource moviesNetworkDataSource,
                             AppExecutors executors) {
        mMovieDao = movieDao;
        mMoviesNetworkDataSource = moviesNetworkDataSource;
        mExecutors = executors;

        // As long as the repository exists, observe the network LiveData.
        // If that LiveData changes, update the database.
        LiveData<MovieEntry[]> networkData = mMoviesNetworkDataSource.getCurrentMovies();
        networkData.observeForever(newMoviesFromNetwork -> {
            mExecutors.diskIO().execute(() -> {
                // Deletes old historical data
                Log.d(LOG_TAG, "Database size before insert ="+ mMovieDao.getMostPopularMoviesSize());
                // Insert our new movie data into the Movie DB database
                mMovieDao.bulkInsert(newMoviesFromNetwork);
                Log.d(LOG_TAG, "New values inserted");
                // To avoid that the DB grows without limit, we keep only the 50 most popular movies
                deleteOldMovies();
                Log.d(LOG_TAG, "Database size after delete = "+ mMovieDao.getMostPopularMoviesSize());
                Log.d(LOG_TAG, "Old movies deleted");
            });
        });
    }

    public synchronized static MoviesRepository getInstance(
            MovieDao movieDao, MoviesNetworkDataSource moviesNetworkDataSource,
            AppExecutors executors) {
        Log.d(LOG_TAG, "Getting the repository");
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new MoviesRepository(movieDao, moviesNetworkDataSource,
                        executors);
                Log.d(LOG_TAG, "Made new repository");
            }
        }
        return sInstance;
    }

    /**
     * Creates periodic sync tasks and checks to see if an immediate sync is required. If an
     * immediate sync is required, this method will take care of making sure that sync occurs.
     */
    private synchronized void initializeData() {

        // Only perform initialization once per app lifetime. If initialization has already been
        // performed, we have nothing to do in this method.
        if (mInitialized) return;
        mInitialized = true;

        // This method call triggers MovieDB to create its task to synchronize movie data
        // periodically.
        mMoviesNetworkDataSource.scheduleRecurringFetchMoviesSync();

        mExecutors.diskIO().execute(() -> {
            if (isFetchNeeded()) {
                startFetchMoviesService();
            }
        });
    }

    /**
     * Database related operations
     **/

    public LiveData<List<ListMovieEntry>> getMostPopularMovies(int size) {
        initializeData();
        return mMovieDao.getMostPopularMovies(size);
    }



    /**
     * Database related operations
     **/

    public LiveData<MovieEntry> getMovieById(int id) {
        initializeData();
        return mMovieDao.getMovieById(id);
    }

    /**
     * Deletes old movie data because we don't need to keep more than MAX_POPULAR_MOVIES_NUMBER data
     */
    private void deleteOldMovies() {
        mMovieDao.deleteOldPopularMovies(MoviesNetworkDataSource.MAX_POPULAR_MOVIES_NUMBER);
    }

    /**
     * Checks if there are enough movies for the app to display all the needed data.
     *
     * @return Whether a fetch is needed
     */
    private boolean isFetchNeeded() {
        int count = mMovieDao.getMostPopularMoviesSize();
        return (count < MoviesNetworkDataSource.MAX_POPULAR_MOVIES_NUMBER);
    }

    /**
     * Network related operation
     */

    private void startFetchMoviesService() {
        mMoviesNetworkDataSource.startFetchMoviesService();
    }

}