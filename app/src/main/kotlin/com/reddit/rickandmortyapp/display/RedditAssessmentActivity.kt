package com.reddit.rickandmortyapp.display

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.graphics.toArgb
import com.reddit.rickandmortyapp.display.composables.RedditAssessmentHomeScreen
import com.reddit.rickandmortyapp.display.theme.Transparent_Grey
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RedditAssessmentActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Transparent_Grey.toArgb())
        )
        setContent {
            RedditAssessmentHomeScreen()
        }
    }
}