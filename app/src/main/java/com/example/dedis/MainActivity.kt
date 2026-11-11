package com.example.dedis

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.dedis.ui.DedisNavGraph
import com.example.dedis.ui.theme.DEDISTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DEDISTheme {
                DedisNavGraph()
            }
        }
    }
}
