package com.hofit.hofitclient.ui.fragment.register_fragment.login

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.hofit.hofitclient.R
import java.util.concurrent.TimeUnit


class LoginOutlet : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var storedVerificationId: String
    private lateinit var number: String
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks

    private var editTextMobileNumber: TextInputEditText? = null
    private lateinit var sendOTP: Button
    private var loginLoading: ProgressBar? = null

    private lateinit var fireBase: CollectionReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            fireBase = Firebase.firestore
                .collection("super_admin")
                .document("rohit-20072022")
                .collection("sports_centers")
        } catch (_: Exception) {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login_partner, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)

        sendOTP.setOnClickListener {
            login(view)
        }

        //CallBacks For OTP Verification
        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {}

            override fun onVerificationFailed(e: FirebaseException) {
                if (e is FirebaseAuthInvalidCredentialsException) {
                    Log.d("Firebase Verification", "onVerificationFailed: $e")
                } else if (e is FirebaseTooManyRequestsException) {
                    Log.d("Firebase Verification", "onVerificationFailed: $e")
                }
                loginLoading!!.visibility = View.INVISIBLE
                sendOTP.visibility = View.VISIBLE
                Toast.makeText(view.context, "Try Again Later", Toast.LENGTH_LONG).show()
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                Log.d("TAG", "onCodeSent:$verificationId")
                storedVerificationId = verificationId
                resendToken = token
                Handler(Looper.getMainLooper()).postDelayed({
                    findNavController().navigate(
                        R.id.action_loginOutlet_to_loginOTP,
                        Bundle().apply {
                            putString("storedVerificationId", storedVerificationId)
                            putParcelable("resendToken", resendToken)
                            putString("userMobileNumber", number)
                        })
                    loginLoading!!.visibility = View.INVISIBLE
                    sendOTP.visibility = View.VISIBLE
                }, 190)
            }
        }
    }

    private fun init(view: View) {
        auth = FirebaseAuth.getInstance()

        editTextMobileNumber = view.findViewById(R.id.edMobileNumber1)
        sendOTP = view.findViewById(R.id.send_otp)
        loginLoading = view.findViewById(R.id.progressBar)
    }

    private fun login(view: View) {
        number = editTextMobileNumber!!.text.toString().trim()
        val regexStr =
            "(0/91)?[7-9][0-9]{9}".toRegex()
        if (number.isNotEmpty()) {
            if (number.matches(regexStr)) {
                sendOTP.visibility = View.INVISIBLE
                loginLoading!!.visibility = View.VISIBLE
                loginOutlet()

            } else {
                Toast.makeText(view.context, "Enter valid number", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(view.context, "Enter mobile number", Toast.LENGTH_SHORT).show()
        }

    }

    private fun loginOutlet() {
        val numberCheck = "+91$number"
        Log.i(
            "CheckOut",
            "onCreateView: $numberCheck"
        )

        fireBase
            .whereEqualTo("outlet_mobile_number", numberCheck)
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    var number1: String? = null
                    for (document in it.result) {
                        Log.i(
                            "CheckOut",
                            "onCreateView: ${document.getString("outlet_mobile_number")}"
                        )
                        number1 = document.getString("outlet_mobile_number").toString()
                    }

                    Log.i(
                        "CheckOut",
                        "onCreateView: $number1"
                    )

                    if (numberCheck != number1) {
                        sendOTP.visibility = View.VISIBLE
                        loginLoading!!.visibility = View.INVISIBLE
                        Toast.makeText(
                            requireContext(),
                            "Mobile number is not register",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        sendOTP.visibility = View.VISIBLE
                        loginLoading!!.visibility = View.INVISIBLE
                        number = "+91$number"
                        sendVerificationCode(number)
                    }
                }
            }
    }

    private fun sendVerificationCode(number: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(number)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(requireContext() as Activity)
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

}