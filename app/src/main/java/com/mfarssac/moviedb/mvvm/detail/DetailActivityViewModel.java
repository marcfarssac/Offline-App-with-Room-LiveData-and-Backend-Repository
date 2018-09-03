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
 */

package com.mfarssac.moviedb.mvvm.detail;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.mfarssac.moviedb.repository.MoviesRepository;
import com.mfarssac.moviedb.repository.room.MovieEntry;

/**
 * {@link ViewModel} for {@link DetailActivity}
 */
class DetailActivityViewModel extends ViewModel {

    // The movie the user is looking at
    private final LiveData<MovieEntry> mMovie;

    // Date for the popular movie
    private final int mId;
    private final MoviesRepository mRepository;

    public DetailActivityViewModel(MoviesRepository repository, int id) {
        mRepository = repository;
        mId = id;
        mMovie = mRepository.getMovieById(mId);
    }

    public LiveData<MovieEntry> getMovie() {
        return mMovie;
    }
}
