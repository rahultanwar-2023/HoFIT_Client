package com.hofit.hofitclient.ui.fragment.registration_outlet_fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.hofit.hofitclient.databinding.FragmentCategoryTimeSlotBinding


class CategoryTimeSlot : Fragment() {

    private var _binding: FragmentCategoryTimeSlotBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth : String
    private lateinit var fireBase: DocumentReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentCategoryTimeSlotBinding.inflate(inflater, container, false)
        auth = Firebase.auth.currentUser!!.uid
        fireBase = Firebase.firestore
            .collection("super_admin")
            .document("rohit-20072022")
            .collection("sports_centers").document(auth)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnSaveOutletDays.setOnClickListener {

            binding.btnSaveOutletDays.visibility = View.INVISIBLE
            binding.progressOutletTimeSlot.visibility = View.VISIBLE

            val firebaseDocMorning = fireBase.collection("time_slots").document("morning")
            if (binding.checkBoxM1.isChecked) {
                val outletF = hashMapOf(
                    "time_slot" to binding.checkBoxM1.text.toString()
                )
                firebaseDocMorning.collection("morning_time_slots").document().set(outletF)
            }
            if (binding.checkBoxM2.isChecked) {
                val outletF = hashMapOf(
                    "time_slot" to binding.checkBoxM2.text.toString()
                )
                firebaseDocMorning.collection("morning_time_slots").document().set(outletF)
            }
            if (binding.checkBoxM3.isChecked) {
                val outletF = hashMapOf(
                    "time_slot" to binding.checkBoxM3.text.toString()
                )
                firebaseDocMorning.collection("morning_time_slots").document().set(outletF)
            }
            if (binding.checkBoxM4.isChecked) {
                val outletF = hashMapOf(
                    "time_slot" to binding.checkBoxM4.text.toString()
                )
                firebaseDocMorning.collection("morning_time_slots").document().set(outletF)
            }
            if (binding.checkBoxM5.isChecked) {
                val outletF = hashMapOf(
                    "time_slot" to binding.checkBoxM5.text.toString()
                )
                firebaseDocMorning.collection("morning_time_slots").document().set(outletF)
            }
            if (binding.checkBoxM6.isChecked) {
                val outletF = hashMapOf(
                    "time_slot" to binding.checkBoxM6.text.toString()
                )
                firebaseDocMorning.collection("morning_time_slots").document().set(outletF)
            }

            val firebaseDocAfternoon = fireBase.collection("time_slots").document("afternoon")
            if (binding.checkBoxA1.isChecked) {
                val outletF = hashMapOf(
                    "time_slot" to binding.checkBoxA1.text.toString()
                )
                firebaseDocAfternoon.collection("afternoon_time_slots").document().set(outletF)
            }
            if (binding.checkBoxA2.isChecked) {
                val outletF = hashMapOf(
                    "time_slot" to binding.checkBoxA2.text.toString()
                )
                firebaseDocAfternoon.collection("afternoon_time_slots").document().set(outletF)
            }
            if (binding.checkBoxA3.isChecked) {
                val outletF = hashMapOf(
                    "time_slot" to binding.checkBoxA3.text.toString()
                )
                firebaseDocAfternoon.collection("afternoon_time_slots").document().set(outletF)
            }
            if (binding.checkBoxA4.isChecked) {
                val outletF = hashMapOf(
                    "time_slot" to binding.checkBoxA4.text.toString()
                )
                firebaseDocAfternoon.collection("afternoon_time_slots").document().set(outletF)
            }
            if (binding.checkBoxA5.isChecked) {
                val outletF = hashMapOf(
                    "time_slot" to binding.checkBoxA5.text.toString()
                )
                firebaseDocAfternoon.collection("afternoon_time_slots").document().set(outletF)
            }
            if (binding.checkBoxA6.isChecked) {
                val outletF = hashMapOf(
                    "time_slot" to binding.checkBoxA6.text.toString()
                )
                firebaseDocAfternoon.collection("afternoon_time_slots").document().set(outletF)
            }

            val firebaseDocEvening = fireBase.collection("time_slots").document("evening")
            if (binding.checkBoxE1.isChecked) {
                val outletF = hashMapOf(
                    "time_slot" to binding.checkBoxE1.text.toString()
                )
                firebaseDocEvening.collection("evening_time_slots").document().set(outletF)
            }
            if (binding.checkBoxE2.isChecked) {
                val outletF = hashMapOf(
                    "time_slot" to binding.checkBoxE2.text.toString()
                )
                firebaseDocEvening.collection("evening_time_slots").document().set(outletF)
            }
            if (binding.checkBoxE3.isChecked) {
                val outletF = hashMapOf(
                    "time_slot" to binding.checkBoxE3.text.toString()
                )
                firebaseDocEvening.collection("evening_time_slots").document().set(outletF)
            }
            if (binding.checkBoxE4.isChecked) {
                val outletF = hashMapOf(
                    "time_slot" to binding.checkBoxE4.text.toString()
                )
                firebaseDocEvening.collection("evening_time_slots").document().set(outletF)
            }
            if (binding.checkBoxE5.isChecked) {
                val outletF = hashMapOf(
                    "time_slot" to binding.checkBoxE5.text.toString()
                )
                firebaseDocEvening.collection("evening_time_slots").document().set(outletF)
            }
            if (binding.checkBoxE6.isChecked) {
                val outletF = hashMapOf(
                    "time_slot" to binding.checkBoxE6.text.toString()
                )
                firebaseDocEvening.collection("evening_time_slots").document().set(outletF)
            }

            val firebaseDocLateNight = fireBase.collection("time_slots").document("late_night")
            if (binding.checkBoxN1.isChecked) {
                val outletF = hashMapOf(
                    "time_slot" to binding.checkBoxN1.text.toString()
                )
                firebaseDocLateNight.collection("late_night_time_slots").document().set(outletF)
            }
            if (binding.checkBoxN2.isChecked) {
                val outletF = hashMapOf(
                    "time_slot" to binding.checkBoxN2.text.toString()
                )
                firebaseDocLateNight.collection("late_night_time_slots").document().set(outletF)
            }
            if (binding.checkBoxN3.isChecked) {
                val outletF = hashMapOf(
                    "time_slot" to binding.checkBoxN3.text.toString()
                )
                firebaseDocLateNight.collection("late_night_time_slots").document().set(outletF)
            }
            if (binding.checkBoxN4.isChecked) {
                val outletF = hashMapOf(
                    "time_slot" to binding.checkBoxN4.text.toString()
                )
                firebaseDocLateNight.collection("late_night_time_slots").document().set(outletF)
            }
            if (binding.checkBoxN5.isChecked) {
                val outletF = hashMapOf(
                    "time_slot" to binding.checkBoxN5.text.toString()
                )
                firebaseDocLateNight.collection("late_night_time_slots").document().set(outletF)
            }

            val firebaseDocClosedDays = fireBase.collection("time_slots").document("closed_days")
            if (binding.checkBoxC1.isChecked) {
                val outletF = hashMapOf(
                    "time_slot" to binding.checkBoxC1.text.toString()
                )
                firebaseDocClosedDays.collection("closed_days_time_slots").document().set(outletF)
            }
            if (binding.checkBoxC2.isChecked) {
                val outletF = hashMapOf(
                    "time_slot" to binding.checkBoxC2.text.toString()
                )
                firebaseDocLateNight.collection("closed_days_time_slots").document().set(outletF)
            }
            if (binding.checkBoxC3.isChecked) {
                val outletF = hashMapOf(
                    "time_slot" to binding.checkBoxC3.text.toString()
                )
                firebaseDocLateNight.collection("closed_days_time_slots").document().set(outletF)
            }
            if (binding.checkBoxC4.isChecked) {
                val outletF = hashMapOf(
                    "time_slot" to binding.checkBoxC4.text.toString()
                )
                firebaseDocLateNight.collection("closed_days_time_slots").document().set(outletF)
            }
            if (binding.checkBoxC5.isChecked) {
                val outletF = hashMapOf(
                    "time_slot" to binding.checkBoxC5.text.toString()
                )
                firebaseDocLateNight.collection("closed_days_time_slots").document().set(outletF)
            }
            if (binding.checkBoxC6.isChecked) {
                val outletF = hashMapOf(
                    "time_slot" to binding.checkBoxC6.text.toString()
                )
                firebaseDocLateNight.collection("closed_days_time_slots").document().set(outletF)
            }

            Toast.makeText(requireContext(), "Your data saved", Toast.LENGTH_SHORT).show()
            binding.btnSaveOutletDays.visibility = View.VISIBLE
            binding.progressOutletTimeSlot.visibility = View.INVISIBLE

        }

    }

}