package com.example.fireapplicatioin

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.example.fireapplicatioin.data.Datasource
import com.example.fireapplicatioin.databinding.FragmentFireBinding
import com.example.fireapplicatioin.model.FirePoint
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import java.util.*
import kotlin.collections.ArrayList

class FireFragment : Fragment(), OnMapReadyCallback {
    private lateinit var map: GoogleMap
    private val REQUEST_LOCATION_PERMISSION = 1

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private var currentLoc: Location = Location("FireLocation")

    private lateinit var firePoints: ArrayList<FirePoint>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = DataBindingUtil.inflate<FragmentFireBinding>(inflater, R.layout.fragment_fire, container, false)
        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

//        val placeTypeList = arrayListOf("hospital", "fire", "fire_department", "atm")
//        val placeNameList = arrayListOf("Hospital", "Fire", "Fire Department", "ATM")
//
//        binding.spType.adapter = ArrayAdapter(context!!, android.R.layout.simple_spinner_dropdown_item
//        , placeNameList)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context!!)

//        binding.findButton.setOnClickListener{
//            val i = binding.spType.selectedItemPosition
//            val url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json" +
//                    "?location=" + currentLat + "," + currentLong +
//                    "&radius=5000" +
//                    "&type=" + placeTypeList[i] +
//                    "&sensor=true" +
//                    "&key=" + getResources().getString(R.string.google_maps_key)
//            PlaceTask(map).execute(url)
//        }

        val datasource = Datasource()
        firePoints = datasource.loadFirePoints()

        getCurrentLocation()


        binding.floating?.setOnClickListener{
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(
                firePoints.first().latLng, 15f
            ))
        }

        binding.myLoc?.setOnClickListener{
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(
                LatLng(
                    currentLoc.latitude, currentLoc.longitude
                ), 15f
            ))
        }

        return binding.root
    }

    private fun getCurrentLocation(){
        if(isPermissionGranted()){
            fusedLocationProviderClient.lastLocation.addOnCompleteListener { it ->
                currentLoc = it.result
                firePoints.sortBy {
                    val firePointLoc = Location("LocationFire")
                    firePointLoc.latitude = it.latLng.latitude
                    firePointLoc.longitude = it.latLng.longitude
                    currentLoc.distanceTo(firePointLoc)
                }
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    LatLng(currentLoc.latitude, currentLoc.longitude),
                    15f
                ))
            }
        }
        else{
            ActivityCompat.requestPermissions(
                activity!!,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
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
                getCurrentLocation()
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
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
        }
    }

    override fun onMapReady(p0: GoogleMap) {

        map = p0

        enableMyLocation()

        setPoiClick(map)
        map.uiSettings.isMyLocationButtonEnabled = false

        drawFirePoints()
        drawVolunteers()
    }

    private fun drawFirePoints(){
        for(firePoint in firePoints){
            val snippet = String.format(
                Locale.getDefault(),
                "Lat: %1$.5f, Long: %2$.5f",
                firePoint.latLng.latitude,
                firePoint.latLng.longitude
            )
            map.addMarker(MarkerOptions()
                .position(firePoint.latLng)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                .snippet(snippet)
                .title(getString(R.string.fire)))
            map.addGroundOverlay(
                GroundOverlayOptions()
                    .image(BitmapDescriptorFactory.fromResource(R.drawable.img))
                    .position(firePoint.latLng, firePoint.size))
        }
    }

    private fun drawVolunteers(){
        val datasource = Datasource()
        for(volunteer in datasource.loadVolunteers()){
            val snippet = String.format(
                Locale.getDefault(),
                "Lat: %1$.5f, Long: %2$.5f",
                volunteer.latLng.latitude,
                volunteer.latLng.longitude
            )
            map.addMarker(MarkerOptions()
                .position(volunteer.latLng)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                .snippet(snippet)
                .title("Volunteer"))
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

//    private class PlaceTask(val map: GoogleMap): AsyncTask<String, Int, String>(){
//        override fun doInBackground(vararg p0: String?): String {
//            return downloadUrl(p0[0]!!)
//        }
//
//        override fun onPostExecute(result: String?) {
//            ParserTask(map).execute(result)
//        }
//        private fun downloadUrl(string: String): String{
//            val url = URL(string)
//            val connection = url.openConnection() as HttpURLConnection
//
//            connection.connect()
//
//
//            val inputStream = connection.inputStream
//            val reader = BufferedReader(InputStreamReader(inputStream))
//            val builder = StringBuilder()
//            var line = ""
//            reader.lineSequence().forEach {
//                builder.append(it)
//            }
//            val data = builder.toString()
//
//            Log.d("FireFragment", "${data} data")
//
//            reader.close()
//            return data
//        }
//
//        private class ParserTask(val map: GoogleMap): AsyncTask<String, Int, List<HashMap<String, String>>>() {
//            override fun doInBackground(vararg p0: String?): List<HashMap<String, String>> {
//                val jsonParser = JsonParser()
//                var mapList: List<HashMap<String, String>>? = null
//                val obj: JSONObject?
//                try {
//                    obj = JSONObject(p0[0]!!)
//                    Log.d("FireFragment", "${obj} obj")
//                    mapList = jsonParser.parseResult(obj)
//                }catch (e: Exception){
//
//                }
//                Log.d("FireFragment", "${mapList} map list")
//                return mapList!!
//            }
//
//            override fun onPostExecute(result: List<HashMap<String, String>>?) {
//                map.clear()
//                val size = result!!.size - 1
//                for(i in 0..size){
//                    val hashMapList = result[i]
//                    val lat = hashMapList["lat"]!!.toDouble()
//                    val lng = hashMapList["lng"]!!.toDouble()
//                    val name = hashMapList["name"]
//                    val latLng = LatLng(lat, lng)
//                    val markerOptions = MarkerOptions()
//
//                    markerOptions.position(latLng)
//                    markerOptions.title(name)
//
//                    map.addMarker(markerOptions)
//                }
//            }
//
//        }
//
//    }

}