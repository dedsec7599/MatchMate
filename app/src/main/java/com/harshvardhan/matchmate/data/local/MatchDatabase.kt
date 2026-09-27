package com.harshvardhan.matchmate.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [
        MatchEntity::class,
        RemoteKeys::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class MatchDatabase : RoomDatabase() {

    abstract fun matchDao(): MatchDao
}