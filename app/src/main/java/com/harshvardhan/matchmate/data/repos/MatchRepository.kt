@file:OptIn(ExperimentalPagingApi::class)

package com.harshvardhan.matchmate.data.repos

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.harshvardhan.matchmate.data.local.MatchDatabase
import com.harshvardhan.matchmate.data.local.MatchEntity
import com.harshvardhan.matchmate.data.local.RemoteKeys
import com.harshvardhan.matchmate.data.remote.RandomUserApi
import com.harshvardhan.matchmate.domain.MatchStatus
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException
import kotlin.time.Duration.Companion.milliseconds

private const val PAGE_SIZE = 10
private const val SEED = "matchmate"

internal fun matchPagingConfig() = PagingConfig(
    pageSize = PAGE_SIZE,
    initialLoadSize = PAGE_SIZE,
    prefetchDistance = 2,
    enablePlaceholders = true
)

class MatchRepository(
    private val api: RandomUserApi,
    private val database: MatchDatabase
) {

    fun getMatches(isOfflineState: StateFlow<Boolean>): Flow<PagingData<MatchEntity>> {
        return Pager(
            config = matchPagingConfig(),
            remoteMediator = MatchRemoteMediator(api, database, isOfflineState),
            pagingSourceFactory = {
                database.matchDao().pagingSource(acceptedOnly = false)
            }
        ).flow
    }

    suspend fun updateDecision(
        id: String,
        status: MatchStatus
    ) {
        database.matchDao().updateDecision(
            id = id,
            status = status.name
        )
    }
}

@OptIn(ExperimentalPagingApi::class)
private class MatchRemoteMediator(
    private val api: RandomUserApi,
    private val database: MatchDatabase,
    private val isOfflineFlow: Flow<Boolean>
) : RemoteMediator<Int, MatchEntity>() {

    override suspend fun initialize(): InitializeAction {
        val count = database.matchDao().count()
        return if (count == 0) {
            InitializeAction.LAUNCH_INITIAL_REFRESH
        } else {
            InitializeAction.SKIP_INITIAL_REFRESH
        }
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, MatchEntity>
    ): MediatorResult {

        return try {
            if (isOfflineFlow.first()) {
                return MediatorResult.Error(IOException("Offline"))
            }

            if (loadType == LoadType.APPEND) {
                delay(1_000.milliseconds)
            }

            val page = when (loadType) {

                LoadType.REFRESH -> 1

                LoadType.PREPEND -> {
                    return MediatorResult.Success(endOfPaginationReached = true)
                }

                LoadType.APPEND -> {
                    database.matchDao()
                        .remoteKeys()
                        ?.nextPage
                        ?: return MediatorResult.Success(endOfPaginationReached = true)
                }
            }

            val response = api.getUsers(
                page = page,
                results = PAGE_SIZE,
                seed = SEED
            )

            val users = response.results

            database.withTransaction {

                if (loadType == LoadType.REFRESH) {
                    database.matchDao().clearRemoteKeys()
                }

                val entities = users.map { user ->
                    MatchEntity(
                        id = user.login.uuid,
                        firstName = user.name.first,
                        lastName = user.name.last,
                        age = user.dob.age,
                        city = user.location.city,
                        state = user.location.state,
                        country = user.location.country,
                        imageUrl = user.picture.large,
                        page = page
                    )
                }

                database.matchDao().upsertMatches(entities)

                database.matchDao().insertRemoteKeys(
                    RemoteKeys(
                        nextPage = if (users.isEmpty()) null else page + 1
                    )
                )
            }

            MediatorResult.Success(endOfPaginationReached = users.isEmpty())
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }
}
