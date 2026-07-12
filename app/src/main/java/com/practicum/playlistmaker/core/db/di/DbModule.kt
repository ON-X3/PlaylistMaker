package com.practicum.playlistmaker.core.db.di

import androidx.room.Room
import com.practicum.playlistmaker.core.db.AppDatabase
import com.practicum.playlistmaker.core.db.converters.TrackDbConverter
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dbModule = module {
    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "database.db").build()
    }

    factory { TrackDbConverter() }
}