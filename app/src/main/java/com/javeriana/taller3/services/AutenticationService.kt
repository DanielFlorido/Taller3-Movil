package com.javeriana.taller3.services

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth

class AutenticationService {
    var auth: FirebaseAuth = FirebaseAuth.getInstance()
    fun register(email: String,password:String): Task<AuthResult> {
        return auth.createUserWithEmailAndPassword(email,password)
    }
    fun validEmailAdress(email:String):Boolean{
        val regex= "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$"
        return email.matches(regex.toRegex())
    }
    fun signIn(email: String,password: String):Task<AuthResult>{
        return auth.signInWithEmailAndPassword(email,password)
    }
}