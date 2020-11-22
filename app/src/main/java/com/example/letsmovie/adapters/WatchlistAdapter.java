package com.example.letsmovie.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.letsmovie.R;
import com.example.letsmovie.databinding.ItemContainerTvShowBinding;
import com.example.letsmovie.listeners.WatchListListeners;
import com.example.letsmovie.models.TVShow;

import java.util.List;

public class WatchlistAdapter extends RecyclerView.Adapter<WatchlistAdapter.TVShowViewHolder>{

    private List<TVShow> tvShows;
    private LayoutInflater layoutInflater;
    private WatchListListeners watchListListeners;


    public WatchlistAdapter(List<TVShow> tvShows, WatchListListeners watchListListeners) {
        this.tvShows = tvShows;
        this.watchListListeners = watchListListeners;
    }

    @NonNull
    @Override
    public TVShowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (layoutInflater == null) {

            layoutInflater = LayoutInflater.from(parent.getContext());
        }
        ItemContainerTvShowBinding tvShowBinding = DataBindingUtil.inflate(
                layoutInflater, R.layout.item_container_tv_show, parent, false
        );
        return new TVShowViewHolder(tvShowBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull TVShowViewHolder holder, int position) {

        holder.bindTVShow(tvShows.get(position));

    }

    @Override
    public int getItemCount() {
        return tvShows.size();
    }

    class TVShowViewHolder extends RecyclerView.ViewHolder{
            private ItemContainerTvShowBinding itemContainerTvShowBinding;

            public TVShowViewHolder(ItemContainerTvShowBinding itemContainerTvShowBinding){
                super(itemContainerTvShowBinding.getRoot());
                this.itemContainerTvShowBinding = itemContainerTvShowBinding;

            }

            public void bindTVShow(TVShow tvShow){
                itemContainerTvShowBinding.setTvShow(tvShow);
                itemContainerTvShowBinding.executePendingBindings();
                itemContainerTvShowBinding.getRoot().setOnClickListener(v -> watchListListeners.onTVShowClicked(tvShow));
                itemContainerTvShowBinding.imageDelete.setOnClickListener(v -> watchListListeners.removeTVShowFromWatchList(tvShow, getAdapterPosition()));
                itemContainerTvShowBinding.imageDelete.setVisibility(View.VISIBLE);
            }
    }
}
