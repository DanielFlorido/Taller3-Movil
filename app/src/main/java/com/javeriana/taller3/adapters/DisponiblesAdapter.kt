package com.javeriana.taller3.adapters

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CursorAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.javeriana.taller3.R
import com.javeriana.taller3.controller.MundoController.Companion.databaseRealtimeService
import com.javeriana.taller3.controller.MundoController.Companion.usuarioSeguido
import com.javeriana.taller3.model.Usuario

class DisponiblesAdapter (context: Context?, c:Cursor?, flags: Int): CursorAdapter(context,c,flags) {
    override fun newView(p0: Context?, p1: Cursor?, p2: ViewGroup?): View {
        return LayoutInflater.from(p0).inflate(R.layout.disponible_row,p2,false)
    }

    override fun bindView(view: View?, context: Context?, cursor: Cursor?) {
        val tvId= view!!.findViewById<TextView>(R.id.numDisponibletxt)
        val tvnombre= view.findViewById<TextView>(R.id.nombre_profile_txt)
        val id= cursor!!.getInt(0)
        val nombre = cursor.getString(1)
        val url= cursor.getString(2)
        val key= cursor.getString(3)
        val image= view!!.findViewById<ImageView>(R.id.profilePic)
        val uri = Uri.parse(url)
        Glide.with(view)
            .load(uri)
            .into(image)
        tvId.text=id.toString()
        tvnombre.text=nombre
        val button=view!!.findViewById<Button>(R.id.followbtn)
        button.setOnClickListener {
            Log.i("Daniel", "le diste a: $key")
            databaseRealtimeService.getUser(key){
                if(it.isSuccessful){
                    val user= it.result.getValue(Usuario::class.java)
                    if(user!=null){
                       usuarioSeguido=user
                        Toast.makeText(context, "Estas siguiendo a ${usuarioSeguido.nombre}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}