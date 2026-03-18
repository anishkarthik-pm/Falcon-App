package com.kpn.falcon.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.kpn.falcon.presentation.KPNFalconApp
import com.kpn.falcon.di.initKoin
import org.koin.android.ext.koin.androidContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        initKoin {
            androidContext(this@MainActivity)
        }

        setContent {
            KPNFalconApp()
        }
    }
}
