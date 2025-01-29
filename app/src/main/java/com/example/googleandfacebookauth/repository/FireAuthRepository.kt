package com.example.googleandfacebookauth.repository

import android.content.Context
import android.content.Intent
import android.content.IntentSender
import com.example.googleandfacebookauth.R
import com.example.googleandfacebookauth.data.SignInResult
import com.example.googleandfacebookauth.data.Userdata
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.cancellation.CancellationException

@Singleton
class FireAuthRepository @Inject constructor(
    private val auth : FirebaseAuth,
    @ApplicationContext private val context: Context,
    private val oneTapClient: SignInClient
) {

    private val _currentUser = MutableStateFlow(auth.currentUser)
    val currentUser: StateFlow<FirebaseUser?> get() = _currentUser

    init {
        auth.addAuthStateListener { firebaseAuth ->
            _currentUser.value = firebaseAuth.currentUser
        }
    }

    suspend fun signIn(): IntentSender? {
        val result = try{
            oneTapClient.beginSignIn(
                buildSignInRequest()
            ).await()
        }catch(e:Exception){
            e.printStackTrace()
            if(e is CancellationException)
                throw e
            else
                null
        }
        return result?.pendingIntent?.intentSender
    }

    fun signOut() {
        auth.signOut()
    }

    suspend fun signInWithIntent(intent: Intent): SignInResult {
        val credential = oneTapClient.getSignInCredentialFromIntent(intent)
        val googleIdToken = credential.googleIdToken
        val googleCredentials = GoogleAuthProvider.getCredential(googleIdToken,null)

        return try{
            val user = auth.signInWithCredential(googleCredentials).await().user
            SignInResult(
                data = user?.run{
                    Userdata(
                        userId = user.uid,
                        username = user.displayName.toString(),
                        image = user.photoUrl.toString(),
                        email = user.email.toString()
                    )
                },
                error = null
            )
        }catch (e : Exception){
            e.printStackTrace()
            if(e is CancellationException) throw e
            SignInResult(
                data = null,
                error = e.message
            )
        }
    }

    private fun buildSignInRequest() : BeginSignInRequest{
        return BeginSignInRequest.Builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(context.getString(R.string.web_client_id))
                    .build()
            )
            .setAutoSelectEnabled(true)
            .build()
    }
}