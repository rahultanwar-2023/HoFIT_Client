package com.hofit.hofitclient.ui

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.hofit.hofitclient.databinding.ActivityRegistrationStatusBinding

class RegistrationStatus : AppCompatActivity() {

    private lateinit var binding: ActivityRegistrationStatusBinding
    private lateinit var auth: String
    private lateinit var fireBase: DocumentReference

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationStatusBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth.currentUser!!.uid
        fireBase = Firebase.firestore
            .collection("super_admin")
            .document("rohit-20072022")
            .collection("sports_centers").document(auth)

        fireBase.get()
            .addOnSuccessListener { result ->
                if (result != null){
                    val regisStatus = result.get("outlet_regis_status").toString()
                    binding.txRegisStatus.text = regisStatus

                    if (regisStatus == "Verified") {
                        val timer = object: CountDownTimer(3000, 1000) {
                            override fun onTick(millisUntilFinished: Long) {}

                            override fun onFinish() {
                                val intent  =  Intent(this@RegistrationStatus, PartnerHomepage::class.java)
                                startActivity(intent)
                                finish()
                            }
                        }
                        timer.start()
                    }
                    
                } else {
                    binding.txRegisStatus.text = "Contact HoFIT customer care"
                }
            }


    }
}