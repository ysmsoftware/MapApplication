package com.application.mapapplication.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.application.mapapplication.databinding.ActivitySignUpBinding
import com.application.mapapplication.models.UserDetails
import com.application.mapapplication.viewmodel.MapViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

@AndroidEntryPoint
class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private val viewModel: MapViewModel by viewModels()
    lateinit var taluka_spinner: Spinner
    private lateinit var talukaAdapter: ArrayAdapter<String>
    var talukaTxt by Delegates.notNull<Int>()
    lateinit var village_spinner: Spinner
    private lateinit var villageAdapter: ArrayAdapter<String>
    var villageTxt by Delegates.notNull<Int>()
    var talukaNameList: ArrayList<String> = ArrayList()
    var talukaCodeList: ArrayList<Int> = ArrayList()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        village_spinner = binding.villageSpinner
        taluka_spinner = binding.talukaSpinner
        lifecycleScope.launch {
            viewModel.getTalukaList().let { response ->
                try {
                    if (response.isSuccessful) {
                        for(i in response.body()!!.taluka.indices){
                            talukaNameList.add(response.body()!!.taluka[i].talukaName)
                            talukaCodeList.add(response.body()!!.taluka[i].talukaCode)
                        }
                        talukaAdapter = ArrayAdapter(
                            this@SignUpActivity,
                            com.google.android.material.R.layout.support_simple_spinner_dropdown_item, talukaNameList
                        )

                        taluka_spinner.adapter = talukaAdapter
                        taluka_spinner.onItemSelectedListener = object :
                            AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(
                                parent: AdapterView<*>,
                                view: View, position: Int, id: Long
                            ) {
                                talukaTxt = talukaCodeList[position]

                                lifecycleScope.launch(Dispatchers.Main) {
                                    viewModel.getVillageListByTaluka(talukaTxt).let { response ->
                                        try {
                                            if (response.isSuccessful) {
                                                val villageNameList: ArrayList<String> = ArrayList()
                                                val villageCodeList: ArrayList<Int> = ArrayList()
                                                for(i in response.body()!!.village.indices){
                                                    villageNameList.add(response.body()!!.village[i].villageName)
                                                    villageCodeList.add(response.body()!!.village[i].villageCode)
                                                }

                                                villageAdapter = ArrayAdapter(
                                                    this@SignUpActivity,
                                                    com.google.android.material.R.layout.support_simple_spinner_dropdown_item, villageNameList
                                                )

                                                village_spinner.adapter = villageAdapter
                                                village_spinner.onItemSelectedListener = object :
                                                    AdapterView.OnItemSelectedListener {
                                                    override fun onItemSelected(
                                                        parent: AdapterView<*>,
                                                        view: View, position: Int, id: Long
                                                    ) {
                                                        villageTxt = villageCodeList[position]
                                                    }

                                                    override fun onNothingSelected(parent: AdapterView<*>) {
                                                        // write code to perform some action
                                                    }
                                                }
                                            } else {
                                                Toast.makeText(this@SignUpActivity, "Error", Toast.LENGTH_SHORT).show()
                                            }
                                        } catch (e: Exception) {
                                            Toast.makeText(this@SignUpActivity, "Error", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            }

                            override fun onNothingSelected(parent: AdapterView<*>) {
                                // write code to perform some action
                            }
                        }
                    } else {
                        Toast.makeText(this@SignUpActivity, "Error", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@SignUpActivity, "Error", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.registerBtn.setOnClickListener {
            if (validateForm(
                    binding.etName.text.toString(),
                    binding.etMobile.text.toString(),
                    binding.etPassword.text.toString(),
                    binding.etConfirmPassword.text.toString()
                )
            ) {
                lifecycleScope.launch{
                   val user = UserDetails(0,binding.etMobile.text.toString(),talukaTxt,binding.etName.text.toString(),
                       binding.etPassword.text.toString(),villageTxt)
                    viewModel.signUpUser(user).let{response ->
                        try {
                            if(response.isSuccessful){
                                Toast.makeText(this@SignUpActivity, response.body()!!.message, Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this@SignUpActivity, LoginActivity::class.java))
                            }else{
                                Toast.makeText(this@SignUpActivity, "Error", Toast.LENGTH_SHORT).show()
                            }
                        }catch(e: Exception){
                            Toast.makeText(this@SignUpActivity, "Error", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

            }
        }
    }

//    private fun villageNameCall(talukaTxt: Int) {
//        lifecycleScope.launch {
//            viewModel.getVillageListByTaluka(talukaTxt).let { response ->
//                try {
//                    if (response.isSuccessful) {
//                        for(i in response.body()!!.village.indices){
//                            villageNameList.add(response.body()!!.village[i].villageName)
//                            villageCodeList.add(response.body()!!.village[i].villageCode)
//                        }
//                        villageAdapter = ArrayAdapter(
//                            this@SignUpActivity,
//                            com.google.android.material.R.layout.support_simple_spinner_dropdown_item, villageNameList
//                        )
//
//                        village_spinner.adapter = villageAdapter
//                        village_spinner.onItemSelectedListener = object :
//                            AdapterView.OnItemSelectedListener {
//                            override fun onItemSelected(
//                                parent: AdapterView<*>,
//                                view: View, position: Int, id: Long
//                            ) {
//                                villageTxt = villageCodeList[position]
//                            }
//
//                            override fun onNothingSelected(parent: AdapterView<*>) {
//                                // write code to perform some action
//                            }
//                        }
//                    } else {
//                        Toast.makeText(this@SignUpActivity, "Error", Toast.LENGTH_SHORT).show()
//                    }
//                } catch (e: Exception) {
//                    Toast.makeText(this@SignUpActivity, "Error", Toast.LENGTH_SHORT).show()
//                }
//            }
//        }
//    }

    private fun validateForm(
        name: String,
        mobile: String,
        password: String,
        confirmPassword: String,
    ): Boolean {
        return when {
            TextUtils.isEmpty(name) -> {
                val snackBar = Snackbar.make(
                    findViewById(android.R.id.content),
                    "Please enter a name", Snackbar.LENGTH_LONG
                )
                viewModel.showErrorSnackBar(this, snackBar)
                false
            }
            TextUtils.isEmpty(mobile) -> {
                val snackBar = Snackbar.make(
                    findViewById(android.R.id.content),
                    "Please enter your mobile", Snackbar.LENGTH_LONG
                )
                viewModel.showErrorSnackBar(this, snackBar)
                false
            }
            mobile.length != 10 -> {
                val snackBar = Snackbar.make(
                    findViewById(android.R.id.content),
                    "Please enter your mobile", Snackbar.LENGTH_LONG
                )
                viewModel.showErrorSnackBar(this, snackBar)
                false
            }
            TextUtils.isEmpty(password) -> {
                val snackBar = Snackbar.make(
                    findViewById(android.R.id.content),
                    "Please enter your password", Snackbar.LENGTH_LONG
                )
                viewModel.showErrorSnackBar(this, snackBar)
                false
            }
            TextUtils.isEmpty(confirmPassword) -> {
                val snackBar = Snackbar.make(
                    findViewById(android.R.id.content),
                    "Please enter your password", Snackbar.LENGTH_LONG
                )
                viewModel.showErrorSnackBar(this, snackBar)
                false
            }
            password != confirmPassword -> {
                val snackBar = Snackbar.make(
                    findViewById(android.R.id.content),
                    "Please enter the same password", Snackbar.LENGTH_LONG
                )
                viewModel.showErrorSnackBar(this, snackBar)
                false
            }
            else -> {
                true
            }
        }
    }
}