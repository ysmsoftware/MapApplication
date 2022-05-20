package com.application.mapapplication.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.application.mapapplication.R
import com.application.mapapplication.adapter.YourCustomInfoWindowAdapter
import com.application.mapapplication.databinding.ActivityShowMapsBinding
import com.application.mapapplication.viewmodel.MapViewModel
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class ShowMapsActivity : AppCompatActivity(), OnMapReadyCallback {
    private val viewModel: MapViewModel by viewModels()
    private val pERMISSION_ID = 42
    lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var binding: ActivityShowMapsBinding
    private lateinit var mMap: GoogleMap
    private lateinit var polyline: Polyline
    var locationArray: ArrayList<String> = ArrayList()
    var roadTypeArray: ArrayList<String> = ArrayList()
    var roadSubTypeArray: ArrayList<String> = ArrayList()
    var roadTypeIdArray: ArrayList<Int> = ArrayList()
    var roadColor: ArrayList<Int> = ArrayList()
    var latlngArray: ArrayList<LatLng> = ArrayList()
    var currentMarker: Marker? = null
    var color = Color.BLACK
    var location: Location? = null
    var currentLocation: LatLng = LatLng(20.5, 78.9)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityShowMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationArray = intent.getSerializableExtra("array") as ArrayList<String>
        roadTypeArray = intent.getSerializableExtra("roadType") as ArrayList<String>
        roadSubTypeArray = intent.getSerializableExtra("roadSubType") as ArrayList<String>
        roadTypeIdArray = intent.getSerializableExtra("typeId") as ArrayList<Int>
        for (i in locationArray.indices) {
            val res = locationArray[i].split("[,]".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()
            val latlng = LatLng(res[0].toDouble(), res[1].toDouble())
            latlngArray.add(latlng)
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.show_map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        getCurrentLoc()
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLoc() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    location = task.result
                    if (location == null) {
                        requestNewLocationData()
                    } else {
                        currentLocation = LatLng(location!!.latitude, location!!.longitude)
                        lifecycleScope.launch {
                            viewModel.getRoadTypeList().let { response ->
                                try {
                                    if (response.isSuccessful) {
                                        for (i in response.body()!!.roadType.indices) {
                                            roadColor.add(response.body()!!.roadType[i].roadColor)
                                        }
                                        for(i in latlngArray.indices){
                                            val geoCoder = Geocoder(this@ShowMapsActivity)
                                            val currentLocation = geoCoder.getFromLocation(
                                                latlngArray[i].latitude,
                                                latlngArray[i].longitude,
                                                1
                                            )


                                            mMap.setInfoWindowAdapter(YourCustomInfoWindowAdapter(this@ShowMapsActivity))
                                            val snippet =
                                                """
                                                ${currentLocation.first().getAddressLine(0)}
                                                ${roadTypeArray[i]},   ${roadSubTypeArray[i]}
                                                """.trimIndent()
                                            val markerOption = MarkerOptions().position(latlngArray[i]).snippet(snippet)
                                            currentMarker = mMap.addMarker(markerOption)
                                        }


                                        for (i in 1 until latlngArray.size) {

                                            if (latlngArray.size - i > 0) {
                                                when {
                                                    roadTypeIdArray[roadTypeIdArray.size - i] == 1 -> {
                                                        color = roadColor[0]
                                                    }
                                                    roadTypeIdArray[roadTypeIdArray.size - i] == 2 -> {
                                                        color = roadColor[1]
                                                    }
                                                    roadTypeIdArray[roadTypeIdArray.size - i] == 3 -> {
                                                        color = roadColor[2]
                                                    }
                                                }
                                                polyline = mMap.addPolyline(
                                                    PolylineOptions()
                                                        .add(
                                                            latlngArray[latlngArray.size - i],
                                                            latlngArray[latlngArray.size - (i + 1)]
                                                        ).color(color)
                                                )
                                            }
                                        }


                                    } else {
                                        Toast.makeText(
                                            this@ShowMapsActivity,
                                            "Error",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                } catch (e: Exception) {
                                    Toast.makeText(
                                        this@ShowMapsActivity,
                                        "Error try" + e.message,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                        mMap.clear()

                        mMap.animateCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                latlngArray[latlngArray.size - 1],
                                17F
                            )
                        )
                    }
                }
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivityForResult(intent, 101)
            }
        } else {
            requestPermissions()
        }
    }

    private fun drawMarker(latLng: LatLng) {
        mMap.clear()
        val markerOption = MarkerOptions().position(latLng).title("I am here")
            .draggable(true)
        currentMarker = mMap.addMarker(markerOption)
        currentMarker?.isInfoWindowShown
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 5F))
    }

    // Get current location, if shifted
    // from previous location
    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        val mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mFusedLocationClient.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()!!
        )
    }

    // If current location could not be located, use last location
    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val mLastLocation: Location = locationResult.lastLocation
            currentLocation = LatLng(mLastLocation.latitude, mLastLocation.longitude)
        }
    }

    // function to check if GPS is on
    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    // Check if location permissions are
    // granted to the application
    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    // Request permissions if not granted before
    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            pERMISSION_ID
        )
    }

    // What must happen when permission is granted
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == pERMISSION_ID) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getCurrentLoc()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101) {
            getCurrentLoc()
        }
    }

}