package com.example.googleandfacebookauth.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.googleandfacebookauth.repository.FireAuthRepository
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class HomeVM @Inject constructor(
    private val authRepository: FireAuthRepository
) : ViewModel() {

    val currentUser = authRepository.currentUser

    fun doLogOut(){
        authRepository.signOut()
    }

}