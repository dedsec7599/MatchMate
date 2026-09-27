package com.harshvardhan.matchmate.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlin.time.Duration.Companion.milliseconds

@OptIn(FlowPreview::class)
class ConnectivityObserver(
    context: Context
) {

    private val connectivityManager =
        context.getSystemService(ConnectivityManager::class.java)

    val isOnline: Flow<Boolean> = callbackFlow {

        fun isValidatedNetworkAvailable(): Boolean =
            connectivityManager.getNetworkCapabilities(
                connectivityManager.activeNetwork
            )?.hasCapability(
                NetworkCapabilities.NET_CAPABILITY_VALIDATED
            ) == true

        trySend(isValidatedNetworkAvailable())

        val callback = object : ConnectivityManager.NetworkCallback() {

            override fun onAvailable(network: Network) {
                trySend(isValidatedNetworkAvailable())
            }

            override fun onLost(network: Network) {
                trySend(isValidatedNetworkAvailable())
            }

            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                // Only the VALIDATED bit matters to us — everything else
                // (signal strength, bandwidth estimates) is noise for this use case.
                trySend(
                    networkCapabilities.hasCapability(
                        NetworkCapabilities.NET_CAPABILITY_VALIDATED
                    )
                )
            }
        }

        connectivityManager.registerDefaultNetworkCallback(callback)

        awaitClose {
            connectivityManager.unregisterNetworkCallback(callback)
        }
    }
        .distinctUntilChanged()
        .debounce(1_000.milliseconds) // let transient handoff/validation flicker settle before emitting
}
