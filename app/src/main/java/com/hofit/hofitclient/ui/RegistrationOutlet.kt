package com.hofit.hofitclient.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.hofit.hofitclient.R
import com.hofit.hofitclient.databinding.ActivityRegistrationOutletBinding
import com.hofit.hofitclient.ui.fragment.*
import com.hofit.hofitclient.ui.fragment.registration_outlet_fragment.*

class RegistrationOutlet : AppCompatActivity() {

    private lateinit var binding: ActivityRegistrationOutletBinding

    var currentPage = 0

    private lateinit var auth: String
    private lateinit var fireBase: DocumentReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth.currentUser!!.uid
        fireBase = Firebase.firestore
            .collection("super_admin")
            .document("rohit-20072022")
            .collection("sports_centers").document(auth)


        binding = ActivityRegistrationOutletBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pagerAdapter = ViewPagerAdapter(supportFragmentManager)
        binding.viewPager.adapter = pagerAdapter

        TabLayoutMediator(binding.viewPagerTab, binding.viewPager) { _, _ -> }.attach()

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> {
                        binding.prev.visibility = View.GONE
                        currentPage = 0
                    }
                    1 -> {
                        binding.prev.visibility = View.VISIBLE
                        binding.next.text = getString(R.string.btc_next)
                        currentPage = 1
                    }
                    2 -> {
                        binding.prev.visibility = View.VISIBLE
                        currentPage = 2
                    }
                    3 -> {
                        binding.prev.visibility = View.VISIBLE
                        currentPage = 3
                    }
                    4 -> {
                        binding.prev.visibility = View.VISIBLE
                        currentPage = 4
                    }
                    5 -> {
                        binding.prev.visibility = View.VISIBLE
                        binding.next.text = getString(R.string.btc_finish)
                        currentPage = 5
                    }
                }
                super.onPageSelected(position)
            }
        })
        binding.next.setOnClickListener {
            if (currentPage < 6) {
                currentPage++
                when (currentPage) {
                    0 -> {
                        binding.viewPager.currentItem = 0
                    }
                    1 -> {
                        binding.viewPager.currentItem = 1
                        binding.prev.visibility = View.VISIBLE
                    }
                    2 -> {
                        binding.viewPager.currentItem = 2
                    }
                    3 -> {
                        binding.viewPager.currentItem = 3
                    }
                    4 -> {
                        binding.viewPager.currentItem = 4
                    }
                    5 -> {
                        binding.viewPager.currentItem = 5
                        binding.next.text = getString(R.string.btc_finish)
                    }
                }
            } else if (binding.next.text.toString() == "Finish") {
                val outletImageUrl = hashMapOf(
                    "outlet_regis_status" to "OnGoing"
                )
                fireBase
                    .set(outletImageUrl, SetOptions.merge())
                val intent = Intent(this, RegistrationStatus::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        }

        binding.prev.setOnClickListener {
            if (currentPage > 0) {
                currentPage--
                when (currentPage) {
                    0 -> {
                        binding.viewPager.currentItem = 0
                        it.visibility = View.GONE
                    }
                    1 -> {
                        binding.viewPager.currentItem = 1
                    }
                    2 -> {
                        binding.viewPager.currentItem = 2
                    }
                    3 -> {
                        binding.viewPager.currentItem = 3
                    }
                    4 -> {
                        binding.viewPager.currentItem = 4
                        binding.next.text = getString(R.string.btc_next)
                    }
                    5 -> {
                        binding.viewPager.currentItem = 5
                    }
                }
            }
        }
    }

    private inner class ViewPagerAdapter(fragmentManager: FragmentManager) :
        FragmentStateAdapter(fragmentManager, lifecycle) {
        override fun getItemCount(): Int {
            return 6
        }

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> {
                    OutletDetails()
                }
                1 -> {
                    DocumentUpload()
                }
                2 -> {
                    CategoryTimeSlot()
                }
                3 -> {
                    Facilities()
                }
                4 -> {
                    TrainerCoachDetails()
                }
                else -> {
                    OutletImages()
                }
            }
        }
    }
}