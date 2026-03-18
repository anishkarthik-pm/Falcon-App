package com.kpn.falcon.util

import kotlinx.coroutines.flow.StateFlow

expect class NetworkMonitor {
    val isConnected: StateFlow<Boolean>
}
