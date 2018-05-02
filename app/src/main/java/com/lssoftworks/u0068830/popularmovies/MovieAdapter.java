package com.lssoftworks.u0068830.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lssoftworks.u0068830.popularmovies.utilities.MovieData;
import com.lssoftworks.u0068830.popularmovies.utilities.NetworkUtils;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.net.URL;

/**
 * Created by u0068830 on 12/03/2018.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private MovieData[] mMoviePosters;

    public MovieAdapter(MovieData[] moviePosters) {
        mMoviePosters = moviePosters;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();

        int layoutIdForListItem = R.layout.list_item;
        LayoutInflater inflater = LayoutInflater.from(context);

        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        MovieViewHolder viewHolder = new MovieViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        if(null == mMoviePosters) return 0;
        return mMoviePosters.length;
    }

    class MovieViewHolder extends RecyclerView.ViewHolder {
        ImageView mMoviePoster;
        TextView mTitle;

        public MovieViewHolder (View itemView) {
            super(itemView);

            mMoviePoster = itemView.findViewById(R.id.iv_movieposter);
            mTitle = itemView.findViewById(R.id.tv_title);
            itemView.setOnClickListener(MainActivity.viewholderClickListener);
        }

        void bind(int listIndex) {
            URL url = NetworkUtils.buildPosterUrl(mMoviePosters[listIndex].getPosterPath());

            Picasso.get().load(url.toString()).fetch();
            Picasso.get().load(url.toString()).into(mMoviePoster);
            mMoviePoster.setTag(mMoviePosters[listIndex].getId());

            mTitle.setText(mMoviePosters[listIndex].getOriginalTitle());
        }
    }

    public void setMovieData(MovieData[] movieData) {
        mMoviePosters = movieData;
        notifyDataSetChanged();
    }
}
