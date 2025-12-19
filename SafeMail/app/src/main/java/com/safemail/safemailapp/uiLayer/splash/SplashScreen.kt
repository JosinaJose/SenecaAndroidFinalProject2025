package com.safemail.safemailapp.uiLayer.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.safemail.safemailapp.R


@Composable
fun SplashScreen(
    viewModel: SplashScreenViewModel = viewModel(),
    onNavigate: () -> Unit
) {

    // Navigate when ViewModel is ready - using LaunchedEffect to avoid multiple calls
    LaunchedEffect(viewModel.isReady) {
        if (viewModel.isReady) {
            onNavigate()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.safemail),
            contentDescription = "SafeMail Logo",
            modifier = Modifier.size(160.dp)
        )
    }
}