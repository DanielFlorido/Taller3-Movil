package com.javeriana.taller3

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseUser
import com.javeriana.taller3.controller.MundoController
import com.javeriana.taller3.controller.MundoController.Companion.autenticationService
import com.javeriana.taller3.controller.MundoController.Companion.databaseRealtimeService
import com.javeriana.taller3.controller.MundoController.Companion.usuario
import com.javeriana.taller3.databinding.ActivityLoginBinding
import com.javeriana.taller3.model.Usuario
import com.javeriana.taller3.services.AutenticationService

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private var mundoController: MundoController= MundoController.getInstancia()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityLoginBinding.inflate(layoutInflater)
        autenticationService= AutenticationService()
        setContentView(binding.root)
        binding.LogInButton.setOnClickListener {
            if (validateData(binding.email.text.toString(),binding.password.text.toString())){
                autenticationService.signIn(binding.email.text.toString(),binding.password.text.toString()).addOnCompleteListener {
                    if(it.isSuccessful){
                        updateUI(autenticationService.auth.currentUser)
                    }else{
                        val message= it.exception!!.message
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                        Log.w("Login", "singin fail", it.exception)
                        binding.email.text.clear()
                        binding.password.text.clear()
                    }
                }
            }
        }
        binding.SignInButton.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun validateData(email:String, password:String):Boolean{
        if (email.isEmpty()){
            binding.email.error = "Requiere email!"
        }else if(!autenticationService.validEmailAdress(email)){
            Log.e("Login", email)
            binding.email.error= "email Invalido!"
        }else if(password.isEmpty()){
            binding.password.error= "Requiere contrasenia!"
        }else if(password.length<6){
            binding.password.error= "Contrasenia menor a 6"
        }else{
            return true
        }
        return false
    }
    private fun updateUI(currentUser: FirebaseUser?){
        if(currentUser!=null){
            databaseRealtimeService.getUser(currentUser) {
                if (it.isSuccessful) {
                    val user = it.result.getValue(Usuario::class.java)
                    if (user != null) {
                        usuario=user
                    }
                }
            }
            val intent=Intent(this, MapActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        updateUI(autenticationService.auth.currentUser)
    }
}