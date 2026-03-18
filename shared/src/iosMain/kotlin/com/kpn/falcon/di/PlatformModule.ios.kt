package com.kpn.falcon.di

import com.kpn.falcon.util.FilePicker
import com.kpn.falcon.util.NetworkMonitor
import org.koin.dsl.module

actual val platformModule = module {
    single { NetworkMonitor() }
    single { FilePicker() }
}
