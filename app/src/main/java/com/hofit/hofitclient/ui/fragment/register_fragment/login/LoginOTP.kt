package com.hofit.hofitclient.ui.fragment.register_fragment.login

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.hofit.hofitclient.R
import com.hofit.hofitclient.ui.PartnerHomepage
import com.hofit.hofitclient.ui.RegistrationOutlet
import com.hofit.hofitclient.ui.RegistrationStatus
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.concurrent.TimeUnit

class LoginOTP : Fragment() {

    private lateinit var auth: FirebaseAuth
    private var data: String? = null
    lateinit var storedVerificationId1: String
    lateinit var resendToken1: PhoneAuthProvider.ForceResendingToken
    private var mobileNumber: String? = null
    var buttonResend: Button? = null
    private var otpBtn: Button? = null
    private var typedNumber: TextView? = null

    private var progressBarOtp: ProgressBar? = null

    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_o_t_p, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val otpField: TextInputEditText = view.findViewById(R.id.user_OTP1)
        otpBtn = view.findViewById(R.id.otp_verifyBtn)
        buttonResend = view.findViewById(R.id.btn_resendOtp)
        typedNumber = view.findViewById(R.id.typed_number)
        progressBarOtp = view.findViewById(R.id.progressBarMobileVerify)

        val btnBackToOnBoard: ImageButton =
            view.findViewById(R.id.back_toOnBoard)
        btnBackToOnBoard.setOnClickListener {
            findNavController().popBackStack()
        }

        auth = FirebaseAuth.getInstance()
        data = requireArguments().getString("storedVerificationId")
        resendToken1 = requireArguments().getParcelable("resendToken")!!
        mobileNumber = requireArguments().getString("userMobileNumber")
        typedNumber!!.text = mobileNumber

        setTimerOnOTPSend()

        otpBtn!!.setOnClickListener {
            val otp = otpField.text.toString().trim()
            if (otp.isNotEmpty()) {
                if (otp.length == 6) {
                    otpBtn!!.visibility = View.INVISIBLE
                    progressBarOtp!!.visibility = View.VISIBLE
                    val credential = PhoneAuthProvider.getCredential(data!!, otp)
                    signInWithPhoneAuthCredential(credential)
                } else {
                    Toast.makeText(requireActivity(), "Enter 6-Digit OTP", Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
                Toast.makeText(requireActivity(), "Enter OTP", Toast.LENGTH_SHORT).show()
            }
        }

        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {

            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                Log.d("TAG", "onCodeSent:$verificationId")
                storedVerificationId1 = verificationId
                resendToken1 = token
            }
        }

        buttonResend?.setOnClickListener {
            setTimesForResendOTP()
        }

    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    otpBtn!!.visibility = View.VISIBLE
                    progressBarOtp!!.visibility = View.INVISIBLE
                    checkRegisSuccess()
                } else {
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {

                        otpBtn!!.visibility = View.VISIBLE
                        progressBarOtp!!.visibility = View.INVISIBLE
                        Toast.makeText(requireActivity(), "Invalid OTP", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }

    private fun checkRegisSuccess() {
        val auth1 = Firebase.auth.currentUser!!.uid
        val fireBase = Firebase.firestore
            .collection("super_admin")
            .document("rohit-20072022")
            .collection("sports_centers").document(auth1).collection("outlet_details")
            .document("details")
        try {
            fireBase
                .get()
                .addOnSuccessListener {
                    if (it != null) {
                        when (it.get("outlet_regis_status").toString()) {
                            "Unverified" -> {
                                val intent = Intent(context, RegistrationOutlet::class.java)
                                startActivity(intent)
                            }
                            "OnGoing" -> {
                                val intent = Intent(context, RegistrationStatus::class.java)
                                startActivity(intent)
                            }
                            else -> {
                                val intent = Intent(context, PartnerHomepage::class.java)
                                startActivity(intent)
                            }
                        }
                    }
                }
        } catch (_: Exception) {
        }
    }

    private fun setTimesForResendOTP() {
        object : CountDownTimer(90000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val f: NumberFormat = DecimalFormat("00")
                val min = millisUntilFinished / 60000 % 60
                val sec = millisUntilFinished / 1000 % 60
                login()
                buttonResend!!.text = String.format("%s : %s", f.format(min), f.format(sec))
                buttonResend!!.isEnabled = false
            }

            override fun onFinish() {
                buttonResend!!.text = "Resend"
                buttonResend!!.isEnabled = true
            }
        }.start()
    }

    private fun login() {
        sendVerificationCode(mobileNumber!!)
    }

    private fun sendVerificationCode(number: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(number)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(requireActivity())
            .setCallbacks(callbacks)
            .setForceResendingToken(resendToken1)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun setTimerOnOTPSend() {
        object : CountDownTimer(90000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val f: NumberFormat = DecimalFormat("00")
                val min = millisUntilFinished / 60000 % 60
                val sec = millisUntilFinished / 1000 % 60
                buttonResend!!.text = String.format("%s : %s", f.format(min), f.format(sec))
                buttonResend!!.isEnabled = false
            }

            override fun onFinish() {
                buttonResend!!.text = "Resend"
                buttonResend!!.isEnabled = true
            }
        }.start()
    }
}