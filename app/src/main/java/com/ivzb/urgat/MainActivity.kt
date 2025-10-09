package com.ivzb.urgat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.ivzb.urgat.navigation.UrgatNavGraph
import com.ivzb.urgat.ui.theme.UrgatTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            UrgatTheme {
                UrgatNavGraph()
            }
        }
    }
}