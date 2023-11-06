package com.javeriana.taller3

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.CompoundButton
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.javeriana.taller3.controller.MundoController
import com.javeriana.taller3.controller.MundoController.Companion.autenticationService
import com.javeriana.taller3.controller.MundoController.Companion.databaseRealtimeService
import com.javeriana.taller3.controller.MundoController.Companion.usuario
import com.javeriana.taller3.databinding.ActivityMapBinding
import com.javeriana.taller3.services.DatabaseRealtimeService

class MapActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMapBinding
    private var mundoController:MundoController= MundoController.getInstancia()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu,menu)
        val itemSwitchCompat: MenuItem? = menu!!.findItem(R.id.disponibleswitch)
        itemSwitchCompat!!.setActionView(R.layout.switch_item)
        val sw= menu.findItem(R.id.disponibleswitch).actionView!!.findViewById<Switch>(R.id.switch2)
        sw.setOnCheckedChangeListener { _, p1 ->
            if (p1) {
                Toast.makeText(baseContext, "Ahora estas disponible!", Toast.LENGTH_SHORT).show()
                databaseRealtimeService.saveDisponible(usuario, autenticationService.auth.currentUser)
            }else{
                Toast.makeText(baseContext,"Ya no estas disponible",Toast.LENGTH_SHORT).show()
                databaseRealtimeService.deleteDisponible(autenticationService.auth.currentUser)
            }
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.logOutBtn->{
                Firebase.auth.signOut()
                val intent=Intent(this,LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
                finish()
            }R.id.usuariosDisponiblesbtn->{
                startActivity(Intent(baseContext, DisponiblesActivity::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }
}