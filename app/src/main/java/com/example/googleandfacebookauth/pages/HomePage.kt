package com.example.googleandfacebookauth.pages

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.googleandfacebookauth.vm.HomeVM
import com.facebook.login.LoginManager
import com.facebook.login.widget.LoginButton
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@Composable
fun HomePage(
    navController: NavController
){
    val homevm : HomeVM = hiltViewModel()

    val user by homevm.currentUser.collectAsState()

    LaunchedEffect(user) {
        if(user == null){
            LoginManager.getInstance().logOut()
            navController.navigate("login"){
                popUpTo("home"){
                    inclusive = true
                }
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ){
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("${user?.uid}")
            Text("${user?.displayName}")
            Text("${user?.email}")
            Button(
                onClick = {
                    homevm.doLogOut()
                }
            ) {
                Text("Log Out")
            }
        }
    }
}