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

import android.support.annotation.NonNull;

import com.mfarssac.moviedb.repository.room.MovieEntry;

/**
 * movie response from the backend. Contains the movies
 */
class MovieDbResponse {

    @NonNull
    private final MovieEntry[] mMovies;

    public MovieDbResponse(@NonNull final MovieEntry[] movies) {
        mMovies = movies;
    }

    public MovieEntry[] getMovies() {
        return mMovies;
    }

}