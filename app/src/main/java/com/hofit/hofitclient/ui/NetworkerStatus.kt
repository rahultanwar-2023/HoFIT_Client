package com.hofit.hofitclient.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import com.hofit.hofitclient.R
import com.hofit.hofitclient.viewmodel.SplashViewModel

class NetworkerStatus : AppCompatActivity() {

    private lateinit var mProgressBar: LinearProgressIndicator
    private lateinit var mCheckStatus: TextView

    private val splashViewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().apply {
            setKeepOnScreenCondition {
                splashViewModel.isLoading.value
            }
        }
        FirebaseApp.initializeApp(this)
        val firebaseAppCheck = FirebaseAppCheck.getInstance()
        firebaseAppCheck.installAppCheckProviderFactory(
            PlayIntegrityAppCheckProviderFactory.getInstance()
        )
        setContentView(R.layout.activity_networker_status)

        mProgressBar = findViewById(R.id.progressBar)
        mCheckStatus = findViewById(R.id.network_status)

        mProgressBar.visibility = View.VISIBLE

        val handler = Handler(Looper.myLooper()!!)
        handler.postDelayed({
            val volleyQueue = Volley.newRequestQueue(this)
            val url = "https://www.google.co.in/"

            val stringRequest = StringRequest(
                Request.Method.GET, url,
                {
                    mProgressBar.visibility = View.INVISIBLE
                    mCheckStatus.visibility = View.VISIBLE
                    mCheckStatus.text = "Welcome To HoFIT"
                    val handler1 = Handler(Looper.myLooper()!!)
                    handler1.postDelayed({
                        startActivity(Intent(this@NetworkerStatus, MainActivity::class.java))
                        finish()
                    }, 1000)
                }) {
                mProgressBar.visibility = View.INVISIBLE
                mCheckStatus.text = "No Internet Connection"
                mCheckStatus.visibility = View.VISIBLE
            }

            volleyQueue.add(stringRequest)

        }, 3000)

    }

}