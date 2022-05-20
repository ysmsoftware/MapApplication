package com.application.mapapplication.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import com.application.mapapplication.UserManager
import com.application.mapapplication.databinding.ActivitySaveLocationsBinding
import com.application.mapapplication.models.SendRoadData
import com.application.mapapplication.viewmodel.MapViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SaveLocationsActivity : AppCompatActivity() {
    lateinit var save : SendRoadData
    private lateinit var userManager: UserManager
    lateinit var arrayAdapter: ArrayAdapter<String>
    private lateinit var binding: ActivitySaveLocationsBinding
    private val viewModel: MapViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySaveLocationsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userManager = UserManager(applicationContext)
        val locationArray : ArrayList<String> = intent.getSerializableExtra("Key") as ArrayList<String>
        val roadCodeArray : ArrayList<Int> = intent.getSerializableExtra("KeyCodes") as ArrayList<Int>
        val roadSubCodeArray : ArrayList<Int> = intent.getSerializableExtra("KeySubCodes") as ArrayList<Int>
        val emptyArray: ArrayList<String> = ArrayList()

        arrayAdapter = ArrayAdapter(this,
            android.R.layout.simple_list_item_1, locationArray)
        binding.secondAct.adapter = arrayAdapter

        binding.saveBtn.setOnClickListener {
            if(binding.etTitleData.text.isNotEmpty()){
                lifecycleScope.launch {
                    userManager.userIdFlow.asLiveData().observe(this@SaveLocationsActivity) { id ->
                        id?.let {
                            save = SendRoadData(0,binding.etActualRoadLength.text.toString(),binding.etActualRoadWidth.text.toString(),binding.etCustomiseCode.text.toString(),locationArray,binding.etPincode.text.toString()
                                ,binding.etRoadLength.text.toString(),binding.etTitleData.text.toString(),roadCodeArray,binding.etRoadWidth.text.toString(),id,roadSubCodeArray,emptyArray,emptyArray)
                            saveLocation(save)
                        }
                    }
                }
            }else{
                Toast.makeText(this@SaveLocationsActivity,"Fill all the details to save",Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveLocation(save: SendRoadData) {
        lifecycleScope.launch {
            viewModel.saveRoadData(save).let { response ->
                try {
                    if(response.isSuccessful){
                        Toast.makeText(this@SaveLocationsActivity,response.body()!!.message,Toast.LENGTH_SHORT).show()
                        finish()
                    }else{
                        Toast.makeText(this@SaveLocationsActivity,"Error",Toast.LENGTH_SHORT).show()
                    }
                }catch (e:Exception){
                    Toast.makeText(this@SaveLocationsActivity,"Error",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}