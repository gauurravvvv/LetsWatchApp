package com.example.letsmovie.responses;

import com.example.letsmovie.models.TVShowDetails;
import com.google.gson.annotations.SerializedName;

public class TVShowDetailsResponse {

    @SerializedName("tvShow")
    private TVShowDetails tvShowDetails;

    public TVShowDetails getTvShowDetails(){
        return  tvShowDetails;
    }
}
