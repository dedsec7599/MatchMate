package com.harshvardhan.matchmate.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remote_keys")
data class RemoteKeys(
    @PrimaryKey
    val id: String = "matches",
    val nextPage: Int?
)