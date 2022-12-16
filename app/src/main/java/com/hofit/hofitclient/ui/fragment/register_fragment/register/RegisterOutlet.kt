package com.hofit.hofitclient.ui.fragment.register_fragment.register

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.hofit.hofitclient.R
import com.hofit.hofitclient.databinding.FragmentRegisterOutletBinding
import java.util.concurrent.TimeUnit


class RegisterOutlet : Fragment() {

    private var _binding: FragmentRegisterOutletBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var storedVerificationId: String
    private lateinit var number: String
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks

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
    ): View {
        _binding = FragmentRegisterOutletBinding.inflate(inflater, container, false)
        auth = Firebase.auth
        //Callbacks for Mobile Number Verification
        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {}

            override fun onVerificationFailed(e: FirebaseException) {
                if (e is FirebaseAuthInvalidCredentialsException) {
                    Log.d("Firebase Verification", "onVerificationFailed: $e")
                } else if (e is FirebaseTooManyRequestsException) {
                    Log.d("Firebase Verification", "onVerificationFailed: $e")
                }
                binding.regisProgressBar.visibility = View.INVISIBLE
                binding.sendOtp1.visibility = View.VISIBLE
                Toast.makeText(requireContext(), "$e", Toast.LENGTH_LONG).show()
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
                        R.id.action_registerOutlet_to_registerOTP,
                        Bundle().apply {
                            putString("storedVerificationId", storedVerificationId)
                            putParcelable("resendToken", resendToken)
                            putString("userMobileNumber", number)
                        })
                    binding.regisProgressBar.visibility = View.INVISIBLE
                    binding.sendOtp1.visibility = View.VISIBLE
                }, 190)
            }
        }

        binding.sendOtp1.setOnClickListener {
            number = binding.inputMobileNumber1.text.toString().trim()
            val regexStr =
                "(0/91)?[7-9][0-9]{9}".toRegex()
            if (number.isNotEmpty()) {
                if (number.matches(regexStr)) {
                    binding.sendOtp1.visibility = View.INVISIBLE
                    binding.regisProgressBar.visibility = View.VISIBLE
                    registerOutlet()

                } else {
                    Toast.makeText(requireContext(), "Enter valid number", Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
                Toast.makeText(requireContext(), "Enter mobile number", Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }

    private fun registerOutlet() {

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
                            "onCreateView: ${document.getString("outlet_number")}"
                        )
                        number1 = document.getString("outlet_mobile_number").toString()
                    }
                    if (numberCheck == number1) {
                        binding.sendOtp1.visibility = View.VISIBLE
                        binding.regisProgressBar.visibility = View.INVISIBLE
                        Toast.makeText(
                            requireContext(),
                            "Mobile number already register",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        binding.sendOtp1.visibility = View.VISIBLE
                        binding.regisProgressBar.visibility = View.INVISIBLE
                        number = "+91$number"
                        sendVerificationCode(number)
                    }
                }
            }
            .addOnFailureListener{

                Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_SHORT).show()

                binding.sendOtp1.visibility = View.VISIBLE
                binding.regisProgressBar.visibility = View.INVISIBLE
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}