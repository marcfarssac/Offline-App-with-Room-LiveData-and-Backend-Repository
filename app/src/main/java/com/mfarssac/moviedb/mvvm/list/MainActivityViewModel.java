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

package com.mfarssac.moviedb.mvvm.list;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.mfarssac.moviedb.repository.MoviesRepository;
import com.mfarssac.moviedb.repository.network.MoviesNetworkDataSource;
import com.mfarssac.moviedb.repository.room.ListMovieEntry;

import java.util.List;

/**
 * {@link ViewModel} for {@link MainActivity}
 */
class MainActivityViewModel extends ViewModel {

    private final MoviesRepository mRepository;
    private final LiveData<List<ListMovieEntry>> mMovies;

    public MainActivityViewModel(MoviesRepository repository) {
        mRepository = repository;
        mMovies = mRepository.getMostPopularMovies(MoviesNetworkDataSource.MAX_POPULAR_MOVIES_NUMBER);
    }

    public LiveData<List<ListMovieEntry>> getMovieEntries() {
        return mMovies;
    }


}
