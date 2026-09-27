package com.harshvardhan.matchmate.data.local

import androidx.room.TypeConverter
import com.harshvardhan.matchmate.domain.MatchStatus

class Converters {

    @TypeConverter
    fun fromStatus(status: MatchStatus): String {
        return status.name
    }

    @TypeConverter
    fun toStatus(value: String): MatchStatus {
        return MatchStatus.valueOf(value)
    }
}