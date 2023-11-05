package com.javeriana.taller3

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import com.javeriana.taller3.databinding.ActivityProfilePicBinding
import java.io.File

class ProfilePicActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfilePicBinding
    private lateinit var uriCamera: Uri
    private val getContentGallery= registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) {
        loadImage(it!!)
    }
    private val getContentCamera= registerForActivityResult(
        ActivityResultContracts.GetContent(), ActivityResultCallback {
            loadImage(it!!)
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityProfilePicBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setContentView(binding.root)
        binding.galleryButton.setOnClickListener{
            getContentGallery.launch("image/*")
        }
        binding.cameraButton.setOnClickListener{
            val file= File(filesDir,"picFromCamera")
            uriCamera=
                FileProvider.getUriForFile(baseContext,baseContext.packageName+".fileprovider", file)
            getContentCamera.launch(uriCamera.toString())
        }
    }
    private fun loadImage(uri:Uri){
        val imageStream= contentResolver.openInputStream(uri)
        val bitmap= BitmapFactory.decodeStream(imageStream)
        binding.image.setImageBitmap(bitmap)
    }
}