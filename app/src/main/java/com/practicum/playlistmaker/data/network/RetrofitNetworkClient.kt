package com.practicum.playlistmaker.data.network

import com.practicum.playlistmaker.data.NetworkClient
import com.practicum.playlistmaker.data.dto.Response
import com.practicum.playlistmaker.data.dto.TracksRequest
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

class RetrofitNetworkClient : NetworkClient {

    private val iTunesBaseUrl = "https://itunes.apple.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(iTunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val iTunesService = retrofit.create(iTunesApiService::class.java)

    override fun doRequest(dto: Any): Response {
        when (dto) {
            is TracksRequest -> return requestTracks(dto)
        }
        return Response().apply { resultCode = 400 }
    }

    private fun requestTracks(dto: TracksRequest): Response {
        lateinit var body: Response

        try {
            val responce = iTunesService.search(dto.expression).execute()
            body = responce.body() ?: Response()
            body.resultCode = responce.code()
        } catch (e: IOException) {
            body = Response()
        }
        return body
    }

}