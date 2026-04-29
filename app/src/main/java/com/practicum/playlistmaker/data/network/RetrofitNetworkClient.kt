package com.practicum.playlistmaker.data.network

import com.practicum.playlistmaker.data.NetworkClient
import com.practicum.playlistmaker.data.dto.Response
import com.practicum.playlistmaker.data.dto.TracksRequest
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

class RetrofitNetworkClient (apiService: Class<*>) : NetworkClient {

    private val apiServiceBaseUrl = when (apiService) {
        ITunesApiService::class.java -> I_TUNES_BASE_URL
        else -> ""
    }

    private val retrofit = Retrofit.Builder()
        .baseUrl(apiServiceBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val service = retrofit.create(apiService)

    override fun doRequest(dto: Any): Response {
        when (dto) {
            is TracksRequest -> return requestTracks(dto)
        }
        return Response().apply { resultCode = 400 }
    }

    private fun requestTracks(dto: TracksRequest): Response {
        lateinit var body: Response

        try {
            val responce = (service as ITunesApiService).search(dto.expression).execute()
            body = responce.body() ?: Response()
            body.resultCode = responce.code()
        } catch (e: IOException) {
            body = Response()
        }
        return body
    }
    companion object {
        const val I_TUNES_BASE_URL = "https://itunes.apple.com"
    }
}