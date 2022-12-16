package com.hofit.hofitclient.ui.fragment.registration_outlet_fragment

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.adapters.TextViewBindingAdapter.setText
import androidx.fragment.app.Fragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.hofit.hofitclient.databinding.FragmentOutletDetailsBinding

class OutletDetails : Fragment() {

    private lateinit var binding: FragmentOutletDetailsBinding
    private lateinit var auth: String
    private lateinit var fireBase: DocumentReference
    private lateinit var mStorageReference: StorageReference

    private var imageURI: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOutletDetailsBinding.inflate(inflater, container, false)

        //Firebase auth and firestore instance
        auth = Firebase.auth.currentUser!!.uid
        fireBase = Firebase.firestore
            .collection("super_admin")
            .document("rohit-20072022")
            .collection("sports_centers").document(auth)
        mStorageReference =
            Firebase.storage.reference.child("outlet/").child(auth).child("outlet_images/")

        return binding.root;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getDataFromFireStore()

        val mGetImageUri = registerForActivityResult(ActivityResultContracts.GetContent()) {
            try {
                imageURI = it
                val imageUriName = getFileName(requireContext(), imageURI!!)
                binding.edOutletImageName.editText?.setText(imageUriName)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Document not selected", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        binding.btnOutletImageUpload.setOnClickListener {
            mGetImageUri.launch("image/*")
        }

        //Save button listener
        binding.btnSaveOutletDetails.setOnClickListener {
            binding.btnSaveOutletDetails.visibility = View.INVISIBLE
            binding.progressOutletDetails.visibility = View.VISIBLE
            saveOutLetDetails()
        }
    }

    //Function for save outlet
    private fun saveOutLetDetails() {
        val outletName = binding.edOutLetName1.text.toString().trim()
        val ownerName = binding.edOutLetOwnerName1.text.toString().trim()
        val ownerWhatsAppNumber = binding.edOutLetOwnerWAMNumber1.text.toString().trim()
        val outletAddress = binding.edOutLetAddress1.text.toString().trim()
        val outletCity = binding.edOutLetCity1.text.toString().trim()
        val outletState = binding.edOutLetState1.text.toString().trim()
        val outletPinCode = binding.edOutLetPinCode1.text.toString().trim()
        val outletFrontImage = binding.edOutletImageName1.text.toString().trim()

//        val fullAddress =
//            "$outletAddress, $outletCity, $outletState ($outletPinCode)"

        val regexStr =
            "(0/91)?[7-9][0-9]{9}".toRegex()

        val checkPinCode = "^\\d{6}\$".toRegex()

        if (outletName.isNotEmpty() && ownerName.isNotEmpty()
            && ownerWhatsAppNumber.isNotEmpty()
            && outletAddress.isNotEmpty() && outletCity.isNotEmpty()
            && outletState.isNotEmpty() && outletPinCode.isNotEmpty()
            && outletFrontImage.isNotEmpty()
        ) {

            if (ownerWhatsAppNumber.matches(regexStr)) {

                if (outletPinCode.matches(checkPinCode)) {

                    uploadData(
                        imageURI,
                        ownerName,
                        outletName,
                        outletAddress,
                        outletCity,
                        outletState,
                        outletPinCode,
                        ownerWhatsAppNumber
                    )

                } else {
                    binding.btnSaveOutletDetails.visibility = View.VISIBLE
                    binding.progressOutletDetails.visibility = View.INVISIBLE
                    Toast.makeText(context, "Enter valid pin-code", Toast.LENGTH_SHORT).show()
                }

            } else {
                binding.btnSaveOutletDetails.visibility = View.VISIBLE
                binding.progressOutletDetails.visibility = View.INVISIBLE
                Toast.makeText(context, "Enter valid mobile number", Toast.LENGTH_SHORT).show()
            }

        } else {
            binding.btnSaveOutletDetails.visibility = View.VISIBLE
            binding.progressOutletDetails.visibility = View.INVISIBLE
            Toast.makeText(context, "Enter the all require details", Toast.LENGTH_SHORT).show()
        }

    }

    private fun uploadData(
        imageURI: Uri?,
        ownerName: String,
        outletName: String,
        outletAddress: String,
        outletCity: String,
        outletState: String,
        outletPinCode: String,
        ownerWhatsAppNumber: String
    ) {
        mStorageReference = mStorageReference.child(System.currentTimeMillis().toString())
        mStorageReference.putFile(imageURI!!)
            .addOnCompleteListener { taskSnapshot ->
                if (taskSnapshot.isSuccessful) {
                    mStorageReference.downloadUrl.addOnSuccessListener { uri ->
                        val outletRegis = hashMapOf(
                            "owner_id" to auth,
                            "outlet_id" to auth,
                            "owner_name" to ownerName,
                            "outlet_name" to outletName,
                            "outlet_wa_number" to ownerWhatsAppNumber,
                            "outlet_image" to uri.toString(),
                            "outlet_address" to outletAddress,
                            "outlet_city" to outletCity,
                            "outlet_state" to outletState,
                            "outlet_pincode" to outletPinCode
                        )
                        fireBase
                            .set(outletRegis, SetOptions.merge())
                            .addOnSuccessListener {
                                clearED()
                                binding.btnSaveOutletDetails.visibility = View.VISIBLE
                                binding.progressOutletDetails.visibility = View.INVISIBLE
                                Toast.makeText(requireContext(), "Data saved", Toast.LENGTH_SHORT)
                                    .show()
                            }
                            .addOnFailureListener {}
                    }
                }
            }
            .addOnFailureListener {}
    }

    private fun clearED() {
        binding.edOutletImageName1.setText("")
    }

    @SuppressLint("Range")
    private fun getFileName(context: Context, uri: Uri): String {

        if (uri.scheme == "content") {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor.use {
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        return cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                    }
                }
            }
        }

        return uri.path!!.substring(uri.path!!.lastIndexOf('/') + 1)
    }

    private fun getDataFromFireStore() {
        fireBase.get()
            .addOnSuccessListener { data ->
                val outName = data.getString("outlet_name").toString()
                binding.edOutLetName1.setText(outName)
                val owName = data.getString("owner_name").toString()
                binding.edOutLetOwnerName1.setText(owName)
                val outWN = data.getString("outlet_wa_number").toString()
                binding.edOutLetOwnerWAMNumber1.setText(outWN)
                val outAdd = data.getString("outlet_address").toString()
                binding.edOutLetAddress1.setText(outAdd)
                val outCity = data.getString("outlet_city").toString()
                binding.edOutLetCity1.setText(outCity)
                val outState = data.getString("outlet_state").toString()
                binding.edOutLetState1.setText(outState)
                val outPinCode = data.getString("outlet_pincode").toString()
                binding.edOutLetPinCode1.setText(outPinCode)
            }
    }

}