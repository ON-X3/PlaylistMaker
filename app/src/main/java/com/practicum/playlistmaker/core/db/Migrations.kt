package com.practicum.playlistmaker.core.db

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE playlists_table (
                playlistId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                playlistName TEXT NOT NULL,
                playlistDescription TEXT NOT NULL,
                imagePath TEXT NOT NULL,
                tracks TEXT NOT NULL,
                count INTEGER NOT NULL
            )
            """.trimIndent()
        )
    }
}

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS tracks_in_playlists_table (
                trackId INTEGER NOT NULL,
                trackName TEXT NOT NULL,
                artistName TEXT NOT NULL,
                trackTime INTEGER NOT NULL,
                artworkUrl100 TEXT NOT NULL,
                collectionName TEXT,
                releaseDate TEXT,
                primaryGenreName TEXT NOT NULL,
                country TEXT NOT NULL,
                previewUrl TEXT NOT NULL,
                createdAt INTEGER NOT NULL,
                PRIMARY KEY(trackId)
            )
            """.trimIndent()
        )
    }
}