package com.example.googleandfacebookauth.vm

import android.app.Activity
import android.content.IntentSender
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.googleandfacebookauth.repository.FireAuthRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class LoginVM @Inject constructor(
    private val authRepo: FireAuthRepository,
    private val auth: FirebaseAuth
): ViewModel() {
    val firebaseAuth : FirebaseAuth = auth

    fun getGoogleAuthClient() : FireAuthRepository{
        return authRepo;
    }

}