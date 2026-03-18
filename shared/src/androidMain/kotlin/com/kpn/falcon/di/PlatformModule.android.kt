package com.kpn.falcon.di

import com.kpn.falcon.util.NetworkMonitor
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

actual val platformModule = module {
    single { NetworkMonitor(androidContext()) }
}
