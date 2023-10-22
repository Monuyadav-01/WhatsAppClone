package com.example.whatsappclone

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.example.whatsappclone.databinding.ActivityLoginBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var phoneNumber: String
    private lateinit var countryCode: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // add hint request for phone number
        binding.phoneLogin.addTextChangedListener {
            binding.loginBtn.isEnabled = ((it.isNullOrEmpty() || (it.length >= 10)))
        }

        binding.loginBtn.setOnClickListener {
            checkNumber()
        }

    }

    private fun popUp() {
        MaterialAlertDialogBuilder(this).apply {
            setMessage("Enter valid number")
            setPositiveButton("Ok") { dialog, which ->
                dialog.dismiss()

            }
        }
    }

    private fun checkNumber() {
        countryCode = binding.ccp.selectedCountryCodeWithPlus
        phoneNumber = countryCode + binding.phoneLogin.text.toString()

        notifyUser()
    }

    private fun notifyUser() {
        MaterialAlertDialogBuilder(this).apply {
            setMessage("We will be verify the phone number $phoneNumber\nis this ok would you like Proceed the number")

            setPositiveButton("OK") { _,_ ->
                showOtpActivity()
            }
            setNegativeButton("EDIT") { dialog, which ->
                dialog.dismiss()
            }
            setCancelable(false)
            create()
            show()
        }
    }

    private fun showOtpActivity() {
        val i = Intent(this, OtpActivity::class.java)
            .putExtra(PHONE_NUMBER, phoneNumber)
        startActivity(i)
        finish()
    }

}
