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

package com.mfarssac.moviedb.utils;

import android.content.Context;

import com.mfarssac.moviedb.AppExecutors;
import com.mfarssac.moviedb.mvvm.detail.DetailViewModelFactory;
import com.mfarssac.moviedb.mvvm.list.MainViewModelFactory;
import com.mfarssac.moviedb.repository.MoviesRepository;
import com.mfarssac.moviedb.repository.network.MoviesNetworkDataSource;
import com.mfarssac.moviedb.repository.room.MovieDatabase;

/**
 * Provides static methods to inject the various classes needed for the MovieDB
 */
public class InjectorUtils {

    public static MoviesRepository provideRepository(Context context) {
        MovieDatabase database = MovieDatabase.getInstance(context.getApplicationContext());
        AppExecutors executors = AppExecutors.getInstance();
        MoviesNetworkDataSource networkDataSource =
                MoviesNetworkDataSource.getInstance(context.getApplicationContext(), executors);
        return MoviesRepository.getInstance(database.MoviesDao(), networkDataSource, executors);
    }

    public static MoviesNetworkDataSource provideNetworkDataSource(Context context) {
        // This call to provide repository is necessary if the app starts from a service - in this
        // case the repository will not exist unless it is specifically created.
        provideRepository(context.getApplicationContext());
        AppExecutors executors = AppExecutors.getInstance();
        return MoviesNetworkDataSource.getInstance(context.getApplicationContext(), executors);
    }

    public static DetailViewModelFactory provideDetailViewModelFactory(Context context, int id) {
        MoviesRepository repository = provideRepository(context.getApplicationContext());
        return new DetailViewModelFactory(repository, id);
    }

    public static MainViewModelFactory provideMainActivityViewModelFactory(Context context) {
        MoviesRepository repository = provideRepository(context.getApplicationContext());
        return new MainViewModelFactory(repository);
    }

}