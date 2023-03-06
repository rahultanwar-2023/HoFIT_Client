package com.hofit.hofitclient.ui.fragment.register_fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.hofit.hofitclient.R
import com.hofit.hofitclient.R.*
import com.hofit.hofitclient.adpter.OnBoardAdapter
import com.hofit.hofitclient.model.OnBoardingData
import com.hofit.hofitclient.ui.RegistrationOutlet
import com.hofit.hofitclient.ui.RegistrationStatus

class OnBoard : Fragment() {

    //Layout Var Define
    private var layoutRegisStatus : ConstraintLayout? = null
    private var layoutOnboard : ConstraintLayout? = null

    //ProgressBar (Status ProgressBar)
    private var progressBarRegisStatus : ProgressBar? = null

    private var onBoardAdapter: OnBoardAdapter? = null
    private var tabLayout: TabLayout? = null
    private var onBoardViewPager: ViewPager? = null
    var btnNext: FloatingActionButton? = null
    var btnGetStarted: Button? = null

    var position = 0

    //Dialog Views
    private var dialog: BottomSheetDialog? = null
    private var registerBtn: Button? = null
    private var loginBtn: Button? = null
    private lateinit var auth: String
    private lateinit var fireBase: CollectionReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            auth = Firebase.auth.currentUser!!.uid
            fireBase = Firebase.firestore
                .collection("super_admin")
                .document("rohit-20072022")
                .collection("sports_centers")
            fireBase.document(auth)
                .get()
                .addOnSuccessListener {
                    if (it != null) {
                        val regisStatus1 = it.get("outlet_regis_status").toString()
                        if (regisStatus1 == "OnGoing") {
                            val intent = Intent(context, RegistrationStatus::class.java)
                            startActivity(intent)
                        } else {
                            val intent = Intent(context, RegistrationOutlet::class.java)
                            startActivity(intent)
                        }
                    }
                }

        } catch (_: Exception) {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(layout.fragment_on_board, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        tabLayout = view.findViewById(R.id.pagerSlider_indicator)
        btnNext = view.findViewById(R.id.btn_next)
        btnGetStarted = view.findViewById(R.id.btn_getStarted)

        val onBoardingData: MutableList<OnBoardingData> = ArrayList()
        onBoardingData.add(
            OnBoardingData(
                requireActivity().resources.getString(string.onboardTitle1),
                drawable.user_research
            )
        )
        onBoardingData.add(
            OnBoardingData(
                requireActivity().resources.getString(string.onboardTitle2),
                drawable.management
            )
        )
        onBoardingData.add(
            OnBoardingData(
                requireActivity().resources.getString(string.onboardTitle3),
                drawable.customer_service
            )
        )
        setOnBoardingViewPagerAdapter(view, onBoardingData)

        position = onBoardViewPager!!.currentItem
        btnNext?.setOnClickListener {
            if (position < onBoardingData.size) {
                position++
                onBoardViewPager!!.currentItem = position
            }
        }

        tabLayout!!.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                position = tab!!.position
                if (tab.position == onBoardingData.size - 1) {
                    btnNext!!.visibility = View.INVISIBLE
                    btnGetStarted!!.visibility = View.VISIBLE
                } else {
                    btnGetStarted!!.visibility = View.INVISIBLE
                    btnNext!!.visibility = View.VISIBLE
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })


        btnGetStarted?.setOnClickListener {
            dialog = BottomSheetDialog(view.context)
            val view1 = layoutInflater.inflate(layout.layout_user_registration, null)
            registerBtn = view1.findViewById(R.id.register)
            registerBtn!!.setOnClickListener {
                dialog!!.dismiss()
                Thread.sleep(100)
                findNavController().navigate(R.id.action_onBoard_to_registerOutlet)
            }
            loginBtn = view1.findViewById(R.id.login)
            loginBtn!!.setOnClickListener {
                dialog!!.dismiss()
                Thread.sleep(100)
                findNavController().navigate(R.id.action_onBoard_to_loginOutlet)
            }
            dialog!!.setContentView(view1)
            dialog!!.show()
        }

    }

    private fun setOnBoardingViewPagerAdapter(view: View, onBoardingData: List<OnBoardingData>) {
        onBoardViewPager = view.findViewById(R.id.onboarding_Pager)
        onBoardAdapter = OnBoardAdapter(view.context, onBoardingData)
        onBoardViewPager!!.adapter = onBoardAdapter
        tabLayout?.setupWithViewPager(onBoardViewPager)
    }

}