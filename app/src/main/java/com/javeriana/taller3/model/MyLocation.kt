package com.javeriana.taller3.model

import org.json.JSONObject
import org.osmdroid.util.GeoPoint
import java.util.Date

class MyLocation() {
    lateinit var fecha: Date
    lateinit var geoPoint: GeoPoint

    constructor(fecha: Date,geo: GeoPoint) : this() {
        this.fecha=fecha
        this.geoPoint=geo
    }
    constructor(fecha: Date) : this() {
        this.geoPoint= GeoPoint(4.62,-74.07)
        this.fecha=fecha
    }
    constructor(fecha: Date, lat: Double, long: Double): this(){
        this.fecha =  fecha
        this.geoPoint.latitude = lat
        this.geoPoint.longitude = long
    }

    fun toJson():String{
        val obj = JSONObject()
        obj.put("date", fecha)
        obj.put("latitude", geoPoint.latitude)
        obj.put("longitud", geoPoint.longitude)
        return obj.toString()
    }
}