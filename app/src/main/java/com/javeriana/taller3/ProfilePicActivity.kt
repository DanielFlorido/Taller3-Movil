package com.javeriana.taller3

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import com.javeriana.taller3.databinding.ActivityProfilePicBinding
import java.io.File


class ProfilePicActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfilePicBinding
    private lateinit var finalImage: Uri
    private var aceptar = false

    val getContentGallery = registerForActivityResult(
        ActivityResultContracts.GetContent(),
        ActivityResultCallback {
            loadImage(it!!)
        }
    )

    val getContentCamera = registerForActivityResult(ActivityResultContracts.TakePicture(),
        ActivityResultCallback {
            if (it) {
                loadImage(cameraUri)
            }
        })


    lateinit var cameraUri: Uri
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfilePicBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val correo = intent.getStringExtra("correo")
        val password = intent.getStringExtra("password")
        val nombre = intent.getStringExtra("nombre")
        val apellido = intent.getStringExtra("apellido")
        val numeroID = intent.getStringExtra("numeroID")

        binding.galleryButton.setOnClickListener {
            getContentGallery.launch("image/*")
        }
        binding.cameraButton.setOnClickListener {
            val file = File(getFilesDir(), "picFromCamera")
            cameraUri = FileProvider.getUriForFile(
                baseContext,
                baseContext.packageName + ".fileprovider",
                file
            )
            getContentCamera.launch(cameraUri)
        }

        binding.aceptarbtn.setOnClickListener {
            if (aceptar) {
                val resultIntent = Intent(this, RegisterActivity::class.java)
                resultIntent.putExtra("image_uri", finalImage.toString())
                resultIntent.putExtra("correo", correo)
                resultIntent.putExtra("password", password)
                resultIntent.putExtra("nombre", nombre)
                resultIntent.putExtra("apellido", apellido)
                resultIntent.putExtra("numeroID", numeroID)
                Log.d("NATA", "la uri que se va a mandar es: $finalImage")
                startActivity(resultIntent)
            } else {
                Toast.makeText(baseContext, "Por favor elija una imagen", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun loadImage(uri: Uri) {
        val imageStream = getContentResolver().openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(imageStream)
        binding.image.setImageBitmap(bitmap)
        finalImage = uri
        aceptar = true
    }


}