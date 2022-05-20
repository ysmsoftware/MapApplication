package com.application.mapapplication.ui

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.application.mapapplication.UserManager
import com.application.mapapplication.adapter.RoadNameListAdapter
import com.application.mapapplication.databinding.ActivitySavedMapDataBinding
import com.application.mapapplication.viewmodel.MapViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class SavedMapDataActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySavedMapDataBinding
    private val viewModel: MapViewModel by viewModels()
    private lateinit var userManager: UserManager
    private lateinit var roadNameListAdapter: RoadNameListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySavedMapDataBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userManager = UserManager(applicationContext)
        userManager.userIdFlow.asLiveData().observe(this@SavedMapDataActivity) { id ->
            id?.let {
                setUpRv(id)
            }
        }
        binding.backSavedData.setOnClickListener {
            onBackPressed()
            finish()
        }

    }

    private fun setUpRv(userId: Int) {
        lifecycleScope.launch {
            viewModel.findByUserId(userId).let { response ->
                try {
                    if (response.isSuccessful) {
                        roadNameListAdapter = RoadNameListAdapter()

                        binding.roadListRv.apply {
                            adapter = roadNameListAdapter
                            layoutManager = LinearLayoutManager(this@SavedMapDataActivity)
                            setHasFixedSize(true)
                        }
                        if(response.body()!!.sendRoadData.isEmpty()){
                            binding.roadListRv.visibility = View.GONE
                            binding.ntgRoad.visibility = View.VISIBLE
                        }else{
                            binding.roadListRv.visibility = View.VISIBLE
                            binding.ntgRoad.visibility = View.GONE
                            response.body()!!.sendRoadData.let { roadNameListAdapter.submitList(it) }
                        }
                        roadNameListAdapter.setOnDeleteClickListener {
                            AlertDialog.Builder(this@SavedMapDataActivity)
                                .setTitle("Delete entry")
                                .setMessage("Are you sure you want to delete this entry?") // Specifying a listener allows you to take an action before dismissing the dialog.
                                // The dialog is automatically dismissed when a dialog button is clicked.
                                .setPositiveButton(android.R.string.yes,
                                    DialogInterface.OnClickListener { dialog, which ->
                                        lifecycleScope.launch{
                                            viewModel.deleteRoadDataById(it.roadId).let{
                                                try{
                                                    if(response.isSuccessful){
                                                        Toast.makeText(this@SavedMapDataActivity, "Deleted", Toast.LENGTH_SHORT).show()
                                                        startActivity(intent)
                                                        finish()
                                                    }else{
                                                        Toast.makeText(this@SavedMapDataActivity, "Error", Toast.LENGTH_SHORT).show()
                                                    }
                                                }catch (e: Exception){
                                                    Toast.makeText(this@SavedMapDataActivity, "Error", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                        }
                                    }) // A null listener allows the button to dismiss the dialog and take no further action.
                                .setNegativeButton(android.R.string.no, null)
                                .setCancelable(false)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show()

                        }

                        roadNameListAdapter.setOnSelectClickListener {
                            val intent =
                                Intent(this@SavedMapDataActivity, SavedMapDetailsActivity::class.java)
                            intent.putExtra("roadId", it.roadId)
                            startActivity(intent)
                            finish()
                        }
                    } else {
                        Toast.makeText(this@SavedMapDataActivity, "Error", Toast.LENGTH_SHORT)
                            .show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@SavedMapDataActivity, "Error", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}