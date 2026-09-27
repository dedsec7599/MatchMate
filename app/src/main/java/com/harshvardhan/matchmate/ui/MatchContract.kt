package com.harshvardhan.matchmate.ui

data class MatchUiState(
    val isOffline: Boolean = false,
    val connectivityKnown: Boolean = false
)

sealed interface MatchEvent {

    data class Accept(
        val id: String
    ) : MatchEvent

    data class Decline(
        val id: String
    ) : MatchEvent
}
