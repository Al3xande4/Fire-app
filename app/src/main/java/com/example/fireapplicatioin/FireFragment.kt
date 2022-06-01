package com.example.fireapplicatioin

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
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
import kotlinx.coroutines.runBlocking
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

class FireFragment : Fragment(), OnMapReadyCallback {
    private lateinit var map: GoogleMap
    private val REQUEST_LOCATION_PERMISSION = 1

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private var currentLoc: Location = Location("FireLocation")

    private lateinit var firePoints: ArrayList<Fire>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = DataBindingUtil.inflate<FragmentFireBinding>(inflater, R.layout.fragment_fire, container, false)
        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context!!)

        val datasource = Datasource()


        getCurrentLocation()

        getFires()

        binding.floating?.setOnClickListener{
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(
                LatLng(firePoints.first().lat,
                firePoints.first().lng), 15f
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

    private fun getFires() = runBlocking {
        try {
            firePoints = java.util.ArrayList(Api.retrofitService.getProperties())
        }catch (e: Exception){
            Log.e("FireFragment", "$e")
        }
    }

    private fun getCurrentLocation(){
        if(isPermissionGranted()){
            fusedLocationProviderClient.lastLocation.addOnCompleteListener { it ->
                if(it.result == null){
                    return@addOnCompleteListener
                }
                currentLoc = it.result
                firePoints.sortBy {
                    val firePointLoc = Location("LocationFire")
                    firePointLoc.latitude = it.lat
                    firePointLoc.longitude = it.lng
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
                firePoint.lat,
                firePoint.lng
            )
            map.addMarker(MarkerOptions()
                .position(
                    LatLng(
                    firePoint.lat, firePoint.lng)
                )
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                .snippet(snippet)
                .title(getString(R.string.fire)))
            map.addGroundOverlay(
                GroundOverlayOptions()
                    .image(BitmapDescriptorFactory.fromResource(R.drawable.img))
                    .position(LatLng(
                        firePoint.lat, firePoint.lng), 1000f))
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

}