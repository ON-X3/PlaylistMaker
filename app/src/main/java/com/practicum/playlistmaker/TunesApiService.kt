package com.practicum.playlistmaker

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface TunesApiService {
    @GET("/search?entity=song")
    fun search (@Query("term") text: String): Call<TracksResponse>

}
