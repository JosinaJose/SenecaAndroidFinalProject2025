package com.safemail.safemailapp.uiLayer.splash

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashScreenViewModel : ViewModel() {

    var isReady by mutableStateOf(false)
        private set

    init {
        viewModelScope.launch {
            delay(1000)        // simulate loading
            isReady = true
        }
    }
}