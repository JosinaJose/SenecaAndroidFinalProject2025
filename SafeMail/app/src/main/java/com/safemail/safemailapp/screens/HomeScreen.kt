package com.safemail.safemailapp.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel



import com.safemail.safemailapp.R
import com.safemail.safemailapp.components.NormalTextComponent
import com.safemail.safemailapp.scaffold.SafeMailBottomBar
import com.safemail.safemailapp.views.HomeViewModel


@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel()
) {
    NormalTextComponent(
        value = stringResource(R.string.greeting_message)
    )
    Scaffold(
        bottomBar = { SafeMailBottomBar() },
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("Welcome!") // You can update this text later
            }
        }
    )
}