package com.ivzb.urgat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.ivzb.urgat.ui.theme.UrgatTheme
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.ivzb.urgat.navigation.UrgatNavGraph
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            UrgatTheme {
                UrgatNavGraph()
            }
        }

        splashScreen.setOnExitAnimationListener { splashScreenView ->
            splashScreenView.remove()
        }
    }
}