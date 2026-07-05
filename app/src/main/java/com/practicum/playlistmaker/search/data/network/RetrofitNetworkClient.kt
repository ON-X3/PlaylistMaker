package com.practicum.playlistmaker.search.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.practicum.playlistmaker.search.data.NetworkClient
import com.practicum.playlistmaker.search.data.dto.Response
import com.practicum.playlistmaker.search.data.dto.TracksRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RetrofitNetworkClient(private val service: ITunesApiService, private val context: Context) :
    NetworkClient {

    override suspend fun doRequest(dto: Any): Response {
        if (!isConnected()) {
            return Response().apply { resultCode = -1 }
        }
        when (dto) {
            is TracksRequest -> return requestTracks(dto)
        }
        return Response().apply { resultCode = 400 }
    }

    private suspend fun requestTracks(dto: TracksRequest): Response {

        return withContext(Dispatchers.IO) {
            try {
                val response = service.search(dto.expression)
                response.apply { resultCode = 200 }
            } catch (_: Throwable) {
                Response().apply { resultCode = 500 }
            }
        }
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