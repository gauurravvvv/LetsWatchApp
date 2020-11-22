package com.example.letsmovie.listeners;

import com.example.letsmovie.models.TVShow;

public interface WatchListListeners {
    void onTVShowClicked(TVShow tvShow);

    void removeTVShowFromWatchList(TVShow tvShow, int position);
}
