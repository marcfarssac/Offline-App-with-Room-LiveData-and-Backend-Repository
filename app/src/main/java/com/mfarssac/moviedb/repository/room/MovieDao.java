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

package com.mfarssac.moviedb.repository.room;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * {@link Dao} which provides an api for all data operations with the {@link MovieDatabase}
 */
@Dao
public interface MovieDao {

    /**
     * Selects a given number of {@link MovieEntry} entries with better ratings. The LiveData will
     * be kept in sync with the database, so that it will automatically notify observers when the
     * values in the table change.
     *
     * @param  id The primary key of the Movie Database
     * @return {@link LiveData} list of {@link MovieEntry} objects sorted from top ranked
     */
    @Query("SELECT * FROM movie WHERE id= :id")
    LiveData<MovieEntry> getMovieById(int id);

    /**
     * Selects a given number of {@link MovieEntry} entries with better ratings. The LiveData will
     * be kept in sync with the database, so that it will automatically notify observers when the
     * values in the table change.
     *
     * @param  size The number of movies to select from the database
     * @return {@link LiveData} list of {@link MovieEntry} objects sorted from top ranked
     */
    @Query("SELECT * FROM movie ORDER BY popularity DESC LIMIT :size")
    LiveData<List<ListMovieEntry>> getMostPopularMovies(int size);

    /**
     * Inserts a list of {@link MovieEntry} into the movieEntries table. If there is a conflicting id
     * the {@link OnConflictStrategy} instructs to replace the popularMovie. The required uniqueness
     * of these values is defined in the {@link MovieEntry}.
     *
     * @param movieEntries A list of movieEntries to insert
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void bulkInsert(MovieEntry... movieEntries);

    /**
     * Deletes the movies not within the offset number of movies.
     *
     * @param moviesToKeep The number of top ranked movies we want to keep in the database
     */
    @Query("DELETE FROM movie WHERE id NOT IN (SELECT id FROM movie ORDER BY popularity DESC LIMIT :moviesToKeep)")
    void deleteOldPopularMovies(int moviesToKeep);

    /**
     * Selects all table elements and counts them
     *
     * @return {@link int} size of table
     */
    @Query("SELECT COUNT(popularity) FROM movie")
    int getMostPopularMoviesSize();

}
