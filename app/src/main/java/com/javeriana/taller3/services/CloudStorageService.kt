package com.javeriana.taller3.services

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class CloudStorageService {
    var storage:FirebaseStorage= FirebaseStorage.getInstance()
    lateinit var myRef: StorageReference
    private val IMAGEUSERS="images/"
    fun saveImage(currentUser: FirebaseUser?){
        myRef=storage.getReference(IMAGEUSERS+currentUser!!.uid)
    }
}