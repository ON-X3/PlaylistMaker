package com.practicum.playlistmaker.search.data

interface StorageClient<T> {
    fun getData(): T?
    fun saveData(data: T)
    fun deleteData()
}