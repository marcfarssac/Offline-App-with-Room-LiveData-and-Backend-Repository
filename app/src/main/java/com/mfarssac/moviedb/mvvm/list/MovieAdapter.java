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

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mfarssac.moviedb.R;
import com.mfarssac.moviedb.repository.room.ListMovieEntry;
import com.mfarssac.moviedb.repository.room.MovieEntry;
import com.squareup.picasso.Picasso;

import java.util.List;

import static com.mfarssac.moviedb.mvvm.detail.DetailActivity.MOVIE_BIG_ICON_HEIGHT;
import static com.mfarssac.moviedb.mvvm.detail.DetailActivity.MOVIE_BIG_ICON_WIDTH;
import static com.mfarssac.moviedb.mvvm.detail.DetailActivity.MOVIE_SMALL_ICON_HEIGHT;
import static com.mfarssac.moviedb.mvvm.detail.DetailActivity.MOVIE_SMALL_ICON_WIDTH;
import static com.mfarssac.moviedb.repository.room.MovieDatabase.MOVIEDB_API_IMAGES_URL;

/**
 * Exposes a list of movies from a list of {@link MovieEntry} to a {@link RecyclerView}.
 */
class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {

    private static final int VIEW_TYPE_MOST_POPULAR = 0;
    private static final int VIEW_TYPE_POPULAR_MOVIE = 1;

    // The context we use to utility methods, app resources and layout inflaters
    private final Context mContext;

    /*
     * Below, we've defined an interface to handle clicks on items within this Adapter. In the
     * constructor of our MovieAdapter, we receive an instance of a class that has implemented
     * said interface. We store that instance in this variable to call the onItemClick method whenever
     * an item is clicked in the list.
     */
    private final MovieAdapterOnItemClickHandler mClickHandler;
    /*
     * Flag to determine if we want to use a separate view for the list item that represents
     * the most popular movie. This flag will be true when the phone is in portrait mode and
     * false when the phone is in landscape. This flag will be set in the constructor of the
     * adapter by accessing boolean resources.
     */
    private final boolean mOneMovieLayout;
    private List<ListMovieEntry> mMovies;

    /**
     * Creates a MovieAdapter.
     *
     * @param context      Used to talk to the UI and app resources
     * @param clickHandler The on-click handler for this adapter. This single handler is called
     *                     when an item is clicked.
     */
    MovieAdapter(@NonNull Context context, MovieAdapterOnItemClickHandler clickHandler) {
        mContext = context;
        mClickHandler = clickHandler;
        mOneMovieLayout = mContext.getResources().getBoolean(R.bool.use_oneMovie_layout);
    }

