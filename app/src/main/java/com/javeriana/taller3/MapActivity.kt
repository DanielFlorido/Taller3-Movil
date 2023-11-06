package com.javeriana.taller3

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.CompoundButton
import android.widget.Switch
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SwitchCompat
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.javeriana.taller3.controller.MundoController
import com.javeriana.taller3.controller.MundoController.Companion.autenticationService
import com.javeriana.taller3.controller.MundoController.Companion.databaseRealtimeService
import com.javeriana.taller3.controller.MundoController.Companion.usuario
import com.javeriana.taller3.databinding.ActivityMapBinding
import com.javeriana.taller3.services.DatabaseRealtimeService
import com.javeriana.taller3.model.MyLocation
import com.javeriana.taller3.services.LocationService
import com.javeriana.taller3.services.MapRenderingService
import org.json.JSONException
import org.json.JSONObject
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import java.util.Date

class MapActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMapBinding
    private val getSimplePermission= registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }
    private lateinit var map : MapView
    private lateinit var locationService: LocationService
    private lateinit var mapRenderingService: MapRenderingService
    private var disponible=false
    private val bogota = GeoPoint(4.62, -74.07)

    private val locationSettings= registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            locationService.startLocationUpdates()
        } else {
            //Todo
        }
    }
    private var mundoController:MundoController= MundoController.getInstancia()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Configuration.getInstance().load(this, androidx.preference.PreferenceManager.getDefaultSharedPreferences(this))
        locationService = LocationService(this, this)
        map = binding.osmMap
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

        readEvents(this)
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

    fun onLocationUpdate(location: Location) {
        updateUI(location)
    }

    private fun locationSettings(){
        val builder= LocationSettingsRequest.Builder().addLocationRequest(locationService.locationRequest)
        val client: SettingsClient = LocationServices.getSettingsClient(this)
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener {
            locationService.startLocationUpdates()
        }

        task.addOnFailureListener{
            if(it is ResolvableApiException){
                try {
                    val isr: IntentSenderRequest = IntentSenderRequest.Builder(it.resolution).build()
                    locationSettings.launch(isr)
                }catch (sendEx: IntentSender.SendIntentException){
                    //eso!!
                }
            }
        }
    }

    private fun updateUI(location: Location){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                Toast.makeText(this, "Se necesita la ubicacion para poder usar el mapa!", Toast.LENGTH_LONG)
                    .show()
            }
            getSimplePermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            return
        } else {
            mapRenderingService.currentLocation.geoPoint= GeoPoint(location.latitude,location.longitude)
            mapRenderingService.addMarker(mapRenderingService.currentLocation.geoPoint, typeMarker = 'A')
            if(disponible){
                usuario.latitud=mapRenderingService.currentLocation.geoPoint.latitude
                Log.i("Daniel",usuario.latitud.toString()+" "+ mapRenderingService.currentLocation.geoPoint.latitude.toString())
                usuario.longitud=mapRenderingService.currentLocation.geoPoint.longitude
                Log.i("Daniel",usuario.longitud.toString()+" "+ mapRenderingService.currentLocation.geoPoint.longitude.toString())
            }
        }
    }
     private fun readEvents(context: Context){
        try {
            val inputStream = context.assets.open("locations.json")
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()

            val json = String(buffer, Charsets.UTF_8)

            val jsonObject = JSONObject(json)
            val eventosArray = jsonObject.optJSONArray("locationsArray")

            if(eventosArray!= null){
                for (i in 0 until eventosArray.length()){
                    val json = eventosArray.optJSONObject(i)
                    val latitude = json.optString("latitude") ?: "0.0"
                    val longitude = json.optString("longitude") ?: "0.0"
                    val nombreEvento = json.optString("name")

                    mapRenderingService.addMarker(GeoPoint(latitude.toDouble(), longitude.toDouble()), nombreEvento,typeMarker = 'J')
                }
            }

        }catch (e: JSONException){
            e.printStackTrace()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu,menu)
        val itemSwitchCompat: MenuItem? = menu!!.findItem(R.id.disponibleswitch)
        itemSwitchCompat!!.setActionView(R.layout.switch_item)
        val sw= menu.findItem(R.id.disponibleswitch).actionView!!.findViewById<Switch>(R.id.switch2)
        sw.setOnCheckedChangeListener { _, p1 ->
            if (p1) {
                Toast.makeText(baseContext, "Ahora estas disponible!", Toast.LENGTH_SHORT).show()
                disponible=true
                usuario.latitud=mapRenderingService.currentLocation.geoPoint.latitude
                usuario.longitud=mapRenderingService.currentLocation.geoPoint.longitude
                databaseRealtimeService.saveDisponible(usuario, autenticationService.auth.currentUser)
            }else{
                Toast.makeText(baseContext,"Ya no estas disponible",Toast.LENGTH_SHORT).show()
                disponible=false
                usuario.latitud=0.0
                usuario.longitud=0.0
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