package com.application.mapapplication.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.application.mapapplication.R
import com.application.mapapplication.UserManager
import com.application.mapapplication.databinding.ActivityIntroBinding
import com.application.mapapplication.databinding.ActivityLoginBinding
import com.application.mapapplication.models.LoginData
import com.application.mapapplication.viewmodel.MapViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val viewModel: MapViewModel by viewModels()
    private lateinit var userManager: UserManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userManager = UserManager(applicationContext)
        binding.signUpBtn.setOnClickListener {
            startActivity(Intent(this@LoginActivity,SignUpActivity::class.java))
        }
        binding.loginBtn.setOnClickListener {
            lifecycleScope.launch {
                val user = LoginData(binding.etMobile.text.toString(),binding.etPassword.text.toString())
                viewModel.loginUser(user).let{response ->
                    try{
                        if(response.isSuccessful){
                            if(response.body()!!.code == 1){
                                Toast.makeText(this@LoginActivity, response.body()!!.message, Toast.LENGTH_SHORT).show()
                                userManager.storeUser(1,response.body()!!.userDetails.userId)
                                delay(1000)
                                startActivity(Intent(this@LoginActivity,MapsActivity::class.java))
                                finish()
                            }else{
                                Toast.makeText(this@LoginActivity, response.body()!!.message, Toast.LENGTH_SHORT).show()
                            }
                        }else{
                            Toast.makeText(this@LoginActivity, "Error", Toast.LENGTH_SHORT).show()
                        }
                    }catch (e: Exception){
                        Toast.makeText(this@LoginActivity, "Error", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}