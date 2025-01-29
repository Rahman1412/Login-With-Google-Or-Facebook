package com.example.googleandfacebookauth.data

data class SignInResult(
    val data : Userdata?,
    val error : String?
)


data class Userdata(
    val userId : String = "",
    val username : String = "",
    val email :String = "",
    val image : String = "",
    val online: Boolean = false,
    var time : Long? = null,
    val friends: List<String>? = null,
    val statusTime:Long? = null
)