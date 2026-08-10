package com.practicum.playlistmaker.core.db

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import org.json.JSONArray

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

val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS playlist_track_in_playlist_table (
                playlistId INTEGER NOT NULL,
                trackId INTEGER NOT NULL,
                PRIMARY KEY(playlistId, trackId),
                FOREIGN KEY(playlistId) REFERENCES playlists_table(playlistId) ON UPDATE NO ACTION ON DELETE CASCADE,
                FOREIGN KEY(trackId) REFERENCES tracks_in_playlists_table(trackId) ON UPDATE NO ACTION ON DELETE CASCADE
            )
            """.trimIndent()
        )

        val cursor = db.query(
            """
            SELECT playlistId, tracks
            FROM playlists_table
            """.trimIndent()
        )

        cursor.use {
            val playlistIdIndex = it.getColumnIndexOrThrow("playlistId")
            val tracksIndex = it.getColumnIndexOrThrow("tracks")

            while (it.moveToNext()) {
                val playlistId = it.getInt(playlistIdIndex)
                val tracksJson = it.getString(tracksIndex)

                if (tracksJson.isBlank()) { continue }

                val trackIds = JSONArray(tracksJson)

                for (i in 0 until trackIds.length()) {
                    val trackId = trackIds.getLong(i)

                    db.execSQL(
                        """
                        INSERT OR IGNORE INTO playlist_track_in_playlist_table
                        (playlistId, trackId)
                        VALUES (?, ?)
                        """.trimIndent(),
                        arrayOf<Any>(playlistId, trackId)
                    )
                }
            }
        }

        db.execSQL(
            """
            ALTER TABLE playlists_table
            DROP COLUMN tracks
            """.trimIndent()
        )

        db.execSQL(
            """
            ALTER TABLE playlists_table
            DROP COLUMN count
            """.trimIndent()
        )
    }
}