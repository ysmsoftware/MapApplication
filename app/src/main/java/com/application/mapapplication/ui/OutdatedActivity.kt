package com.application.mapapplication.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.application.mapapplication.databinding.ActivityIntroBinding
import com.application.mapapplication.databinding.ActivityOutdatedBinding
import com.application.mapapplication.viewmodel.MapViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OutdatedActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOutdatedBinding
    private val viewModel: MapViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOutdatedBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}