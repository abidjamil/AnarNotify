package com.anaar.io.notify.nav

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.anaar.io.notify.presentation.HomeScreen
import com.anaar.io.notify.presentation.LoginScreen
import com.anaar.io.notify.presentation.SplashScreen
import com.anaar.io.notify.saveUserId
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        composable("splash") {
            SplashScreen(
                onTimeout = {
                    navController.navigate("login") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            )
        }

        composable("login") {
            LoginScreen(onLoginClick = {
                navController.navigate("home"){
                    popUpTo("login") { inclusive = true }
                }
            })
        }

        composable("home"){
            HomeScreen(
                onLogoutClick = {
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }
    }
}
