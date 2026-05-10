package com.practicum.playlistmaker.search.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.practicum.playlistmaker.search.data.NetworkClient
import com.practicum.playlistmaker.search.data.dto.Response
import com.practicum.playlistmaker.search.data.dto.TracksRequest
import java.io.IOException

class RetrofitNetworkClient(private val service: ITunesApiService, private val context: Context) :
    NetworkClient {

    override fun doRequest(dto: Any): Response {
        if (!isConnected()) {
            return Response().apply { resultCode = -1 }
        }
        when (dto) {
            is TracksRequest -> return requestTracks(dto)
        }
        return Response().apply { resultCode = 400 }
    }

    private fun requestTracks(dto: TracksRequest): Response {
        lateinit var body: Response

        try {
            val response = (service).search(dto.expression).execute()
            body = response.body() ?: Response()
            body.resultCode = response.code()
        } catch (_: IOException) {
            body = Response()
        }
        return body
    }

    private fun isConnected(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> return true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> return true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> return true
            }
        }
        return false
    }
}