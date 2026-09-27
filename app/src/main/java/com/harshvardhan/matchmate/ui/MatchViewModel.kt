@file:OptIn(FlowPreview::class)

package com.harshvardhan.matchmate.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.harshvardhan.matchmate.data.repos.MatchRepository
import com.harshvardhan.matchmate.domain.MatchStatus
import com.harshvardhan.matchmate.util.ConnectivityObserver
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalCoroutinesApi::class)
class MatchViewModel(
    private val repository: MatchRepository,
    connectivityObserver: ConnectivityObserver,
) : ViewModel() {

    private val _state = MutableStateFlow(
        MatchUiState()
    )

    val state: StateFlow<MatchUiState> = _state.asStateFlow()

    private val isOfflineState = state
        .map { it.isOffline }
        .distinctUntilChanged()
        .debounce(500.milliseconds)
        .stateIn(viewModelScope, SharingStarted.Eagerly, _state.value.isOffline)

    val matches = repository
        .getMatches(isOfflineState)
        .cachedIn(viewModelScope)

    init {

        observeConnectivity(
            connectivityObserver
        )
    }

    fun onEvent(event: MatchEvent) {

        when (event) {

            is MatchEvent.Accept -> {

                updateDecision(
                    event.id, MatchStatus.ACCEPTED
                )
            }

            is MatchEvent.Decline -> {

                updateDecision(
                    event.id, MatchStatus.DECLINED
                )
            }
        }
    }

    private fun updateDecision(
        id: String,
        status: MatchStatus
    ) {
        viewModelScope.launch {

            repository.updateDecision(
                id = id,
                status = status
            )
        }
    }

    private fun observeConnectivity(
        observer: ConnectivityObserver
    ) {

        viewModelScope.launch {

            observer.isOnline.collect { online ->

                _state.value = _state.value.copy(
                    isOffline = !online,
                    connectivityKnown = true
                )
            }
        }
    }
}

class MatchViewModelFactory(
    private val repository: MatchRepository,
    private val context: Context
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        return MatchViewModel(
            repository = repository,
            connectivityObserver =
                ConnectivityObserver(context)
        ) as T
    }
}
