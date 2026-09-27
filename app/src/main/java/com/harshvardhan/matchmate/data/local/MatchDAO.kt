package com.harshvardhan.matchmate.data.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface MatchDao {

    @Query("""
        SELECT * FROM matches
        WHERE :acceptedOnly = 0 OR status = 'ACCEPTED'
        ORDER BY page ASC
    """)
    fun pagingSource(acceptedOnly: Boolean): PagingSource<Int, MatchEntity>

    @Upsert
    suspend fun upsertMatches(matches: List<MatchEntity>)

    @Query("""
        UPDATE matches
        SET status = :status
        WHERE id = :id
    """)
    suspend fun updateDecision(
        id: String,
        status: String
    )

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRemoteKeys(keys: RemoteKeys)

    @Query("SELECT * FROM remote_keys WHERE id = 'matches'")
    suspend fun remoteKeys(): RemoteKeys?

    @Query("DELETE FROM remote_keys")
    suspend fun clearRemoteKeys()

    @Query("SELECT COUNT(*) FROM matches")
    suspend fun count(): Int
}