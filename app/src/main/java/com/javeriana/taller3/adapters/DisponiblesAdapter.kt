package com.javeriana.taller3.adapters

import android.content.Context
import android.database.Cursor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CursorAdapter
import android.widget.TextView
import com.javeriana.taller3.R

class DisponiblesAdapter (context: Context?, c:Cursor?, flags: Int): CursorAdapter(context,c,flags) {
    override fun newView(p0: Context?, p1: Cursor?, p2: ViewGroup?): View {
        return LayoutInflater.from(p0).inflate(R.layout.disponible_row,p2,false)
    }

    override fun bindView(view: View?, context: Context?, cursor: Cursor?) {
        val tvId= view!!.findViewById<TextView>(R.id.numDisponibletxt)
        val tvnombre= view.findViewById<TextView>(R.id.nombre_profile_txt)
        val id= cursor!!.getInt(0)
        val nombre = cursor.getString(1)
        tvId.text=id.toString()
        tvnombre.text=nombre
    }
}