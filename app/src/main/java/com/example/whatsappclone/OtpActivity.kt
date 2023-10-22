package com.example.whatsappclone

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.whatsappclone.databinding.ActivityOtpBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

const val PHONE_NUMBER = "phoneNumber"

class OtpActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOtpBinding

    private var phoneNumber: String? = null
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    var mVerificationId: String? = null
    var mResendToken: PhoneAuthProvider.ForceResendingToken? = null
    private lateinit var progressDialog: ProgressDialog
    private var mCounterDown: CountDownTimer? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
        startVerify()
    }

    // working on progressDialog
    private fun startVerify() {
        PhoneAuthProvider.getInstance()
            .verifyPhoneNumber(phoneNumber!!, 60, TimeUnit.SECONDS, this, callbacks)
        progressDialog = createProgressDialog("Sending a verification Code", false)
        progressDialog.show()
        showTimer(60000)
    }

    // countDown Timer

    private fun showTimer(milliSecondsFuture: Long) {
        binding.resetBtn.isEnabled = false
        mCounterDown = object : CountDownTimer(milliSecondsFuture, 1000) {
            override fun onTick(milliSecUntilFinished: Long) {
                binding.counterTV.isVisible = true
                binding.counterTV.text =
                    getString(R.string.seconds_remaining, milliSecUntilFinished / 1000)
            }

            override fun onFinish() {
                binding.resetBtn.isEnabled = true
                binding.counterTV.isVisible = false
            }

        }.start()
    }

    // app destroyed then working on timer
    override fun onDestroy() {
        super.onDestroy()
        if (mCounterDown != null) {
            mCounterDown!!.cancel()
        }
    }


    // initvews for whole otpactivity

    private fun initViews() {
        phoneNumber = intent.getStringExtra(PHONE_NUMBER)
        binding.verifyTv.text = getString(R.string.verify_number, phoneNumber)
        setSpannableString()

        // firebase auth started from here using phone number
        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {

                if (::progressDialog.isInitialized) {
                    progressDialog.dismiss()
                }

                val smsCode = credential.smsCode
                if (!smsCode.isNullOrBlank()) {
                    binding.sentCodeET.setText(smsCode)
                }
                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                if (::progressDialog.isInitialized) {
                    progressDialog.dismiss()
                }

                if (e is FirebaseAuthInvalidCredentialsException) {

                } else if (e is FirebaseTooManyRequestsException) {

                } else if (e is FirebaseAuthMissingActivityForRecaptchaException) {

                }
                // when user put wrong number making a fun
                notifyUser("Your number might be wrong")

            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken,
            ) {
                if (::progressDialog.isInitialized) {
                    progressDialog.dismiss()
                }
                binding.counterTV.isVisible = false
                mVerificationId = verificationId
                mResendToken = token
            }
        }
    }

    // when otp came notify the user you are wrong function
    private fun notifyUser(message: String) {
        MaterialAlertDialogBuilder(this).apply {
            setMessage(message)
            setPositiveButton("ok") { _, _ ->
                backToLoginActivity()
            }
        }
    }


    // for signin using firebase for phonenumber
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {

    }

    // spannable string
    private fun setSpannableString() {
        val span =
            SpannableString(getString(R.string.sms, phoneNumber))
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(p0: View) {
                // send back
                showLoginActivity()

            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
                ds.color = ds.linkColor
            }
        }
        span.setSpan(clickableSpan, span.length - 13, span.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.waitingTv.movementMethod = LinkMovementMethod.getInstance()
        binding.waitingTv.text = span


    }

    // show loginActivity

    private fun showLoginActivity() {
        startActivity(
            Intent(this, LoginActivity::class.java).setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            )
        )
    }


    override fun onBackPressed() {
        super.onBackPressed()

    }

    private fun backToLoginActivity() {
        val i = Intent(this, OtpActivity::class.java)
        startActivity(i)
        finish()
    }


}

//dialog function

fun Context.createProgressDialog(message: String, isCancelable: Boolean): ProgressDialog {
    return ProgressDialog(this).apply {
        setCancelable(false)
        setMessage(message)
        setCanceledOnTouchOutside(false)
    }

}