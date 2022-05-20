package com.application.mapapplication.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.ActivityCompat
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import com.application.mapapplication.R
import com.application.mapapplication.UserManager
import com.application.mapapplication.databinding.ActivityMapsBinding
import com.application.mapapplication.viewmodel.MapViewModel
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.properties.Delegates


@AndroidEntryPoint
class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    private val viewModel: MapViewModel by viewModels()
    private lateinit var userManager: UserManager
    private val pERMISSION_ID = 42
    var handler: Handler = Handler()
    var runnable: Runnable? = null
    var delay = 15000
    val locationArray: ArrayList<LatLng> = ArrayList()
    val locStringArray: ArrayList<String> = ArrayList()
    val roadCodeArray: ArrayList<Int> = ArrayList()
    val roadSubCodeArray: ArrayList<Int> = ArrayList()
    lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var binding: ActivityMapsBinding
    private lateinit var mMap: GoogleMap
    private lateinit var polyline: Polyline
    lateinit var map_spinner2: Spinner
    private lateinit var mapAdapter2: ArrayAdapter<String>
    lateinit var map_spinner: Spinner
    private lateinit var mapAdapter: ArrayAdapter<String>
    var spinnerTxt by Delegates.notNull<Int>()
    var subSpinnerTxt by Delegates.notNull<Int>()
    var currentMarker: Marker? = null
    var color = Color.BLACK
    var location: Location? = null
    var currentLocation: LatLng = LatLng(20.5, 78.9)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        map_spinner = findViewById(R.id.spinner)
        map_spinner2 = findViewById(R.id.spinner_second)
        userManager = UserManager(applicationContext)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        binding.startLoc.setOnClickListener {
            binding.startLoc.visibility = View.GONE
            binding.getPointsLoc.visibility = View.VISIBLE
            getStartLocation()
        }
        binding.getPointsLoc.setOnClickListener {
            getLastLocation()
        }
        binding.btnCurrLoc.setOnClickListener {
            getCurrentLoc()
        }
        binding.btnLogout.setOnClickListener {
            val popup = PopupMenu(
                this@MapsActivity,
                binding.btnLogout
            )
            //inflating menu from xml resource
            popup.inflate(R.menu.map_menu)
            //adding click listener
            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_listView -> {
                        val intent = Intent(this@MapsActivity, SavedMapDataActivity::class.java)
                        startActivity(intent)
                        true
                    }
                    R.id.action_logout -> {
                        lifecycleScope.launch {
                            userManager.storeUser(0,0)
                            delay(500)
                            val intent = Intent(this@MapsActivity, LoginActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                        true
                    }
                    else -> false
                }
            }
            //displaying the popup
            popup.show()
        }
        binding.endLoc.setOnClickListener {
            binding.startLoc.visibility = View.VISIBLE
            binding.getPointsLoc.visibility = View.GONE
            val intent = Intent(this@MapsActivity,SaveLocationsActivity::class.java)
            intent.putExtra("Key",locStringArray)
            intent.putExtra("KeyCodes",roadCodeArray)
            intent.putExtra("KeySubCodes",roadSubCodeArray)
            startActivity(intent)
            locationArray.clear()
            locStringArray.clear()
            roadCodeArray.clear()
            roadSubCodeArray.clear()
            mMap.clear()
        }
        lifecycleScope.launch {
            viewModel.getRoadTypeList().let{response ->
                try{
                    if(response.isSuccessful){
                        val roadArray: ArrayList<String> = ArrayList()
                        val roadId: ArrayList<Int> = ArrayList()
                        val roadColor: ArrayList<Int> = ArrayList()
                        for(i in response.body()!!.roadType.indices){
                            roadArray.add(response.body()!!.roadType[i].roadType)
                            roadId.add(response.body()!!.roadType[i].roadId)
                            roadColor.add(response.body()!!.roadType[i].roadColor)
                        }

                        mapAdapter = ArrayAdapter(
                            this@MapsActivity,
                            com.google.android.material.R.layout.support_simple_spinner_dropdown_item, roadArray
                        )

                        map_spinner.adapter = mapAdapter
                        map_spinner.onItemSelectedListener = object :
                            AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(
                                parent: AdapterView<*>,
                                view: View, position: Int, id: Long
                            ) {
                                spinnerTxt = roadId[position]
                                color = roadColor[position]
                            }

                            override fun onNothingSelected(parent: AdapterView<*>) {
                                // write code to perform some action
                            }
                        }
                    }else{
                        Toast.makeText(this@MapsActivity, "Error", Toast.LENGTH_SHORT).show()
                    }
                }catch (e: Exception){
                    Toast.makeText(this@MapsActivity, "Error", Toast.LENGTH_SHORT).show()
                }
            }
        }
        lifecycleScope.launch {
            viewModel.getRoadSubTypeList().let{response ->
                try{
                    if(response.isSuccessful){
                        val roadArray: ArrayList<String> = ArrayList()
                        val roadId: ArrayList<Int> = ArrayList()
                        for(i in response.body()!!.roadSubType.indices){
                            roadArray.add(response.body()!!.roadSubType[i].roadSubType)
                            roadId.add(response.body()!!.roadSubType[i].roadId)
                        }

                        mapAdapter2 = ArrayAdapter(
                            this@MapsActivity,
                            com.google.android.material.R.layout.support_simple_spinner_dropdown_item, roadArray
                        )

                        map_spinner2.adapter = mapAdapter2
                        map_spinner2.onItemSelectedListener = object :
                            AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(
                                parent: AdapterView<*>,
                                view: View, position: Int, id: Long
                            ) {
                                subSpinnerTxt = roadId[position]
                            }

                            override fun onNothingSelected(parent: AdapterView<*>) {
                                // write code to perform some action
                            }
                        }
                    }else{
                        Toast.makeText(this@MapsActivity, "Error", Toast.LENGTH_SHORT).show()
                    }
                }catch (e: Exception){
                    Toast.makeText(this@MapsActivity, "Error", Toast.LENGTH_SHORT).show()
                }
            }
        }


    }




    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        if(isLocationEnabled()){
            getCurrentLoc()
        }else{
            val builder = AlertDialog.Builder(this)
            val layoutDialog: View =
                LayoutInflater.from(this).inflate(R.layout.location_dialog, null)
            builder.setView(layoutDialog)
            val btnUpdate: AppCompatButton = layoutDialog.findViewById(R.id.dialogButtonLocOpen)
            val btnSkip: AppCompatButton = layoutDialog.findViewById(R.id.dialogButtonCloseApp)
            val dialog = builder.create()
            dialog.show()
            dialog.setCancelable(false)
            dialog.window!!.setGravity(Gravity.CENTER)
            btnUpdate.setOnClickListener{
                getCurrentLoc()
                dialog.dismiss()
            }
            btnSkip.setOnClickListener {
                dialog.dismiss()
                finish()
            }
        }

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
                        drawMarker(currentLocation)
                    }
                }
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivityForResult(intent,101)
            }
        } else {
            requestPermissions()
        }
    }
    private fun drawMarker(latLng: LatLng){
        val markerOption = MarkerOptions().position(latLng).title("I am here")
            .draggable(true)
        currentMarker = mMap.addMarker(markerOption)
        currentMarker?.isInfoWindowShown
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16F))
    }
    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
                mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    location = task.result
                    if (location == null) {
                        requestNewLocationData()
                    } else {
                        currentLocation = LatLng(location!!.latitude, location!!.longitude)
                        val stringLoc = location!!.latitude.toString()+","+location!!.longitude.toString()
                        locationArray.add(currentLocation)
                        locStringArray.add(stringLoc)
                        roadCodeArray.add(spinnerTxt)
                        roadSubCodeArray.add(subSpinnerTxt)
                            polyline = mMap.addPolyline(
                                PolylineOptions()
                                    .add(locationArray[locationArray.size -1],locationArray[locationArray.size -2]).color(color))

                        drawMarker(currentLocation)
                    }
                }
    }
    @SuppressLint("MissingPermission")
    private fun getStartLocation() {
        mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
            location = task.result
            if (location == null) {
                requestNewLocationData()
            } else {
                mMap.clear()
                currentLocation = LatLng(location!!.latitude, location!!.longitude)
                val stringLoc = location!!.latitude.toString()+","+location!!.longitude.toString()
                locationArray.add(currentLocation)
                locStringArray.add(stringLoc)
                roadCodeArray.add(spinnerTxt)
                roadSubCodeArray.add(subSpinnerTxt)
                drawMarker(currentLocation)
            }
        }
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
        val locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    // Check if location permissions are
    // granted to the application
    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    // Request permissions if not granted before
    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
            pERMISSION_ID
        )
    }

    // What must happen when permission is granted
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
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

    override fun onResume() {
        handler.postDelayed(Runnable {
            handler.postDelayed(runnable!!, delay.toLong())
            requestNewLocationData()
        }.also { runnable = it }, delay.toLong())

        super.onResume()
    }
    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(runnable!!)
    }

}