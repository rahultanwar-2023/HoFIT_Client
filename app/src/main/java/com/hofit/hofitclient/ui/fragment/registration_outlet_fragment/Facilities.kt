package com.hofit.hofitclient.ui.fragment.registration_outlet_fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.hofit.hofitclient.R
import com.hofit.hofitclient.databinding.FragmentFacilitiesBinding
import kotlinx.coroutines.NonDisposableHandle.parent

class Facilities : Fragment() {

    private lateinit var binding: FragmentFacilitiesBinding
    private lateinit var auth : String
    private lateinit var fireBase: DocumentReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFacilitiesBinding.inflate(inflater,container,false);
        auth = Firebase.auth.currentUser!!.uid
        fireBase = Firebase.firestore
            .collection("super_admin")
            .document("rohit-20072022")
            .collection("sports_centers").document(auth)
        return binding.root;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val alarmTypeAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.centerCategorySpinnerEntries, R.layout.spinner_text
        )
        binding.autoCompleteEDText.setAdapter(alarmTypeAdapter)

        binding.btnSaveOutletFacilities.setOnClickListener {

            binding.btnSaveOutletFacilities.visibility = View.INVISIBLE
            binding.progressOutletFacilities.visibility = View.VISIBLE

            val outletCategory = hashMapOf(
                "outlet_category" to binding.autoCompleteEDText.text.toString().trim()
            )
            fireBase.set(outletCategory, SetOptions.merge())

            val firebaseDoc = fireBase.collection("facilities")
            if (binding.checkBoxA.isChecked) {
                val outletF = hashMapOf(
                    "outlet_f" to binding.checkBoxA.text.toString()
                )
                firebaseDoc.document().set(outletF)
            }
            if (binding.checkBoxL.isChecked) {
                val outletF = hashMapOf(
                    "outlet_f" to binding.checkBoxL.text.toString()
                )
                firebaseDoc.document().set(outletF)
            }
            if (binding.checkBoxW.isChecked) {
                val outletF = hashMapOf(
                    "outlet_f" to binding.checkBoxW.text.toString()
                )
                firebaseDoc.document().set(outletF)
            }
            if (binding.checkBoxS.isChecked) {
                val outletF = hashMapOf(
                    "outlet_f" to binding.checkBoxS.text.toString()
                )
                firebaseDoc.document().set(outletF)
            }
            if (binding.checkBoxC.isChecked) {
                val outletF = hashMapOf(
                    "outlet_f" to binding.checkBoxC.text.toString()
                )
                firebaseDoc.document().set(outletF)
            }
            if (binding.checkBoxD.isChecked) {
                val outletF = hashMapOf(
                    "outlet_f" to binding.checkBoxD.text.toString()
                )
                firebaseDoc.document().set(outletF)
            }
            Toast.makeText(requireContext(), "Your data saved", Toast.LENGTH_SHORT).show()

            binding.progressOutletFacilities.visibility = View.INVISIBLE
            binding.btnSaveOutletFacilities.visibility = View.VISIBLE
        }

    }

}