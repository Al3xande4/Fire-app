package com.example.fireapplicatioin

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.example.fireapplicatioin.data.Datasource
import com.example.fireapplicatioin.databinding.FragmentFireBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.gms.tasks.Task
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Exception
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import kotlin.collections.HashMap

class FireFragment : Fragment(), OnMapReadyCallback {
    lateinit var map: GoogleMap
    private val REQUEST_LOCATION_PERMISSION = 1

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    var currentLat: Double = 0.0
    var currentLong: Double = 0.0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = DataBindingUtil.inflate<FragmentFireBinding>(inflater, R.layout.fragment_fire, container, false)
        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val placeTypeList = arrayListOf("hospital", "fire", "fire_department", "atm")
        val placeNameList = arrayListOf("Hospital", "Fire", "Fire Department", "ATM")

        binding.spType?.adapter = ArrayAdapter(context!!, android.R.layout.simple_spinner_dropdown_item
        , placeNameList)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context!!)

        binding.findButton?.setOnClickListener{
            val i = binding.spType?.selectedItemPosition
            val url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json" +
                    "?location=" + currentLat + "," + currentLong +
                    "&radius=5000" +
                    "&type=" + placeTypeList[i!!] +
                    "&sensor=true" +
                    "&key=" + getResources().getString(R.string.google_maps_key)
            PlaceTask(map).execute(url)
        }


        return binding.root
    }

    fun getCurrentLocation(map: GoogleMap){
        if(isPermissionGranted()){
            var task: Task<Location> = fusedLocationProviderClient.lastLocation
            task.addOnSuccessListener { location: Location ->
                currentLong = location.latitude
                currentLat = location.longitude
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    LatLng(currentLat, currentLong), 10f
                ))
            }
        }
    }

    private fun isPermissionGranted() : Boolean {
        return ContextCompat.checkSelfPermission(
            context!!,
            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray) {
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.contains(PackageManager.PERMISSION_GRANTED)) {
                enableMyLocation()
            }
        }
    }

    private fun enableMyLocation() {
        if (isPermissionGranted()) {
            map.isMyLocationEnabled = true
            map.animateCamera(CameraUpdateFactory.zoomTo(20f), 2000, null)
        }
        else {
            ActivityCompat.requestPermissions(
                activity!!,
                arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
        }
    }

    override fun onMapReady(p0: GoogleMap) {

        map = p0
        val miass = LatLng(55.064343, 60.105972)
        val zoomLevel = 15f
        map.addMarker(MarkerOptions().position(miass).title("Marker in Miass"))

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(miass, zoomLevel))



        val datasource = Datasource()
        setMapLongClick(map)
        setPoiClick(map)
        enableMyLocation()
        for(firePoint in datasource.loadFirePoints()){
            val snippet = String.format(
                Locale.getDefault(),
                "Lat: %1$.5f, Long: %2$.5f",
                firePoint.lanLng.latitude,
                firePoint.lanLng.longitude
            )
            map.addMarker(MarkerOptions()
                .position(firePoint.lanLng)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                .snippet(snippet)
                .title(getString(R.string.fire)))
            map.addGroundOverlay(
                GroundOverlayOptions()
                    .image(BitmapDescriptorFactory.fromResource(R.drawable.img))
                    .position(firePoint.lanLng, firePoint.size * 1 / map.cameraPosition.zoom))
        }
    }

    private fun setMapLongClick(map: GoogleMap) {
        map.setOnMapLongClickListener { latLng ->
            val snippet = String.format(
                Locale.getDefault(),
                "Lat: %1$.5f, Long: %2$.5f",
                latLng.latitude,
                latLng.longitude
            )
            map.addMarker(
                MarkerOptions()
                    .title(getString(R.string.dropped_pin))
                    .snippet(snippet)
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)))
        }
    }
    private fun setPoiClick(map: GoogleMap) {
        map.setOnPoiClickListener { poi ->
            val poiMarker = map.addMarker(
                MarkerOptions()
                    .position(poi.latLng)
                    .title(poi.name)
            )
            poiMarker?.showInfoWindow()
        }
    }

    private class PlaceTask(val map: GoogleMap): AsyncTask<String, Int, String>(){
        override fun doInBackground(vararg p0: String?): String {
            val data = downloadUrl(p0[0]!!)
            return data
        }

        override fun onPostExecute(result: String?) {
            ParserTask(map).execute(result)
        }
        private fun downloadUrl(string: String): String{
            val url = URL(string)
            val connection = url.openConnection() as HttpURLConnection

            connection.connect()

            val inputStream = connection.inputStream
            val reader = BufferedReader(InputStreamReader(inputStream))
            val builder = StringBuilder()
            var line = ""
            reader.lineSequence().forEach {
                builder.append(it)
            }
            val data = builder.toString()

            reader.close()
            return data
        }

        private class ParserTask(val map: GoogleMap): AsyncTask<String, Int, List<HashMap<String, String>>>() {
            override fun doInBackground(vararg p0: String?): List<HashMap<String, String>> {
                val jsonParser = JsonParser()
                var mapList: List<HashMap<String, String>>? = null
                val obj: JSONObject?
                try {
                    obj = JSONObject(p0[0]!!)
                    mapList = jsonParser.parseResult(obj)
                }catch (e: Exception){

                }
                return mapList!!
            }

            override fun onPostExecute(result: List<HashMap<String, String>>?) {
                map.clear()
                val size = result!!.size - 1
                for(i in 0..size){
                    val hashMapList = result[i]
                    val lat = hashMapList["lat"]!!.toDouble()
                    val lng = hashMapList["lng"]!!.toDouble()
                    val name = hashMapList["name"]
                    val latLng = LatLng(lat, lng)
                    val markerOptions = MarkerOptions()

                    markerOptions.position(latLng)
                    markerOptions.title(name)

                    map.addMarker(markerOptions)
                }
            }

        }

    }

}