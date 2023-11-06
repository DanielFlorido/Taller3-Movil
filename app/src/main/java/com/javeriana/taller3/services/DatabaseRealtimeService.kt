package com.javeriana.taller3.services

import android.database.Cursor
import android.database.MatrixCursor
import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.javeriana.taller3.model.Usuario

class DatabaseRealtimeService {
    private var database:FirebaseDatabase= FirebaseDatabase.getInstance()
    private lateinit var myRef: DatabaseReference
    private val USERS="users/"
    private val DISPONIBLES= "disponibles/"
    var listUsers: MutableList<Usuario> = mutableListOf()
    private lateinit var data: ValueEventListener
    fun saveUser(usuario:Usuario, currentUser: FirebaseUser?){
        myRef=database.getReference(USERS+currentUser!!.uid)
        myRef.setValue(usuario)
    }
    fun saveDisponible(usuario: Usuario, currentUser: FirebaseUser?){
        myRef=database.getReference(DISPONIBLES+currentUser!!.uid)
        myRef.setValue(usuario)
    }
    fun deleteDisponible(currentUser: FirebaseUser?){
        myRef=database.getReference(DISPONIBLES+currentUser!!.uid)
        myRef.removeValue()
    }
    fun readDisponibles(f:()-> Unit){
        data =object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                listUsers.clear()
                if (listUsers.isEmpty()){
                    for(single in snapshot.children){
                        val user= single.getValue(Usuario::class.java)
                        if (user != null) {
                            listUsers.add(user)
                        }
                    }
                }
                f()
            }
            override fun onCancelled(error: DatabaseError) {
                Log.w("FirebaseOne", "Failed to read value.", error.toException())
            }
        }
        myRef=database.getReference(DISPONIBLES)
        myRef.addValueEventListener(data)
    }
    fun endSubscription(){
        database.getReference(DISPONIBLES).removeEventListener(data)
    }
    fun cursor():Cursor{
        val cursor2= MatrixCursor(arrayOf("_id", "nombre","urlFile", "key"), listUsers.size)
        var i=1
        for (user in listUsers){
            cursor2.newRow().add("_id",i)
                .add("nombre",user.nombre)
                .add("urlFile", user.urlImage)
                .add("key", user.key)
            i++
        }
        return cursor2
    }
    fun getUser(currentUser: FirebaseUser?, co: OnCompleteListener<DataSnapshot>):Task<DataSnapshot>{
        return database.getReference(USERS).child(currentUser!!.uid).get().addOnCompleteListener(co)
    }
    fun getUser(key:String, co:OnCompleteListener<DataSnapshot>):Task<DataSnapshot>{
        return database.getReference(DISPONIBLES).child(key).get().addOnCompleteListener(co)
    }
    private val usersRef = database.getReference("users")
    fun listenForAvailabilityChanges(listener: ValueEventListener) {
        usersRef.addValueEventListener(listener)
    }
}