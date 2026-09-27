package com.harshvardhan.matchmate.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey

@Composable
fun MatchScreen(
    viewModel: MatchViewModel
) {

    val state by viewModel.state.collectAsStateWithLifecycle()

    val matches = viewModel.matches.collectAsLazyPagingItems()
    val listState = remember { LazyListState() }
    val snackbarHostState = remember { SnackbarHostState() }
    var wasOffline by remember { mutableStateOf(false) }

    LaunchedEffect(state.isOffline, state.connectivityKnown) {
        if (!state.connectivityKnown) return@LaunchedEffect

        if (state.isOffline) {
            wasOffline = true
            snackbarHostState.showSnackbar(
                message = "Internet is off",
                duration = SnackbarDuration.Short
            )
        } else if (wasOffline) {
            wasOffline = false
            matches.retry()
            snackbarHostState.showSnackbar(
                message = "Back online",
                duration = SnackbarDuration.Short
            )
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

        Text(
            text = "Profile Matches",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(
                horizontal = 16.dp, vertical = 20.dp
            )
        )

        when (matches.loadState.refresh) {
            is LoadState.Loading if matches.itemCount == 0 -> {

                LoadingState()
            }

            is LoadState.Error if matches.itemCount == 0 -> {

                ErrorState(
                    onRetry = matches::retry
                )
            }

            else -> {

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    state = listState,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {

                    items(
                        count = matches.itemCount,
                        key = matches.itemKey { it.id }
                    ) { index ->
                        matches[index]?.let { match ->
                            MatchCard(
                                match = match,
                                onAccept = { viewModel.onEvent(MatchEvent.Accept(match.id)) },
                                onDecline = { viewModel.onEvent(MatchEvent.Decline(match.id)) }
                            )
                        }
                    }

                    if (matches.loadState.append is LoadState.Loading) {

                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                contentAlignment = Alignment.Center
                            ) {

                                CircularProgressIndicator()
                            }
                        }
                    }

                    if (matches.loadState.append is LoadState.Error) {

                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("Couldn't load more matches.")
                                androidx.compose.material3.TextButton(onClick = matches::retry) {
                                    Text("Retry")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    }
}

@Composable
private fun LoadingState() {

    Box(
        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
    ) {

        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorState(
    onRetry: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Unable to load matches"
        )

        androidx.compose.material3.TextButton(
            onClick = onRetry
        ) {
            Text("Retry")
        }
    }
}
