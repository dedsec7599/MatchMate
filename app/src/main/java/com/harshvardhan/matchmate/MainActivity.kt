package com.harshvardhan.matchmate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.harshvardhan.matchmate.ui.MatchScreen
import com.harshvardhan.matchmate.ui.MatchViewModel
import com.harshvardhan.matchmate.ui.MatchViewModelFactory
import com.harshvardhan.matchmate.ui.theme.MatchMateTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        val app =
            application as MatchMateApp

        setContent {
            MatchMateTheme {
                val viewModel: MatchViewModel =
                    viewModel(
                        factory =
                            MatchViewModelFactory(
                                repository =
                                    app.repository,
                                context = applicationContext
                            )
                    )

                MatchScreen(
                    viewModel = viewModel
                )
            }
        }
    }
}