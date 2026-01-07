package com.safemail.safemailapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

import com.safemail.safemailapp.navigation.MyNavHost
import com.safemail.safemailapp.navigation.NavItem
import com.safemail.safemailapp.scaffold.SafeMailBottomBar
import com.safemail.safemailapp.ui.theme.SafeMailAppTheme
import com.safemail.safemailapp.uiLayer.splash.SplashScreenViewModel

class MainActivity : ComponentActivity() {
    private val splashViewModel: SplashScreenViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
      //  installSplashScreen()
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        splashScreen.setKeepOnScreenCondition {
            !splashViewModel.isReady
        }

        enableEdgeToEdge()
        setContent {
            SafeMailAppTheme {
                val navController = rememberNavController()

                // Track current route to hide bottom bar on Splash/Login/Signup
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                val showBottomBar = currentRoute !in listOf(
                   // NavItem.Splash.route,
                    NavItem.Login.route,
                    NavItem.Signup.route
                )

                Scaffold(
                    bottomBar = {
                        if (showBottomBar) {
                            SafeMailBottomBar(navController = navController)
                        }
                    }
                ) { innerPadding ->
                    // The innerPadding ensures content doesn't overlap the bottom bar
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        MyNavHost(navController = navController)
                    }
                }
            }
        }
    }
}