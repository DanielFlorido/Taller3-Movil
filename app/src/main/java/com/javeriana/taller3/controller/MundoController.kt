package com.javeriana.taller3.controller

import com.javeriana.taller3.model.Usuario
import com.javeriana.taller3.services.AutenticationService
import com.javeriana.taller3.services.CloudStorageService
import com.javeriana.taller3.services.DatabaseRealtimeService

class MundoController private constructor(){


    companion object{
        @Volatile
        private var instacia: MundoController? = null
        var databaseRealtimeService:DatabaseRealtimeService= DatabaseRealtimeService()
        var cloudStorageService:CloudStorageService= CloudStorageService()
        var autenticationService:AutenticationService= AutenticationService()
        var usuario: Usuario=Usuario()
        fun getInstancia():MundoController{
            if(instacia==null){
                synchronized(this){
                    if(instacia==null){
                        instacia= MundoController()
                    }
                }
            }
            return instacia!!
        }
    }

}