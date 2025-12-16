package com.safemail.safemailapp.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.safemail.safemailapp.R
import com.safemail.safemailapp.views.SplashScreenViewModel


@Composable
fun SplashScreen(
    viewModel: SplashScreenViewModel = viewModel(),
    onNavigate: () -> Unit
) {

    // Navigate when ViewModel is ready
    if (viewModel.isReady) {
        onNavigate()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White), // Use Compose's Color.White
        contentAlignment = Alignment.Center // Correct placement
    ) {
        Image(
            painter = painterResource(id = R.drawable.safemail),
            contentDescription = "SafeMail Logo",
            modifier = Modifier.size(160.dp)
        )
    }
}