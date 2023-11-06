package com.javeriana.taller3

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.javeriana.taller3.controller.MundoController
import com.javeriana.taller3.controller.MundoController.Companion.autenticationService
import com.javeriana.taller3.controller.MundoController.Companion.databaseRealtimeService
import com.javeriana.taller3.controller.MundoController.Companion.getInstancia
import com.javeriana.taller3.controller.MundoController.Companion.usuario
import com.javeriana.taller3.databinding.ActivityRegisterBinding
import com.javeriana.taller3.model.Usuario

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var mundoController:MundoController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityRegisterBinding.inflate(layoutInflater)
        mundoController= getInstancia()
        setContentView(binding.root)
        binding.SubirImagen.setOnClickListener {
            startActivity(Intent(this,ProfilePicActivity::class.java))
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
        }else if(!autenticationService.validEmailAdress(binding.correotxt.text.toString())){
            binding.correotxt.error="correo no esta bien escrito"
        }else if(binding.passwordtxt.text.toString().length<6){
            binding.correotxt.error="password con menos de 6 digitos!"
        }else{
            return true
        }
        return false
    }


}