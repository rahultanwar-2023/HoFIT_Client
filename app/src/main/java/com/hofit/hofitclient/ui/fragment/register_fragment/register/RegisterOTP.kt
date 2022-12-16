package com.hofit.hofitclient.ui.fragment.register_fragment.register

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.hofit.hofitclient.databinding.FragmentRegisterOTPBinding
import com.hofit.hofitclient.ui.RegistrationOutlet
import com.hofit.hofitclient.ui.RegistrationStatus
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.concurrent.TimeUnit

class RegisterOTP : Fragment() {

    private var _binding: FragmentRegisterOTPBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private var data: String? = null
    lateinit var storedVerificationId1: String
    private lateinit var resendToken1: PhoneAuthProvider.ForceResendingToken
    private var mobileNumber: String? = null
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks

    private lateinit var countDown: CountDownTimer

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterOTPBinding.inflate(inflater, container, false)
        auth = Firebase.auth

        //Instance & Data
        auth = FirebaseAuth.getInstance()
        data = requireArguments().getString("storedVerificationId")
        resendToken1 = requireArguments().getParcelable("resendToken")!!
        mobileNumber = requireArguments().getString("userMobileNumber")
        binding.typedNumber.text = mobileNumber

        //OnActivity Create Timer get trigger
        setTimerOnOTPSend()

        //Listener Handling on otp button
        binding.otpRegisterVerifyBtn.setOnClickListener {
            val otp = binding.userRegisterOTP1.text.toString().trim()
            if (otp.isNotEmpty()) {
                if (otp.length == 6) {
                    binding.otpRegisterVerifyBtn.visibility = View.INVISIBLE
                    binding.progressBarMobileVerify.visibility = View.VISIBLE
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

        //Callbacks for Authentication
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

        //Resend OTP Listener
        binding.btnResendOtp1.setOnClickListener {
            setTimesForResendOTP()
        }

        return binding.root
    }

    //Function for Resend OTP | Force Resend OTP
    private fun setTimesForResendOTP() {
         countDown = object : CountDownTimer(90000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val f: NumberFormat = DecimalFormat("00")
                val min = millisUntilFinished / 60000 % 60
                val sec = millisUntilFinished / 1000 % 60
                login()
                binding.btnResendOtp1.text = String.format("%s : %s", f.format(min), f.format(sec))
                binding.btnResendOtp1.isEnabled = false
            }

            override fun onFinish() {
                binding.btnResendOtp1.text = "Resend"
                binding.btnResendOtp1.isEnabled = true
            }
        }.start()
    }

    //Function For new OTP Generate
    private fun login() {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(mobileNumber!!)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(requireActivity())
            .setCallbacks(callbacks)
            .setForceResendingToken(resendToken1)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    //Authentication checker function (Firebase PhoneAuthCredential)
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    binding.otpRegisterVerifyBtn.visibility = View.VISIBLE
                    binding.progressBarMobileVerify.visibility = View.INVISIBLE
                    createEmptyEntityForClient()
                    val intent = Intent(requireContext(), RegistrationOutlet::class.java)
                    startActivity(intent)
                    requireActivity().finish()
                } else {
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        binding.otpRegisterVerifyBtn.visibility = View.VISIBLE
                        binding.progressBarMobileVerify.visibility = View.INVISIBLE
                        Toast.makeText(requireActivity(), "Invalid OTP", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }

    //Create filled or unfilled field for outlet (Firebase Firestore)
    private fun createEmptyEntityForClient() {

        val auth1 = Firebase.auth.currentUser!!.uid
        val fireBase = Firebase.firestore
            .collection("super_admin")
            .document("rohit-20072022")
            .collection("sports_centers").document(auth1)

        val outletRegis = hashMapOf(
            "owner_id" to auth1,
            "outlet_id" to auth1,
            "outlet_mobile_number" to mobileNumber,
            "owner_name" to "",
            "outlet_name" to "",
            "outlet_category" to "",
            "outlet_regis_status" to "Unverified",
            "outlet_image" to "",
            "outlet_wa_number" to "",
            "outlet_address" to "",
            "outlet_city" to "",
            "outlet_state" to "",
            "outlet_pincode" to ""
        )

        fireBase
            .set(outletRegis, SetOptions.merge())
            .addOnSuccessListener {}
            .addOnFailureListener {}

    }

    //OnActivity start timer function
    private fun setTimerOnOTPSend() {
        countDown = object : CountDownTimer(90000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val f: NumberFormat = DecimalFormat("00")
                val min = millisUntilFinished / 60000 % 60
                val sec = millisUntilFinished / 1000 % 60
                binding.btnResendOtp1.text = String.format("%s : %s", f.format(min), f.format(sec))
                binding.btnResendOtp1.isEnabled = false
            }

            override fun onFinish() {
                binding.btnResendOtp1.text = "Resend"
                binding.btnResendOtp1.isEnabled = true
            }
        }.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        countDown.cancel()
        _binding = null
    }

}