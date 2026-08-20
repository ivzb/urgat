package com.ivzb.urgat

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.glance.appwidget.updateAll
import com.ivzb.urgat.ui.theme.UrgatTheme
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.ivzb.urgat.di.ApplicationScope
import com.ivzb.urgat.navigation.UrgatNavGraph
import com.ivzb.urgat.ui.widget.NetWorthWidget
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    @ApplicationScope
    lateinit var applicationScope: CoroutineScope

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _ -> }

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        requestNotificationPermission()

        setContent {
            UrgatTheme {
                UrgatNavGraph()
            }
        }

        splashScreen.setOnExitAnimationListener { splashScreenView ->
            splashScreenView.remove()
        }
    }

    override fun onStop() {
        super.onStop()

        // A Glance session only lives as long as this process, so once the app is backgrounded
        // nothing recomposes the widget until the next 30 minute system update. Push whatever was
        // edited in this session to the home screen now, while the process is still around.
        applicationScope.launch {
            NetWorthWidget().updateAll(applicationContext)
        }
    }

    private fun requestNotificationPermission() {
        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {

            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}