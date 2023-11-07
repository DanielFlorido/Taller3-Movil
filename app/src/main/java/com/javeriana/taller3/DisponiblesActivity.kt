package com.javeriana.taller3

import android.content.Intent
import android.database.Cursor
import android.database.MatrixCursor
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.CursorAdapter
import com.javeriana.taller3.adapters.DisponiblesAdapter
import com.javeriana.taller3.controller.MundoController
import com.javeriana.taller3.controller.MundoController.Companion.autenticationService
import com.javeriana.taller3.controller.MundoController.Companion.databaseRealtimeService
import com.javeriana.taller3.databinding.ActivityDisponiblesBinding
import com.javeriana.taller3.services.DatabaseRealtimeService

class DisponiblesActivity : AppCompatActivity(){
    private lateinit var binding: ActivityDisponiblesBinding
    private var projection= arrayOf(String, String)
    private lateinit var adapter: DisponiblesAdapter
    private var mundoController:MundoController= MundoController.getInstancia()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityDisponiblesBinding.inflate(layoutInflater)
        adapter= DisponiblesAdapter(this,null,0)
        binding.listaDisponibles.adapter=adapter
        setContentView(binding.root)
        databaseRealtimeService.readDisponibles { updateUI() }
    }

    override fun onPause() {
        super.onPause()
        databaseRealtimeService.endSubscription()
    }

    override fun onResume() {
        super.onResume()
        databaseRealtimeService.readDisponibles { updateUI() }
    }
    fun updateUI(){
        val cursor=databaseRealtimeService.cursor()
        adapter.changeCursor(cursor)
    }
}