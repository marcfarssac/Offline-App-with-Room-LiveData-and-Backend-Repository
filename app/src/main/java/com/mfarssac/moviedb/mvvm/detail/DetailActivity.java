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
 */
package com.mfarssac.moviedb.mvvm.detail;

import android.arch.lifecycle.LifecycleActivity;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;

import com.mfarssac.moviedb.R;
import com.mfarssac.moviedb.databinding.ActivityDetailBinding;
import com.mfarssac.moviedb.repository.room.MovieEntry;
import com.mfarssac.moviedb.utils.InjectorUtils;
import com.squareup.picasso.Picasso;

import static com.mfarssac.moviedb.repository.room.MovieDatabase.MOVIEDB_API_IMAGES_URL;

/**
 * Displays a popular movie
 */
public class DetailActivity extends LifecycleActivity {

    public static final String MOVIE_ID_EXTRA = "MOVIE_ID_EXTRA";

    public static int MOVIE_SMALL_ICON_WIDTH = 37;
    public static int MOVIE_SMALL_ICON_HEIGHT = 55;
    public static int MOVIE_BIG_ICON_WIDTH = 55;
    public static int MOVIE_BIG_ICON_HEIGHT = 83;

    /*
     * This field is used for data binding. Normally, we would have to call findViewById many
     * times to get references to the Views in this Activity. With data binding however, we only
     * need to call DataBindingUtil.setContentView and pass in a Context and a layout, as we do
     * in onCreate of this class. Then, we can access all of the Views in our layout
     * programmatically without cluttering up the code with findViewById.
     */
    private ActivityDetailBinding mDetailBinding;
    private DetailActivityViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDetailBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail);
        int id  = getIntent().getIntExtra(MOVIE_ID_EXTRA, -1);

        // Get the ViewModel from the factory
        DetailViewModelFactory factory = InjectorUtils.provideDetailViewModelFactory(this.getApplicationContext(), id);
        mViewModel = ViewModelProviders.of(this, factory).get(DetailActivityViewModel.class);

        // Observers changes in the MovieEntry with the id mId
        mViewModel.getMovie().observe(this, movieEntry -> {
            // If the movie details change, update the UI
            if (movieEntry != null) bindMovieToUI(movieEntry);
        });

    }

    private void bindMovieToUI(MovieEntry movieEntry) {

        /****************
         * movie Icon *
         ****************/

        int iconWidth = MOVIE_BIG_ICON_WIDTH;
        int iconHeight =MOVIE_BIG_ICON_HEIGHT;

        Picasso.get()
                .load(MOVIEDB_API_IMAGES_URL + movieEntry.getPoster_path())
                .resize(iconWidth,iconHeight)
                .into(mDetailBinding.primaryInfo.movieIcon);

        /****************
         * Movie title *
         ****************/
        /* Use the movieId to obtain the proper description */

        String title = movieEntry.getTitle();

        /* Set the text and content description (for accessibility purposes) */
        mDetailBinding.primaryInfo.movieTitle.setText(title);

        /***********************
         * Movie overview *
         ***********************/
        /* Use the overview to obtain the proper description */
        String overview = movieEntry.getOverview();

        /* Set the text and content description (for accessibility purposes) */
        mDetailBinding.primaryInfo.movieOverviewTeaser.setText(overview.substring(0, overview.length()>40 ? 40 : overview.length() ));

        /***********************
         * Movie popularity *
         ***********************/

        String popularity = movieEntry.getPopularity();
        /* Use the movieId to obtain the proper description */
        mDetailBinding.extraDetails.popularity.setText(popularity);

        /***********************
         * Movie vote average  *
         ***********************/

        String vote_average = movieEntry.getVote_average();
        /* Use the movieId to obtain the proper description */
        mDetailBinding.extraDetails.voteAverage.setText(vote_average);

        /***********************
         * Movie vote count    *
         ***********************/

        String vote_count = movieEntry.getVote_count();
        /* Use the movieId to obtain the proper description */
        mDetailBinding.extraDetails.voteCount.setText(vote_count);
    }
}