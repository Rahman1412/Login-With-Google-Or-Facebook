package com.example.googleandfacebookauth.pages
import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import com.example.googleandfacebookauth.utils.ToastUtils
import com.example.googleandfacebookauth.vm.LoginVM
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.firebase.Firebase
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await

@Composable
fun LoginPage(
    navController: NavController
){
    val vm : LoginVM = hiltViewModel()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val firebaseAuth = vm.firebaseAuth
    val callbackManager by remember { mutableStateOf(CallbackManager.Factory.create()) }


    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
        onResult = { activityResult ->
            if(activityResult.resultCode == Activity.RESULT_OK){
                runBlocking {
                    val signIntResult= vm.getGoogleAuthClient().signInWithIntent(
                        intent = activityResult.data ?: return@runBlocking
                    )
                    if(signIntResult.data != null){
                        navController.navigate("home"){
                            popUpTo("login"){
                                inclusive = true
                            }
                        }
                    }else{
                        ToastUtils.displayToast(context,"Something went wrong, Please try again")
                    }
                }
            }
        }
    )

    val loginwithFb : () -> Unit = {

        try{
            LoginManager.getInstance().logInWithReadPermissions(
                context as Activity, listOf("email", "public_profile")
            )

            LoginManager.getInstance().registerCallback(callbackManager,
                object : FacebookCallback<LoginResult> {
                    override fun onSuccess(result: LoginResult) {
                        Log.d("FacebookAuth Success", "Login canceled")
                        val accessToken = result.accessToken
                        val credential = FacebookAuthProvider.getCredential(accessToken.token)

                        firebaseAuth.signInWithCredential(credential).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Log.d("FacebookAuth", "Success: ${firebaseAuth.currentUser?.email}")
                            } else {
                                Log.e("FacebookAuth", "Error: ${task.exception?.message}")
                            }
                        }
                    }

                    override fun onCancel() {
                        Log.d("FacebookAuth Cancel", "Login canceled")
                    }

                    override fun onError(error: FacebookException) {
                        Log.d("FacebookAuth ERROR", "Error: ${error.message}")
                    }
                })
        }catch(e:Exception){
            Log.d("Getting Exception","${e.message}")
        }
    }




    Box(
        modifier = Modifier.fillMaxSize().imePadding()
    ){
        Column(
            modifier = Modifier.fillMaxSize().padding(20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = {
                    lifecycleOwner.lifecycleScope.launch{
                        vm.getGoogleAuthClient().signIn()?.let {
                            IntentSenderRequest.Builder(
                                it
                            ).build()
                        }?.let {
                            launcher.launch(
                                it
                            )
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Login With Google")
            }
            FaceBookLogin(navController)
        }
    }
}


@Composable
fun FaceBookLogin(
    navController: NavController
) {
    val scope = rememberCoroutineScope()

    AndroidView({ context ->
        LoginButton(context).apply {
            val callbackManager = CallbackManager.Factory.create()
            setPermissions("email", "public_profile")
            registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
                override fun onCancel() {
                    Toast.makeText(context,"Cancel",Toast.LENGTH_SHORT).show()
                }

                override fun onError(error: FacebookException) {
                    Toast.makeText(context,"Error",Toast.LENGTH_SHORT).show()
                }

                override fun onSuccess(result: LoginResult) {
                    scope.launch {
                        val token = result.accessToken.token
                        Toast.makeText(context,"Success Result - ${token}",Toast.LENGTH_SHORT).show()
                        val credential = FacebookAuthProvider.getCredential(token)
                        val authResult = Firebase.auth.signInWithCredential(credential).await()
                        if (authResult.user != null) {
                            navController.navigate("home"){
                                popUpTo("login"){
                                    inclusive = true
                                }
                            }
                        } else {
                            Toast.makeText(context,"Success Not User",Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            })
        }
    })
}