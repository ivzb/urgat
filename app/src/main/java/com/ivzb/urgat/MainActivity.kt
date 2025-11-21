package com.ivzb.urgat

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.ivzb.urgat.navigation.UrgatNavGraph
import com.ivzb.urgat.ui.theme.UrgatTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) { // Android 15+
            enableEdgeToEdge()
        }

        setContent {
            UrgatTheme {
                UrgatNavGraph()
            }
        }
    }
}