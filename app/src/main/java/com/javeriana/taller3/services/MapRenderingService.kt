package com.javeriana.taller3.services

import android.content.Context
import android.location.Geocoder
import android.os.StrictMode
import androidx.core.content.res.ResourcesCompat
import com.google.android.gms.maps.model.LatLng
import com.javeriana.taller3.R
import com.javeriana.taller3.model.MyLocation
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import java.util.Date

class MapRenderingService (private val context: Context, private val map : MapView) {

    private var currentPositionMarker: Marker?=null
    private var markers: MutableList<Marker> = mutableListOf()
    var currentLocation: MyLocation = MyLocation(Date(System.currentTimeMillis()))
    private var geocoder: Geocoder

    init {
        Configuration.getInstance().load(context, androidx.preference.PreferenceManager.getDefaultSharedPreferences(context))
        map.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE)
        map.setMultiTouchControls(true)
        val bogota= GeoPoint(4.62,-74.07)
        map.controller.setZoom(18.0)
        map.controller.animateTo(bogota)
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        geocoder= Geocoder(context)
    }

    fun addMarker(geo: GeoPoint, title: String?=null, typeMarker: Char){
        val marker= Marker(map)
        when (typeMarker) {
            'A' -> {
                marker.title="Ubicacion Actual"
                val icon= ResourcesCompat.getDrawable(context.resources, R.drawable.baseline_location_on_24_green, context.theme)
                marker.icon=icon
                if(currentPositionMarker!=null){
                    map.overlays.remove(currentPositionMarker)
                }
                currentPositionMarker=marker
            }
            'J' ->{
                marker.title= title
                val icon= ResourcesCompat.getDrawable(context.resources, R.drawable.baseline_location_on_24_black, context.theme)
                marker.icon=icon
                markers.add(marker)
            }
            'O' ->{
                marker.title="Ubicacion Otro Usuario"
                val icon= ResourcesCompat.getDrawable(context.resources, R.drawable.baseline_location_on_cyan, context.theme)
                marker.icon=icon
                markers.add(marker)
            }
        }
        marker.position=geo
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        map.overlays.add(marker)
    }
    fun center(geo: GeoPoint){
        map.controller.animateTo(geo)
        map.controller.setZoom(20.0)
    }
    private fun removeMarkers(){
        for (i in markers.indices.reversed()) {
            map.overlays.remove(markers[i])
            markers.removeAt(i)
        }
    }
}