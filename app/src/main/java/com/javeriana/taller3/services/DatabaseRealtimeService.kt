package com.javeriana.taller3.services

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.javeriana.taller3.model.Usuario

class DatabaseRealtimeService {
    private var database:FirebaseDatabase= FirebaseDatabase.getInstance()
    private lateinit var myRef: DatabaseReference
    private val USERS="users/"
    fun saveUser(usuario:Usuario, currentUser: FirebaseUser?){
        myRef=database.getReference(USERS+currentUser!!.uid)
        myRef.setValue(usuario)
    }
}