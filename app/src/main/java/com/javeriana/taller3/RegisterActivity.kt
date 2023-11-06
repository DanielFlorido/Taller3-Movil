package com.javeriana.taller3

import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.javeriana.taller3.controller.MundoController
import com.javeriana.taller3.controller.MundoController.Companion.autenticationService
import com.javeriana.taller3.controller.MundoController.Companion.cloudStorageService
import com.javeriana.taller3.controller.MundoController.Companion.databaseRealtimeService
import com.javeriana.taller3.controller.MundoController.Companion.getInstancia
import com.javeriana.taller3.controller.MundoController.Companion.usuario
import com.javeriana.taller3.databinding.ActivityRegisterBinding
import com.javeriana.taller3.model.Usuario
import com.javeriana.taller3.services.CloudStorageService

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private var imagenSubida = false
    private lateinit var imageUri : Uri
    private lateinit var imageName: String

    private lateinit var mundoController:MundoController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityRegisterBinding.inflate(layoutInflater)
        mundoController= getInstancia()
        cloudStorageService = CloudStorageService()
        setContentView(binding.root)
        binding.SubirImagen.setOnClickListener {
            val profilePicIntent = Intent(this, ProfilePicActivity::class.java)
            profilePicIntent.putExtra("correo", binding.correotxt.text.toString())
            profilePicIntent.putExtra("password", binding.passwordtxt.text.toString())
            profilePicIntent.putExtra("nombre", binding.nombretxt.text.toString())
            profilePicIntent.putExtra("apellido", binding.apellidotxt.text.toString())
            profilePicIntent.putExtra("numeroID", binding.numeroID.text.toString())
            startActivity(profilePicIntent)

        }
        binding.SignInButton.setOnClickListener {
            if(validateData()){
                autenticationService.register(binding.correotxt.text.toString(),binding.passwordtxt.text.toString()).addOnCompleteListener {
                    if (it.isSuccessful){
                        val user= Usuario(binding.correotxt.text.toString(),
                            binding.passwordtxt.text.toString(),
                            binding.nombretxt.text.toString(),
                            binding.apellidotxt.text.toString(),
                            binding.numeroID.text.toString())
                        usuario=user
                        databaseRealtimeService.saveUser(user,autenticationService.auth.currentUser)
                        val intent= Intent(this, MapActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        startActivity(intent)
                        finish()
                        imageName =  "${binding.numeroID.text.toString()}.jpg"
                        cloudStorageService.uploadImageToFirebaseStorage(
                            imageUri,
                            imageName,
                            { imageUrl ->
                                Log.i("NATA", "LA IMAGEN ES: $imageUrl")
                                val user= Usuario(binding.correotxt.text.toString(),
                                    binding.passwordtxt.text.toString(),
                                    binding.nombretxt.text.toString(),
                                    binding.apellidotxt.text.toString(),
                                    binding.numeroID.text.toString(),
                                    imageUrl.toString(),
                                    autenticationService.auth.uid!!
                                )
                                databaseRealtimeService.saveUser(user,autenticationService.auth.currentUser)
                                usuario=user
                                val intent= Intent(this, MapActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                startActivity(intent)
                                finish()
                            },
                            { exception ->
                                Toast.makeText(
                                    this,
                                    "No se ha podido guardar el perfil",
                                    Toast.LENGTH_SHORT,).show()
                            }
                        )
                    }else{
                        Log.w(ContentValues.TAG, "createUserWithEmail:failure", it.exception)
                        Toast.makeText(
                            this,
                            "Authentication failed.",
                            Toast.LENGTH_SHORT,
                        ).show()
                        binding.nombretxt.text.clear()
                        binding.apellidotxt.text.clear()
                        binding.correotxt.text.clear()
                        binding.passwordtxt.text.clear()
                        binding.numeroID.text.clear()
                    }
                }
            }
        }
    }
    private fun validateData():Boolean{
        if(binding.nombretxt.text.isEmpty()){
            binding.nombretxt.error="Se necesita el nombre!"
        }else if(binding.apellidotxt.text.isEmpty()){
            binding.apellidotxt.error="Se necesita el apellido!"
        }else if(binding.passwordtxt.text.isEmpty()){
            binding.passwordtxt.error="Se necesita la password!"
        }else if(binding.correotxt.text.isEmpty()){
            binding.correotxt.error="Se necesita el correo!"
        }else if(!imagenSubida){
            binding.SubirImagen.error="Se necesita una imagen!"}
        else if(!autenticationService.validEmailAdress(binding.correotxt.text.toString())){
            binding.correotxt.error="correo no esta bien escrito"
        }else if(binding.passwordtxt.text.toString().length<6){
            binding.correotxt.error="password con menos de 6 digitos!"
        }else{
            return true
        }
        return false
    }

    override fun onResume() {
        super.onResume()
        if (intent.hasExtra("image_uri")) {
            imageUri = Uri.parse(intent.getStringExtra("image_uri"))
            imagenSubida = true
        }

        if (intent.hasExtra("correo")) {
            val correo = intent.getStringExtra("correo")
            binding.correotxt.setText(correo)
        }

        if (intent.hasExtra("password")) {
            val password = intent.getStringExtra("password")
            binding.passwordtxt.setText(password)
        }

        if (intent.hasExtra("nombre")) {
            val nombre = intent.getStringExtra("nombre")
            binding.nombretxt.setText(nombre)
        }

        if (intent.hasExtra("apellido")) {
            val apellido = intent.getStringExtra("apellido")
            binding.apellidotxt.setText(apellido)
        }

        if (intent.hasExtra("numeroID")) {
            val numeroID = intent.getStringExtra("numeroID")
            binding.numeroID.setText(numeroID)
        }
    }

}