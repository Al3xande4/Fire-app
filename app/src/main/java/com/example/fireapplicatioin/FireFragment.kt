package com.example.fireapplicatioin

import android.Manifest
import android.content.pm.PackageManager
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
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import java.util.*

class FireFragment : Fragment(), OnMapReadyCallback {
    private lateinit var map: GoogleMap
    private val REQUEST_LOCATION_PERMISSION = 1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<FragmentFireBinding>(inflater, R.layout.fragment_fire, container, false)
        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        return binding.root
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

}