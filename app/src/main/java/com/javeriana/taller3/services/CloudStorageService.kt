package com.javeriana.taller3.services

import android.net.Uri
import android.util.Log
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.io.File

class CloudStorageService {
    fun uploadImageToFirebaseStorage(
        imageUri: Uri,
        imageName: String,
        successCallback: (String) -> Unit,
        errorCallback: (Exception) -> Unit
    ) {
        val storage = FirebaseStorage.getInstance()
        val storageReference = storage.reference
        val imageRef: StorageReference = storageReference.child("images/$imageName")

        val uploadTask: UploadTask = imageRef.putFile(imageUri)

        uploadTask.addOnFailureListener(OnFailureListener { exception ->
            errorCallback(exception)
        }).addOnSuccessListener(OnSuccessListener<UploadTask.TaskSnapshot> { taskSnapshot ->
            val downloadUrlTask = imageRef.downloadUrl
            downloadUrlTask.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUrl = task.result.toString()
                    Log.i("NATA", "downloadUrl: $downloadUrl")
                    successCallback(downloadUrl)
                } else {
                    // Handle download URL retrieval failure
                    errorCallback(task.exception ?: Exception("Download URL retrieval failed"))
                }
            }
        })
    }
}
