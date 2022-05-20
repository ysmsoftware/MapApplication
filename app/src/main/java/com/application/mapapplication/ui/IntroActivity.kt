package com.application.mapapplication.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.asLiveData
import com.application.mapapplication.UserManager
import com.application.mapapplication.databinding.ActivityIntroBinding
import com.application.mapapplication.viewmodel.MapViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class IntroActivity : AppCompatActivity() {
    private lateinit var binding: ActivityIntroBinding
    private val viewModel: MapViewModel by viewModels()
    private lateinit var userManager: UserManager

    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userManager = UserManager(applicationContext)
//        val valid_until = "31/05/2022"
//        val sdf = SimpleDateFormat("dd/MM/yyyy")
//        val strDate: Date = sdf.parse(valid_until)!!

        Handler().postDelayed({
//            if (System.currentTimeMillis() > strDate.time) {
//                val intent = Intent(this@IntroActivity, OutdatedActivity::class.java)
//                startActivity(intent)
//                finish()
//            }else{
                observeData()
         //   }
        },2000)
    }
    private fun observeData() {
        userManager.userLoginFlow.asLiveData().observe(this) { login ->
            login?.let {
                if (login == 0) {
                    val intent = Intent(this@IntroActivity, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    startActivity(Intent(this@IntroActivity,MapsActivity::class.java))
                    finish()
                }
            }
        }
    }
}