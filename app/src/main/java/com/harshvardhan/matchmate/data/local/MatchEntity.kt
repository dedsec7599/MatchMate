package com.harshvardhan.matchmate.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.harshvardhan.matchmate.domain.MatchStatus

@Entity(tableName = "matches")
data class MatchEntity(
    @PrimaryKey
    val id: String,
    val firstName: String,
    val lastName: String,
    val age: Int,
    val city: String,
    val state: String,
    val country: String,
    val imageUrl: String,
    val page: Int,
    val status: MatchStatus = MatchStatus.PENDING
)