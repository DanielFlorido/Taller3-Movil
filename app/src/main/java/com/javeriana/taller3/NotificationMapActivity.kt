package com.javeriana.taller3

import android.Manifest
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.tasks.Task
import com.javeriana.taller3.controller.MundoController
import com.javeriana.taller3.databinding.ActivityMapBinding
import com.javeriana.taller3.databinding.ActivityNotificationMapBinding
import com.javeriana.taller3.model.MyLocation
import com.javeriana.taller3.services.LocationService
import com.javeriana.taller3.services.MapRenderingService
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import java.util.Date

class NotificationMapActivity : AppCompatActivity(), LocationService.LocationUpdateListener {
    private lateinit var binding: ActivityNotificationMapBinding
    private val getSimplePermission= registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }
    private lateinit var map : MapView
    private lateinit var locationService: LocationService
    private lateinit var mapRenderingService: MapRenderingService
    private var disponible=false
    private val bogota = GeoPoint(4.62, -74.07)

    private val locationSettings = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            locationService.startLocationUpdates()
        } else {
            Toast.makeText(this, "La localizacion esta desactivada", Toast.LENGTH_LONG).show()
        }
    }
    private var mundoController: MundoController = MundoController.getInstancia()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityNotificationMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Configuration.getInstance().load(this, androidx.preference.PreferenceManager.getDefaultSharedPreferences(this))
        locationService = LocationService(this, this)
        map = binding.osmNotificationMap
        mapRenderingService= MapRenderingService(this,map)
        map.setTileSource(TileSourceFactory.MAPNIK)

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)){
                Toast.makeText(this, "La ubicación es necesaria para usar el mapa", Toast.LENGTH_LONG).show()
            }
            getSimplePermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        locationService.locationClient.lastLocation.addOnSuccessListener {
            if(it!=null){
                val geo= GeoPoint(it.latitude, it.longitude)
                mapRenderingService.addMarker(geo, typeMarker = 'A')
                mapRenderingService.currentLocation= MyLocation(Date(System.currentTimeMillis()),GeoPoint(it.latitude, it.longitude))
                mapRenderingService.center(geo)
                updateUI(it)
            }else{
                Log.i("UbiP", "Esta apagada la ubicacion")
                locationSettings()
                map.controller.animateTo(bogota)
            }
        }

        val intent = intent
        val extras = intent.extras

        if(extras != null){
            Log.i("Patnur Evitchium", "WHERE DO WE GO NOW")
            val latitud = extras.getDouble("Latitud")
            val longitud = extras.getDouble("Longitud")
            val nombre = extras.getString("Nombre")
            var point = GeoPoint(latitud, longitud)
            mapRenderingService.addMarker(point, nombre, typeMarker = 'O')
            calcularDistancia(latitud, longitud)
        }
    }

    override fun onResume() {
        super.onResume()
        map.onResume()
        map.controller.setZoom(18.0)
        locationService.startLocationUpdates()
    }
    override fun onPause() {
        super.onPause()
        map.onPause()
        locationService.stopLocationUpdates()
    }

    override fun onLocationUpdate(location: Location) {
        updateUI(location)
    }
    private fun locationSettings(){
        val builder= LocationSettingsRequest.Builder().addLocationRequest(locationService.locationRequest)
        val client: SettingsClient = LocationServices.getSettingsClient(this)
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener {
            locationService.startLocationUpdates()
        }

        task.addOnFailureListener{exception ->
            if(exception is ResolvableApiException){
                try {
                    val isr: IntentSenderRequest = IntentSenderRequest.Builder(exception.resolution).build()
                    locationSettings.launch(isr)
                }catch (sendEx: IntentSender.SendIntentException){
                    //eso!!
                }
            }else{
                Toast.makeText(this, "No hay hardware para el GPS", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun updateUI(location: Location){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                Toast.makeText(this, "Se necesita la ubicacion para poder usar el mapa!", Toast.LENGTH_LONG).show()
            }
            getSimplePermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            return
        } else {
            mapRenderingService.currentLocation.geoPoint= GeoPoint(location.latitude,location.longitude)
            mapRenderingService.addMarker(mapRenderingService.currentLocation.geoPoint, typeMarker = 'A')
            if(disponible){
                MundoController.usuario.latitud=mapRenderingService.currentLocation.geoPoint.latitude
                Log.i("Daniel2", MundoController.usuario.latitud.toString()+" "+ mapRenderingService.currentLocation.geoPoint.latitude.toString())
                MundoController.usuario.longitud=mapRenderingService.currentLocation.geoPoint.longitude
                Log.i("Daniel2", MundoController.usuario.longitud.toString()+" "+ mapRenderingService.currentLocation.geoPoint.longitude.toString())
            }
        }
    }

    private fun calcularDistancia(latitud: Double, longitud: Double){
        val distancia = mapRenderingService.currentLocation.distance(GeoPoint(latitud, longitud))
        binding.distancia.text = "$distancia m"
    }
}