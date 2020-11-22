package com.example.letsmovie.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import com.example.letsmovie.database.TVShowsDatabase;
import com.example.letsmovie.models.TVShow;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;

public class WatchlistViewModel extends AndroidViewModel {

    private TVShowsDatabase tvShowsDatabase;

    public WatchlistViewModel(@NonNull Application application)
    {
        super(application);
        tvShowsDatabase = TVShowsDatabase.getTvShowsDatabase(application);
    }
    public Flowable<List<TVShow>> loadWatchList(){
        return tvShowsDatabase.tvShowDao().getWatchList();
    }

    public Completable removeTVShowFromWatchList(TVShow tvShow){
        return tvShowsDatabase.tvShowDao().removeFromWatchlist(tvShow);
    }
}
