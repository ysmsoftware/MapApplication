package com.application.mapapplication.ui

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.application.mapapplication.adapter.LatlngListAdapter
import com.application.mapapplication.databinding.ActivitySavedMapDetailsBinding
import com.application.mapapplication.viewmodel.MapViewModel
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SavedMapDetailsActivity : AppCompatActivity() {
    private lateinit var binding : ActivitySavedMapDetailsBinding
    private val viewModel: MapViewModel by viewModels()
    private lateinit var latlngListAdapter: LatlngListAdapter
    val locationArray: ArrayList<String> = ArrayList()
    val roadTypeArray: ArrayList<String> = ArrayList()
    val roadSubTypeArray: ArrayList<String> = ArrayList()
    val roadTypeIdArray: ArrayList<Int> = ArrayList()
    lateinit var arrayAdapter: ArrayAdapter<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySavedMapDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val roadId = intent.getIntExtra("roadId",0)
        binding.backSavedData.setOnClickListener {
            onBackPressed()
            finish()
        }
        binding.mapBtn.setOnClickListener {
            val intent = Intent(this@SavedMapDetailsActivity,ShowMapsActivity::class.java)
            intent.putExtra("array",locationArray)
            intent.putExtra("roadType",roadTypeArray)
            intent.putExtra("roadSubType",roadSubTypeArray)
            intent.putExtra("typeId",roadTypeIdArray)
            startActivity(intent)
        }
        setupData(roadId)
    }

    private fun setupData(roadId: Int) {
        lifecycleScope.launch{
            viewModel.findByRoadId(roadId).let{response ->
                try {
                    if (response.isSuccessful) {
                        response.body()!!.sendRoadData.apply {
                            binding.tvTitleData.text = roadName
                            binding.tvRoadLength.text = roadLength
                            binding.tvActualRoadLength.text =actualRoadLength
                            binding.tvActualRoadWidth.text = actualRoadWidth
                            binding.tvRoadWidth.text = roadWidth
                            binding.tvCustomiseCode.text = customiseCode
                            binding.tvPincode.text = pincode

                            latlngListAdapter = LatlngListAdapter(response.body()!!.sendRoadData.roadType,response.body()!!.sendRoadData.roadSubType)

                            binding.roadDataLl.apply {
                                adapter = latlngListAdapter
                                layoutManager = LinearLayoutManager(this@SavedMapDetailsActivity)
                                setHasFixedSize(true)
                            }
                            response.body()!!.sendRoadData.latitudeLongitude.let { latlngListAdapter.submitList(it) }
                            for(i in response.body()!!.sendRoadData.latitudeLongitude.indices){
                                locationArray.add(response.body()!!.sendRoadData.latitudeLongitude[i])
                                roadTypeArray.add(response.body()!!.sendRoadData.roadType[i])
                                roadSubTypeArray.add(response.body()!!.sendRoadData.roadSubType[i])
                                roadTypeIdArray.add(response.body()!!.sendRoadData.roadTypeId[i])
                            }
                        }

                    } else {
                        Toast.makeText(this@SavedMapDetailsActivity, "Error", Toast.LENGTH_SHORT)
                            .show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@SavedMapDetailsActivity, "Error", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}