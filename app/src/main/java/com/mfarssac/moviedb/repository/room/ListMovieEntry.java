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


/**
 * Simplified {@link MovieEntry} which only contains the details needed for the popularity movie list in
 * the {@link com.mfarssac.moviedb.mvvm.list.MovieAdapter}
 */
public class ListMovieEntry {

    private int id;
    private String title;
    private String popularity;
    private String poster_path;
    private String original_language;
    private String original_title;
    private String overview;
    private String votes;
    private String vote_count;


    public ListMovieEntry(int id, String title, String popularity, String poster_path,
                          String original_language, String original_title, String overview,
                          String votes, String vote_count) {
        this.id = id;
        this.title = title;
        this.popularity = popularity;
        this.poster_path = poster_path;
        this.original_language = original_language;
        this.original_title = original_title;
        this.overview = overview;
        this.votes = votes;
        this.vote_count = vote_count;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getPopularity() {
        return popularity;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public String getOriginal_language() {
        return original_language;
    }

    public String getOriginal_title() {
        return original_title;
    }

    public String getOverview() {
        return overview;
    }

    public String getVotes() {
        return votes;
    }

    public String getVote_count() {
        return vote_count;
    }
}