    /**
     * This gets called when each new ViewHolder is created. This happens when the RecyclerView
     * is laid out. Enough ViewHolders will be created to fill the screen and allow for scrolling.
     *
     * @param viewGroup The ViewGroup that these ViewHolders are contained within.
     * @param viewType  If your RecyclerView has more than one type of item (like ours does) you
     *                  can use this viewType integer to provide a different layout. See
     *                  {@link android.support.v7.widget.RecyclerView.Adapter#getItemViewType(int)}
     *                  for more details.
     * @return A new MovieAdapterViewHolder that holds the View for each list item
     */
    @Override
    public MovieAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        int layoutId = getLayoutIdByType(viewType);
        View view = LayoutInflater.from(mContext).inflate(layoutId, viewGroup, false);
        view.setFocusable(true);
        return new MovieAdapterViewHolder(view);
    }

    /**
     * OnBindViewHolder is called by the RecyclerView to display the data at the specified
     * position. In this method, we update the contents of the ViewHolder to display the movie
     * details for this particular position, using the "position" argument that is conveniently
     * passed into us.
     *
     * @param movieAdapterViewHolder The ViewHolder which should be updated to represent the
     *                                  contents of the item at the given position in the data set.
     * @param position                  The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(MovieAdapterViewHolder movieAdapterViewHolder, int position) {
        ListMovieEntry currentMovie = mMovies.get(position);

        /**************************
         * Movie title *
         **************************/

        int iconWidth = MOVIE_SMALL_ICON_WIDTH;
        int iconHeight = MOVIE_SMALL_ICON_HEIGHT;

        if (position == 1) {
            iconWidth = MOVIE_BIG_ICON_WIDTH;
            iconHeight = MOVIE_BIG_ICON_HEIGHT;
        }

        Picasso.get()
                .load(MOVIEDB_API_IMAGES_URL + currentMovie.getPoster_path())
                .resize(iconWidth,iconHeight)
                .into(movieAdapterViewHolder.iconView);

        String movie_title = currentMovie.getTitle();
        movieAdapterViewHolder.movieTitle.setText(movie_title);
        String popularity = currentMovie.getPopularity();
        movieAdapterViewHolder.popularity.setText(popularity);

    }

    /**
     * This method simply returns the number of items to display. It is used behind the scenes
     * to help layout our Views and for animations.
     *
     * @return The number of items available in our movie list
     */
    @Override
    public int getItemCount() {
        if (null == mMovies) return 0;
        return mMovies.size();
    }

    /**
     * Returns an integer code related to the type of View we want the ViewHolder to be at a given
     * position. This method is useful when we want to use different layouts for different items
     * depending on their position.
     *
     * @param position index within our RecyclerView and list
     * @return the view type (a movie or the most popular one)
     */
    @Override
    public int getItemViewType(int position) {
        if (mOneMovieLayout && position == 0) {
            return VIEW_TYPE_MOST_POPULAR;
        } else {
            return VIEW_TYPE_POPULAR_MOVIE;
        }
    }

    /**
     * Swaps the list used by the MovieAdapter for its movie data. This method is called by
     * {@link MainActivity} after a load has finished. When this method is called, we assume we have
     * a new set of data, so we call notifyDataSetChanged to tell the RecyclerView to update.
     *
     * @param newMoviesList the new list of movies to use as MovieAdapter's data source
     */
    void swapMovies(final List<ListMovieEntry> newMoviesList) {
        // If there was no movie data, then recreate all of the list
        if (mMovies == null) {
            mMovies = newMoviesList;
            notifyDataSetChanged();
        } else {

            DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return mMovies.size();
                }

                @Override
                public int getNewListSize() {
                    return newMoviesList.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return mMovies.get(oldItemPosition).getId() ==
                            newMoviesList.get(newItemPosition).getId();
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    ListMovieEntry newMovie = newMoviesList.get(newItemPosition);
                    ListMovieEntry oldMovie = mMovies.get(oldItemPosition);
                    return (newMovie.getOriginal_title().equals(oldMovie.getOriginal_title()))
                            && (newMovie.getPopularity()==oldMovie.getPopularity());
                }
            });
            mMovies = newMoviesList;
            result.dispatchUpdatesTo(this);
        }
    }

    /**
     * Returns the the layout id depending on whether the list item is a normal item or the most
     *
     * @param viewType
     * @return
     */
    private int getLayoutIdByType(int viewType) {
        switch (viewType) {

            case VIEW_TYPE_MOST_POPULAR: {
                return R.layout.list_item_movie;
            }

            case VIEW_TYPE_POPULAR_MOVIE: {
                return R.layout.movies_list_item;
            }

            default:
                throw new IllegalArgumentException("Invalid view type, value of " + viewType);
        }
    }

    /**
     * The interface that receives onItemClick messages.
     */
    public interface MovieAdapterOnItemClickHandler {
        void onItemClick(int id);
    }

    /**
     * A ViewHolder is a required part of the pattern for RecyclerViews. It mostly behaves as
     * a cache of the child views for a movie item. It's also a convenient place to set an
     * OnClickListener, since it has access to the adapter and the views.
     */
    class MovieAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final ImageView iconView;

        final TextView movieTitle;
        final TextView movieRelease;
        final TextView movieOverviewTeaser;
        final TextView popularity;

        MovieAdapterViewHolder(View view) {
            super(view);

            iconView = view.findViewById(R.id.movie_icon);
            movieTitle = view.findViewById(R.id.movie_title);
            movieRelease = view.findViewById(R.id.popularity);
            popularity = view.findViewById(R.id.popularity);
            movieOverviewTeaser = view.findViewById(R.id.movie_overview_teaser);

            view.setOnClickListener(this);
        }

        /**
         * This gets called by the child views during a click. We fetch the date that has been
         * selected, and then call the onItemClick handler registered with this adapter, passing that
         * date.
         *
         * @param v the View that was clicked
         */
        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            int id = mMovies.get(adapterPosition).getId();
            mClickHandler.onItemClick(id);
        }
    }
}