package com.example.googleandfacebookauth.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.googleandfacebookauth.pages.FaceBookLogin
import com.example.googleandfacebookauth.pages.HomePage
import com.example.googleandfacebookauth.pages.LoginPage
import com.example.googleandfacebookauth.pages.SplashPage

@Composable
fun Navigation(){
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "splash"
    ){

        composable("splash") {
            SplashPage(navController)
        }

        composable("login"){
            LoginPage(navController)
        }

        composable("home"){
            HomePage(navController)
        }
    }
}